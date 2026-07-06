package com.odcchina.juc.conditionThread;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatchDemo 一个线程等待多个线程
 * 田径运动会上，终点处，计时裁判需要等待所有运动员到达终点，才能宣布本次比赛结束。
 */
public class CountDownLatchDemo1WaitN {
    public static void main(String[] args) {
        // 设置一个计数器 为 6
        CountDownLatch latch = new CountDownLatch(6);

        // 开启6个线程,来模拟6个运动员
        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                try {
                    // 生成 5s 以内的随机数,这里仅仅只是让打印更生动.
                    Thread.sleep(new Random().nextInt(5) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + "达到终点~");

                // 计数器-1
                latch.countDown();
            }, i + " 号运动员").start();
        }

        try {
            // 主线程在这里阻塞,当latch的计数器减为0,才会被唤醒,继续执行.
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("所有运动员达到,裁判宣布比赛结束~");
    }
}