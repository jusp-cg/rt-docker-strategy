package com.capgroup.dcip.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility methods for Collections 
 */
public class CollectionUtils {
	public static <T> List<T> asList(final Iterable<T> iterable) {
	    return StreamSupport.stream(iterable.spliterator(), false)
	                        .collect(Collectors.toList());
	}

	public static <T> Set<T> asSet(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false)
				.collect(Collectors.toSet());
	}
}
