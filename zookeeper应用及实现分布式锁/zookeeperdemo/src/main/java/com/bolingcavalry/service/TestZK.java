package com.bolingcavalry.service;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

public class TestZK {
    public static void main(String[] args) throws Exception {
//        ZKOperateAPI zkOperateAPI = new ZKOperateAPI();
//        zkOperateAPI.connect("127.0.0.1:2181");
//        zkOperateAPI.create("testAPI/testAPI1.1/testAPI1.1.85", "abc");
//        zkOperateAPI.close();
//        ZKOperateAPI operateAPI = new ZKOperateAPI();
//        operateAPI.connect("127.0.0.1");
//        operateAPI.list("testAPI");
//        operateAPI.close();
        ZKOperateAPI operateAPI = new ZKOperateAPI();
        operateAPI.connect("127.0.0.1:2181");
        operateAPI.delete("testAPI/testAPI1.1/testAPI1.1.85");
        operateAPI.close();

       /* zooKeeper.exists("/test1", true);
        zooKeeper.create("/test1", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        //重新设置watch，zookeeper中watch被调用之后需要重新设置
        zooKeeper.exists("/test1", true);
        zooKeeper.delete("/test1", -1);*/
    }
}
