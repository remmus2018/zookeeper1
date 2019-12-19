package com.bolingcavalry.service;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;


/**
 * zk 分布式锁
 */
public class DistributedLock implements Lock,Watcher {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

    private ZooKeeper zooKeeper = null;

    //定义根节点
    private String ROOT_LOCK = "/locks";

    //表示等待前一个锁
    private String WAIT_LOCK;

    //表示当前锁
    private String CURRENT_LOCK;

    private CountDownLatch countDownLatch;

    public DistributedLock(){
        try {
            zooKeeper = new ZooKeeper("127.0.0.1",500000,this);
            //为false 不去注册
            Stat stat = zooKeeper.exists(ROOT_LOCK, false);
            String data = "11111";
            if(stat == null){
                zooKeeper.create(ROOT_LOCK, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lock() {

        if(tryLock()){
            logger.debug(Thread.currentThread().getName()+"--->"+ CURRENT_LOCK + "获得锁成功。");
            return;
        }

        try {
            waitForLock(WAIT_LOCK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 持续阻塞获得锁的过程
     * @param prev 当前节点的前一个等待节点
     * @return
     */
    public boolean waitForLock(String prev) throws  Exception{
        //等待锁需要监听上一个节点，每一个有序节点都去监听它的上一个节点 watch设置为true
        Stat stat = zooKeeper.exists(prev,true);
        if(stat != null){
            //如果上一个节点存在的话
            logger.debug(Thread.currentThread().getName()+"--->等待锁"+ ROOT_LOCK + "/" + prev + "释放。");
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await();
            logger.debug(Thread.currentThread().getName()+"--->等待后获得锁成功。");
        }
        return true;
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        String data = "11111";
        try {
            //创建临时有序节点(自增)--当前锁
            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/",data.getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug(Thread.currentThread().getName()+"---->"+ CURRENT_LOCK + "尝试竞争锁！");

            //获取根节点下的所有子节点，不注册watch监听
            List<String> children = zooKeeper.getChildren(ROOT_LOCK,false);
            SortedSet<String> sortedSet = new TreeSet();
            children.forEach(child->{
                sortedSet.add(ROOT_LOCK+"/"+child);
            });
            //获取当前子节点中最小的节点
            String firstNode = sortedSet.first();
            if(StringUtils.equals(firstNode,CURRENT_LOCK)){
                //将当前节点和最小节点进行比较，如果相等，则获得锁成功
                return true;
            }
            //返回比CURRENT_LOCK小的子节点数
            SortedSet<String> lessThenMe = sortedSet.headSet(CURRENT_LOCK);

            //如果当前所有节点中有比自己更小的节点
            if (lessThenMe.size() > 0){
                //获取比自己小的节点中的最后一个节点，设置为等待锁(获得自己节点的上一个节点用于监听)
                WAIT_LOCK = lessThenMe.last();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static void main(String[] args) {
//        SortedSet<String> sortedSet = new TreeSet();
//        sortedSet.add("1");
//        sortedSet.add("3");
//        sortedSet.add("2");
//        sortedSet.add("4");
//        SortedSet<String> sortedSet1 = sortedSet.headSet("3");
//        String a = sortedSet1.last();
//        System.out.println("sortedSet====="+sortedSet.toString());
//        System.out.println("sortedSet1==="+sortedSet1.toString());
//        System.out.println("a==="+a);
//    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        logger.debug(Thread.currentThread().getName()+"---->释放锁"+CURRENT_LOCK);
        try {
            //强制删除，释放锁
            zooKeeper.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK = null;
            zooKeeper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(this.countDownLatch != null){
            //不为空，说明存在监听
            this.countDownLatch.countDown();
        }

    }
}
