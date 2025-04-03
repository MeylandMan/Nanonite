package Mycraft.Core;

import java.util.concurrent.*;

import static org.joml.Math.min;

public class MultiThreading {
    public static final int CHUNK_THREADS = min(Client.MAX_THREADS, 4);
    private static final ExecutorService executor = Executors.newFixedThreadPool(Client.MAX_THREADS);
    private static final ExecutorService chunksExecutor = Executors.newFixedThreadPool(CHUNK_THREADS);

    // Submite a Runnable task (without callback)
    public static void executeTask(Runnable task) {
        executor.execute(task);
    }

    // Submit a Callable task (with callback)
    public static <T> Future<T> submitTask(Callable<T> task) {
        return executor.submit(task);
    }
    public static <T> Future<T> submitChunkTask(Callable<T> task) {
        return chunksExecutor.submit(task);
    }

    // Stop the threads
    public static void shutdown() {
        chunksExecutor.shutdown();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }

            if (!chunksExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                chunksExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            chunksExecutor.shutdownNow();
        }
    }
}