package com.perfect.client.core.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public class Utils {

	public static void sleep(long delay) {
		try {
			Thread.sleep(delay);
		}
		catch (InterruptedException e) {
			LoggerUtils.warning(e);
		}
	}

	public static void safeRun(Runnable runnable) {
		try {
			runnable.run();
		}
		catch (Exception e) {
			LoggerUtils.warning(e);
		}
	}

	public static Vector3d set(Vector3d vec, Vec3d v) {
		vec.x = v.x;
		vec.y = v.y;
		vec.z = v.z;

		return vec;
	}

	public static Vector3d set(Vector3d vec, Entity entity, double tickDelta) {
		vec.x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
		vec.y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
		vec.z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

		return vec;
	}

	public static <T> void add(List<T> list, T item) {
		addIf(list, item, i -> !list.contains(i));
	}

	public static <T> void addIf(List<T> list, T item, Predicate<T> predicate) {
		if (predicate.test(item)) {
			list.add(item);
		}
	}

	public static <T> void addFirst(List<T> list, T item) {
		if (!list.contains(item)) {
			list.addFirst(item);
		}
	}

	public static <T> List<T> listOf(T item) {
		List<T> list = new ArrayList<>();
		list.add(item);
		return list;
	}

	public static <T> void keepHigherValues(
			List<T> list,
			Function<T, Double> keyExtractor
	) {
		if (list.isEmpty()) {
			return;
		}

		list.sort(Comparator.comparing(item -> -keyExtractor.apply(item)));
		double best = keyExtractor.apply(list.getFirst());

		boolean remove = false;

		Iterator<T> iterator = list.iterator();

		while (iterator.hasNext()) {
			T item = iterator.next();

			if (remove) {
				iterator.remove();
				continue;
			}

			if (keyExtractor.apply(item) < best) {
				iterator.remove();
				remove = true;
			}
		}
	}
}