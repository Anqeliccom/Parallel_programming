package org.example;

// Thread C joins thread B after exception happened.
public class Case2 {
    public static void main(String[] args) {
        Thread B = new Thread(() -> {
            System.out.println("Thread B: started");
            throw new RuntimeException("Thread B: exception!");
        });

        Thread C = new Thread(() -> {
            System.out.println("Thread C: started");
            try {
                B.join();
            } catch (InterruptedException e) {
                System.out.println("Thread C: interrupted");
            }
            System.out.println("Thread C: finished");
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

        C.start();
        try {
            C.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread: interrupted");
        }
    }
}

/*
 Thread B also completes abnormally, but this does not prevent A and C from continuing execution.
 Thread C immediately exits join() because B has already completed.

    > Task :org.example.Case2.main()
    Thread A: started
    Thread B: started
    Thread A: 1
    Thread A: 2
    Thread A: 3
    Thread A: B finished
    Thread C: started
    Thread C: finished
 */