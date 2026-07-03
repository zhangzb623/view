package com.odcchina.juc.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1.CAS是什么？
 * 1.1比较并交换
 */
public class CasDemo {
    public static void main(String[] args) {
        checkCas();
    }

    public static void checkCas() {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        System.out.println(atomicInteger.compareAndSet(5, 2019) + "\t current data:" + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(5, 1024) + "\t current data:" + atomicInteger.get());
    }
}
