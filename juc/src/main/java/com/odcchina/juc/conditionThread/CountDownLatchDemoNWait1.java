package com.odcchina.juc.conditionThread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatchDemo 多个线程等1个线程
 * 模拟6个运动员,等1个裁判员发枪
 * 田径运动会上，起跑前所有运动员等待裁判发枪声为准开始比赛。
 */
public class CountDownLatchDemoNWait1 {
    public static void main(String[] args) {
        // 设置一个计数器 为 1
        CountDownLatch latch = new CountDownLatch(1);

        // 开启6个线程,来模拟6个运动员
        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + "起跑~");

            }, i + " 号运动员").start();
        }

        System.out.println("裁判发出枪声,比赛开始~");
        // 计数器减1变为0.因await方法阻塞的线程会被唤醒,继续执行.
        latch.countDown();
    }
}