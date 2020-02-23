package com.capgroup.dcip.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RangeMerger<T extends Comparable<? super T>, K extends Range<T>> {

    RangeFactory<T, K> factory;

    public RangeMerger(RangeFactory<T, K> factory) {
        this.factory = factory;
    }

    private TreeMap<T, Moment> moments(Stream<K> lhs) {
        // create a mapping of event date (start/end) to the start/end count
        TreeMap<T, Moment> result = lhs.map(x ->
                (x.getStart() == x.getEnd())
                        ? Stream.of(new AbstractMap.SimpleEntry<>(x.getStart(),
                        new Moment(0, 0, 1)))
                        : Stream.of(
                        new AbstractMap.SimpleEntry<>(x.getStart(),
                                new Moment(1, 0, 1)),
                        new AbstractMap.SimpleEntry<>(x.getEnd(),
                                new Moment(0, 1, 0)))
        ).flatMap(Function.identity()).collect(Collectors.groupingBy(y -> y.getKey(), TreeMap::new,
                Collectors.reducing(new Moment(), x -> x.getValue(), (a, b) -> a.add(b))));
        return result;
    }

    /**
     * Merges date ranges together
     */
    public Stream<K> merge(Stream<K> input) {
        TreeMap<T, Moment> data = moments(input);

        if (data.isEmpty()) {
            return Stream.empty();
        }

        Stream.Builder<K> result = Stream.builder();

        ArrayList<Map.Entry<T, Moment>> iter = new ArrayList<>(data.entrySet());
        int index = 0;
        while (index < iter.size()) {
            Map.Entry<T, Moment> start, current;
            start = current = iter.get(index);
            int startCount = start.getValue().getStartCount();

            int balance = startCount;
            while (index < iter.size() - 1 && (balance > 0 || (balance == 0 && start.getValue().getEventCount() > 0
                    && start.getValue().getStartCount() > 0))) {
                ++index;
                current = iter.get(index);
                balance += current.getValue().getBalance();
            }

            if (current.getValue().getStartCount() > 0 && current.getValue().getEventCount() > 0) {
                ++index;
                continue;
            }

            if (index < iter.size()) {
                result.add(factory.create(start.getKey(), current.getKey()));
            }
            ++index;
        }

        return result.build();
    }

    @FunctionalInterface
    public interface RangeFactory<T extends Comparable<? super T>, K extends Range<T>> {
        K create(T start, T end);
    }

    @Data
    @NoArgsConstructor
    private static class Moment {
        private int startCount;
        private int endCount;
        private int eventCount;

        public Moment(int startCount, int endCount, int eventCount) {
            this.startCount = startCount;
            this.endCount = endCount;
            this.eventCount = eventCount;
        }

        public int getBalance() {
            return startCount - endCount;
        }

        public Moment add(Moment rhs) {
            return new Moment(startCount + rhs.startCount, endCount + rhs.endCount,
                    eventCount + rhs.eventCount);
        }
    }

}
