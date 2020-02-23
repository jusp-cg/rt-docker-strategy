package com.capgroup.dcip.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class that chunks a collection of items into smaller chunks
 */
public class BatchUtils {

    public static int DEFAULT_BATCH_SIZE = 50;

    public static <T> Stream<List<T>> chunk(List<T> source, int length) {
        if (source.size() <= 0) {
            return Stream.empty();
        }

        int fullChunks = (source.size() - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(x -> source.subList(x * length, x == fullChunks ?
                source.size() : (x + 1) * length));
    }

    public static <T, U, C extends Collection<U>> Stream<U> execute(Function<List<T>, C> executor, List<T> items) {
        return execute(executor, items, DEFAULT_BATCH_SIZE);
    }

    public static <T, U, C extends Collection<U>> Stream<U> execute(Function<List<T>, C> executor, List<T> items,
                                                                    int batchSize) {
        return chunk(items, batchSize).flatMap(x -> executor.apply(x).stream());
    }

    public static <T> void execute(Consumer<List<T>> executor, List<T> items, int batchSize) {
        chunk(items, batchSize).forEach(x -> executor.accept(x));
    }
}
