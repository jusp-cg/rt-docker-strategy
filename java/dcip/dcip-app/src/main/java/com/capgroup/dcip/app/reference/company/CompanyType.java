package com.capgroup.dcip.app.reference.company;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Stream;

public enum CompanyType {
	PUBLIC(1), PUBLIC_TO_PRIVATE(2), PRIVATE(4);

	private int value;

	CompanyType(int i) {
		this.value = i;
	}

	int value() {
		return value;
	}

	public static int toValue(EnumSet<CompanyType> values) {
		return Stream.of(CompanyType.values()).map(x -> x.value).reduce((lhs, rhs) -> lhs + rhs).orElse(0);
	}

	public static Optional<CompanyType> fromValue(int i) {
		return Stream.of(CompanyType.values()).filter(x -> x.value == i).findAny();
	}
};
