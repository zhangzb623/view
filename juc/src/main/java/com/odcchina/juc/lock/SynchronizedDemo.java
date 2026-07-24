package com.odcchina.juc.lock;


/**
 * 演示 synchronized 的可重入性，和之前的 ReentrantLock 例子本质上是一回事，但用的是 Java 内置的 synchronized 关键字。
 */
public class SynchronizedDemo {
    public static void main(String[] args) {
        Phone phone = new Phone();
        new Thread(() -> {
            try {
                phone.sendSMS();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Thread 1").start();
        new Thread(() -> {
            try {
                phone.sendSMS();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Thread 2").start();
    }
}
class Phone{
    public synchronized void sendSMS()throws Exception{
        // 线程获取了 Phone 对象的锁
        System.out.println(Thread.currentThread().getName()+"\t -----invoked sendSMS()");
        Thread.sleep(3000);
        sendEmail();
    }

    public synchronized void sendEmail() throws Exception{
        // 同一个线程再次获取同一个对象的锁（可重入！）
        System.out.println(Thread.currentThread().getName()+"\t +++++invoked sendEmail()");
    }
}