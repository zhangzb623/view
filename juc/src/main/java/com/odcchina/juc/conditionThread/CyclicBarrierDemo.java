package com.odcchina.juc.conditionThread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier: 集齐7颗龙珠,召唤神龙
 * 每执行 CyclicBarrier 一次障碍数会加一，如果达到了目标障碍数，才会执行目标方法。
 * 可以将 CyclicBarrier 理解为加一计数器。
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        cyclicBarrierTest();
    }

    public static void cyclicBarrierTest() {
        /**
         * CyclicBarrier: int parties：目标障碍数
         * Runnable barrierAction：达到目标障碍数后,需要执行的方法.
         */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {
            System.out.println("====召唤神龙=====");
        });
        for (int i = 1; i <= 7; i++) {
            final int tempInt = i;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t收集到第" + tempInt + "颗龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, "" + i).start();
        }
    }
}