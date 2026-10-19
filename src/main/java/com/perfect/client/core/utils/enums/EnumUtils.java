package com.perfect.client.core.utils.enums;

import java.util.Optional;

public class EnumUtils {

	public static <T extends Enum<?>> Optional<T> valueFromName(T[] values, String string) {
		for (T value : values) {
			if (value.name().equals(string)) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}
}
