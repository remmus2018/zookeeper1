package com.bolingcavalry.service;

import java.util.concurrent.*;

public class ExecutorTestLock {

//    static Integer ALL = 10;

//    int corePoolSize, // 线程池长期维持的线程数，即使线程处于Idle状态，也不会回收。
////    int maximumPoolSize, // 线程数的上限
////    long keepAliveTime, TimeUnit unit, // 超过corePoolSize的线程的idle时长，
////    // 超过这个时间，多余的线程会被回收。
////    BlockingQueue<Runnable> workQueue, // 任务的排队队列

    static ExecutorService executorService = new ThreadPoolExecutor(10,10,10,TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    public static void main(String[] args) throws Exception {

//        for (int j = 0; j <= 10 ; j++) {
//            CountDownLatch countDownLatch = new CountDownLatch(10);
//            for (int i = 1; i <= 10; i++) {
//                new Thread(() -> {
//                    try {
//                        countDownLatch.await();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    DistributedLock lock= new DistributedLock();
//                    lock.lock();
//                    System.out.println(Thread.currentThread().getName() + "卖出第" + ALL-- + "张票!");
//                    lock.unlock();
//                }, "售票员<" + i + ">").start();
//                countDownLatch.countDown();
//            }
//            Thread.sleep(1500);
//            ALL=10;
//            System.out.println("========================");
//    }

    }
}
