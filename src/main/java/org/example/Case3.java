package org.example;

// Thread D joins thread A.
public class Case3 {
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

        Thread D = new Thread(() -> {
            System.out.println("Thread D: started");
            try {
                A.join();
            } catch (InterruptedException e) {
                System.out.println("Thread D: interrupted");
            }
            System.out.println("Thread D: finished");
        });

        A.start();
        try {
            A.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread: interrupted");
        }

        D.start();
        try {
            D.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread: interrupted");
        }
    }
}

/*
 Thread D executes successfully after A completes.

    > Task :org.example.Case3.main()
    Thread A: started
    Thread B: started
    Thread A: 1
    Thread A: 2
    Thread A: 3
    Thread A: B finished
    Thread D: started
    Thread D: finished
 */