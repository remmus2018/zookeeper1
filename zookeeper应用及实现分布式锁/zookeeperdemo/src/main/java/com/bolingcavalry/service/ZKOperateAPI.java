package com.bolingcavalry.service;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

import java.util.List;

public class ZKOperateAPI extends ConnectionWatcher {

    public void create(String groupName, String data) throws Exception{

        String path = "/" + groupName;

        //创建znode节点，第一个参数为路径；第二个参数为znode内容，字节数组；
        // 第三个参数访问控制列表（简称ACL，此处使用完全开放的ACL，允许任何客户端对znode进行读写）；
        // 第四个为创建znode类型，此处是持久的（两种类型，短暂的和持久的，短暂类型会在客户端与zk服务断开连接后，被zk服务删掉，而持久的不会）
        zooKeeper.exists(path,true);
        String createPath = zooKeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("created=============" + createPath);
    }

    public void join(String groupName, String memberName, String data) throws Exception{
        String path = "/" + groupName + "/" + memberName;
        String createPath = zooKeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("Create " + createPath);
    }

    public void list(String groupName) throws Exception{
        String path = "/" + groupName;
        //第一个参数为组名，即znode路径；第二个参数是否设置观察标识，如果为true，那么一旦znode状态改变，当前对象的Watcher会被触发
        List<String> childrenList = zooKeeper.getChildren(path,true);
        if(childrenList.size() > 0){
            for (String children: childrenList) {
                System.out.println("children======" + children);
            }
        }
    }

    public void delete(String groupName) throws Exception{
        String path = "/" + groupName;
        List<String> childrenList = zooKeeper.getChildren(path,true);
        if(childrenList.size() > 0){
            for (String children: childrenList) {
                //删除方法第一个参数指定路径，第二个参数是版本号；这是一种乐观锁机制，如果指定的版本号和对应znode版本号一致才可删除；
                // 如果设置为-1，不校验可直接删除
                zooKeeper.delete(path + "/" + children,-1);
            }
        }
        zooKeeper.delete(path,-1);
    }
}
