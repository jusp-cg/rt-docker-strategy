package com.capgroup.dcip.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * 
 */
public interface Range<T extends Comparable<? super T>> {

	T getStart();

	T getEnd();

	default boolean intersects(Range<? extends T> rhs) {
		return getStart().compareTo(rhs.getEnd()) <= 0 && getEnd().compareTo(rhs.getStart()) >= 0;
	}

	default boolean contains(T item) {
		return getStart().compareTo(item) >= 0 && getEnd().compareTo(item) >= 0;
	}
}
