package com.capgroup.dcip.util.stream;

import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Additional converters that are not defined by the standard java library 
 */
public class MoreCollectors {
	public static <T, K, U> Collector<T, ?, LinkedHashMap<K, U>> toLinkedMap(Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends U> valueMapper) {
		return Collectors.toMap(keyMapper, valueMapper, (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		}, LinkedHashMap::new);
	}

	public static <T, K, U> Collector<T, ?, TreeMap<K, U>> toTreeMap(Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends U> valueMapper) {
		return Collectors.toMap(keyMapper, valueMapper, (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		}, TreeMap::new);
	}
}
