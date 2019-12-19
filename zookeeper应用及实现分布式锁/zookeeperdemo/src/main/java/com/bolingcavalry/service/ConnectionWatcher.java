package com.bolingcavalry.service;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionWatcher implements Watcher {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionWatcher.class);

    AtomicInteger atomicInteger = new AtomicInteger();

    private static final int SESSION_TIME = 5000000;

    protected ZooKeeper zooKeeper;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void connect(String hosts) {
        try {
            zooKeeper = new ZooKeeper(hosts, SESSION_TIME, this);
            logger.debug("进入connect");
            //当一个ZooKeeper的实例被创建时，会启动一个线程连接到Zookeeper服务。
            // 由于对构造函数的调用是立即返回的，因此在使用新建的Zookeeper对象之前一定要等待其与Zookeeper服务之间的连接建立成功。
            // 使用CountDownLatch使当前线程等待，直到Zookeeper对象准备就绪
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 收到来自Server的Watcher通知后的处理。
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        // 连接状态
        Event.KeeperState keeperState = watchedEvent.getState();

        // 事件类型
        Event.EventType eventType = watchedEvent.getType();

        // 受影响的path
        String path = watchedEvent.getPath();

        //原子对象seq 记录进入process的次数
        String logPrefix = "【Watcher-" + this.atomicInteger.incrementAndGet() + "】";

        System.out.println(logPrefix + "收到Watcher通知");
        System.out.println(logPrefix + "连接状态:" + keeperState.toString());
        System.out.println(logPrefix + "事件类型:" + eventType.toString());


        //客户端与ZK建立连接后，Watcher的process方法会被调用，参数是表示该连接的事件，
        // 连接成功后调用CountDownLatch的countDown方法，计数器减为0，释放线程锁，zk对象可用
            if (keeperState == Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            //成功连接上ZK服务器
            if (eventType == Event.EventType.None) {
                System.out.println(logPrefix + "成功连接上ZK服务器");
            }
            //创建节点
            else if (Event.EventType.NodeCreated == eventType) {
                System.out.println(logPrefix + "节点创建");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //更新节点
            else if (Event.EventType.NodeDataChanged == eventType) {
                System.out.println(logPrefix + "节点数据更新");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //更新子节点
            else if (Event.EventType.NodeChildrenChanged == eventType) {
                System.out.println(logPrefix + "子节点变更");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //删除节点
            else if (Event.EventType.NodeDeleted == eventType) {
                System.out.println(logPrefix + "节点 " + path + " 被删除");
            }
        } else if (Event.KeeperState.Disconnected == keeperState) {
                System.out.println(logPrefix + "与ZK服务器断开连接");
            } else if (Event.KeeperState.AuthFailed == keeperState) {
                System.out.println(logPrefix + "权限检查失败");
            } else if (Event.KeeperState.Expired == keeperState) {
                System.out.println(logPrefix + "会话失效");
            }
    }

    public void close() throws Exception {
        zooKeeper.close();
    }
}
