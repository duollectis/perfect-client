package com.perfect.client.core.utils;

import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RegistryUtils {

	public static <T> List<T> getFiltered(Registry<T> registry, Predicate<T> filter) {
		List<T> list = new ArrayList<>();

		registry.forEach(key -> {
			if (filter == null || filter.test(key)) {
				list.add(key);
			}
		});

		return list;
	}
}
