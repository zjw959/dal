package test.future.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class DCompletableFuture {
    enum TYPE {
        SUPPLY_ASYNC_0, SUPPLY_ASYNC_1, RUN_ASYNC_0, RUN_ASYNC_1
    }

    /**
     * CompletableFuture generator
     * 
     * @param type type
     * @param supplier supplier
     * @param runnable runnable
     * @param executor executor
     * @param <U> type
     * @return the CompletableFuture
     */
    private static synchronized <U> CompletableFuture<U> createCompletableFuture(TYPE type,
            Supplier<U> supplier, Runnable runnable, Executor executor) {
        CompletableFuture<U> completableFuture = null;
        switch (type) {
            case SUPPLY_ASYNC_0:
                completableFuture = CompletableFuture.supplyAsync(supplier);
            case SUPPLY_ASYNC_1:
                if (supplier == null) {
                    System.out.println("supplier is null:SUPPLY_ASYNC_1");
                    break;
                }

                if (executor == null) {
                    System.out.println("executor is null:SUPPLY_ASYNC_1");
                    executor = Executors.newSingleThreadExecutor();
                }
                completableFuture = CompletableFuture.supplyAsync(supplier, executor);
                break;
            case RUN_ASYNC_0:
                CompletableFuture.runAsync(runnable);
                break;
            case RUN_ASYNC_1:
                if (runnable == null) {
                    System.out.println("runnable is null:RUN_ASYNC_1");
                    break;
                }

                if (executor == null) {
                    System.out.println("executor is null:RUN_ASYNC_1");
                    break;
                }
                CompletableFuture.runAsync(runnable, executor);
                break;
        }

        return completableFuture;
    }

    private static Supplier<String> stringSupplier = () -> {
        System.out.println(Thread.currentThread().getName());
        return "supply of string";
    };

    private static Runnable runnable = () -> {
        System.out.println("runnable :" + Thread.currentThread().getName());
        System.out.println("void runnable");
    };

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String... args) {


        // CompletableFuture<Void> voidCompletableFutureV1
        // = createCompletableFuture(TYPE.RUN_ASYNC_1, null, runnable, executor);
        // CompletableFuture<Void> voidCompletableFuture
        // = createCompletableFuture(TYPE.RUN_ASYNC_0, null, runnable, null);

        CompletableFuture<String> stringCompletableFutureV1 =
                createCompletableFuture(TYPE.SUPPLY_ASYNC_1, stringSupplier, null, executor);
        // CompletableFuture<String> stringCompletableFuture
        // = createCompletableFuture(TYPE.SUPPLY_ASYNC_0, stringSupplier, null, executor);

        // String result0 = stringCompletableFuture.getNow("Empty V0");
        String result1 = stringCompletableFutureV1.getNow("Empty V1");

        // System.out.println("Result:" + result0 + " , " + result1);
        System.out.println("Result: , " + result1);
        executor.shutdown();
    }

}
