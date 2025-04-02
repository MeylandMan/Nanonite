package Mycraft.Core;

import java.util.concurrent.*;

public class MultiThreading {
    private static final ExecutorService executor = Executors.newFixedThreadPool(Client.MAX_THREADS);

    // Submite a Runnable task (without callback)
    public static void executeTask(Runnable task) {
        executor.execute(task);
    }

    // Submit a Callable task (with callback)
    public static <T> Future<T> submitTask(Callable<T> task) {
        return executor.submit(task);
    }

    // Stop the threads
    public static void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}