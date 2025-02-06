package org.example;

// Thread A starts thread B and joins. Uncaught exception happens in thread B.
public class Case1 {
    public static void main(String[] args) {
        Thread B = new Thread(() -> {
            System.out.println("Thread B: started");
            throw new RuntimeException("Thread B: exception!");
        });

        Thread A = new Thread(() -> {
            System.out.println("Thread A: started");
            try {
                B.start();
                for (int i = 1; i <= 3; i++){
                    System.out.println("Thread A: " + i);
                }
                B.join();
            } catch (InterruptedException e) {
                System.out.println("Thread A: interrupted");
            }
            System.out.println("Thread A: B finished");
        });

        A.start();
        try {
            A.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread: interrupted");
        }
    }
}

/*
 Thread A waits for thread B to complete.
 Thread B completes abnormally, but A continues its work:

    > Task :org.example.Case1.main()
    Thread A: started
    Thread B: started
    Thread A: 1
    Thread A: 2
    Thread A: 3
    Thread A: B finished
 */
