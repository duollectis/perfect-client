package com.perfect.client.core.utils.math;

import com.perfect.client.core.utils.range.DoubleRange;
import com.perfect.client.core.utils.range.IntRange;

import java.security.SecureRandom;

public class SecureRandomUtils {

	private static final SecureRandom random = new SecureRandom();

	public static boolean random() {
		return random.nextBoolean();
	}

	public static float random(float from, float to) {
		float min = Math.min(from, to);
		float max = Math.max(from, to);

		return min + random.nextFloat() * (max - min);
	}

	public static double random(DoubleRange range) {
		return random(range.getFrom(), range.getTo());
	}

	public static double random(double from, double to) {
		double min = Math.min(from, to);
		double max = Math.max(from, to);

		return min + random.nextDouble() * (max - min);
	}

	public static int random(IntRange range) {
		return random(range.getFrom(), range.getTo());
	}

	public static int random(int from, int to) {
		int min = Math.min(from, to);
		int max = Math.max(from, to);

		return random.nextInt(max - min + 1) + min;
	}

	public static int randomSign() {
		return random() ? -1 : 1;
	}
}
