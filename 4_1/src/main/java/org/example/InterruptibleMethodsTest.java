package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InterruptibleMethodsTest {
    public static void main(String[] args) {
        // Thread.sleep
        testInterruptible(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep was interrupted");
            }
        });

        // ReentrantLock.lock
        testLockInterruptible();

        // Synchronized block
        testInterruptible(() -> {
            synchronized (InterruptibleMethodsTest.class) {
                System.out.println("Synchronized block");
            }
        });

        // Condition.await
        testInterruptible(() -> {
            Lock conditionLock = new ReentrantLock();
            Condition condition = conditionLock.newCondition();
            conditionLock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                System.out.println("Condition.await was interrupted");
            } finally {
                conditionLock.unlock();
            }
        });

        // Object.wait
        testInterruptible(() -> {
            Object x = new Object();
            synchronized (x) {
                try {
                    x.wait();
                } catch (InterruptedException e) {
                    System.out.println("Object.wait was interrupted");
                }
            }
        });

        // FileWriter.write
        testInterruptible(() -> {
            try (FileWriter writer = new FileWriter("test.txt")) {
                for (int i = 0; i < 10; i++) {
                    writer.write("some text");
                }
            } catch (IOException e) {
                System.out.println("FileWriter.write: IOException");
            }
        });

        // System.in.read
        testInterruptible(() -> {
            try {
                int data = System.in.read();
            } catch (IOException e) {
                System.out.println("System.in.read: IOException");
            }
        });
    }

    private static void testInterruptible(Runnable task) {
        CountDownLatch started = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            started.countDown();
            task.run();
        });

        thread.start();

        try {
            started.await();
            thread.interrupt();
        } catch (InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void testLockInterruptible() {
        Lock lock = new ReentrantLock();
        CountDownLatch lockerStarted = new CountDownLatch(1);
        CountDownLatch lockAttempted = new CountDownLatch(1);

        Thread locker = new Thread(() -> {
            lock.lock();
            lockerStarted.countDown();
            try {
                Thread.sleep(900);
            } catch (InterruptedException ignored) {
            } finally {
                lock.unlock();
            }
        });

        Thread testThread = new Thread(() -> {
            try {
                lockerStarted.await(); // ждём, пока locker захватит lock
                lock.lock();
                lockAttempted.countDown(); // сообщаем, что заблокировались
            } catch (InterruptedException e) {
                System.out.println("ReentrantLock.lock was interrupted");
            } finally {
                if (lock.tryLock()) {
                    lock.unlock();
                }
            }
        });

        locker.start();
        testThread.start();

        try {
            lockAttempted.await(); // ждём, пока testThread заблокируется
            testThread.interrupt();
        } catch (InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
