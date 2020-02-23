package com.capgroup.dcip.util;

import java.util.function.Supplier;

/**
 * Threadsafe implementation of the lazy initialisation pattern 
 */
public interface Lazy {
	static <T> Supplier<T> lazy(Supplier<T> supplier) {
		return new Supplier<T>() {

			volatile T value;

			@Override
			public T get() {
				// double checked locking
				if (value == null) {
					synchronized (this) {
						if (value == null) {
							value = supplier.get();
						}
					}
				}
				return value;
			}
		};
	}
}
