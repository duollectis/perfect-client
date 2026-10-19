package com.perfect.client.core.utils.math;

import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Random;


public class MathUtils {

	private static final Random random = new Random(573428067829056L);

	public static boolean randomBoolean() {
		return random.nextBoolean();
	}

	public static float random(float from, float to) {
		float min = Math.min(from, to);
		float max = Math.max(from, to);

		return min + random.nextFloat() * (max - min);
	}

	public static double random(double from, double to) {
		double min = Math.min(from, to);
		double max = Math.max(from, to);

		return min + random.nextDouble() * (max - min);
	}

	public static int random(int from, int to) {
		int min = Math.min(from, to);
		int max = Math.max(from, to);

		return random.nextInt(max - min + 1) + min;
	}

	public static double offset(double range) {
		double val = random.nextDouble() * range;
		return randomBoolean() ? val : -val;
	}

	public static float offset(float range) {
		float val = random.nextFloat() * range;
		return randomBoolean() ? val : -val;
	}

	public static int square(int value) {
		return value * value;
	}

	public static float square(float value) {
		return value * value;
	}

	public static double square(double value) {
		return value * value;
	}

	public static double roundTo(double value, double decimalPlaces) {
		double pow = Math.pow(10, decimalPlaces);
		return Math.round(value * pow) / pow;
	}

	public static boolean isNear(float a, float b) {
		return Math.abs(a - b) <= 0.0005;
	}

	//————————————————————————COMPARE——————————————————————————//

	public static float absMin(float a, float b) {
		if (Math.abs(a) < Math.abs(b)) {
			return a;
		}
		else {
			return b;
		}
	}

	public static double absMin(double a, double b) {
		if (Math.abs(a) < Math.abs(b)) {
			return a;
		}
		else {
			return b;
		}
	}

	//————————————————————————INTERPOLATION——————————————————————————//

	public static Color interpolateColor(Color start, Color end, double progress) {
		if (start == null) {
			return end;
		}
		if (start.equals(end)) {
			return end;
		}

		progress = Math.min(1, Math.max(0, progress));

		return new Color(
				interpolateInt(start.getRed(), end.getRed(), progress),
				interpolateInt(start.getGreen(), end.getGreen(), progress),
				interpolateInt(start.getBlue(), end.getBlue(), progress),
				interpolateInt(start.getAlpha(), end.getAlpha(), progress)
		);
	}

	public static int interpolateInt(int oldValue, int newValue, double progress) {
		return (int) interpolate(oldValue, newValue, progress);
	}

	public static float interpolateFloat(float oldValue, float newValue, double progress) {
		return (float) interpolate(oldValue, newValue, progress);
	}

	public static Vec3d interpolateVec3d(Vec3d oldValue, Vec3d newValue, double progress) {
		if (oldValue == null) {
			return newValue;
		}

		double x = interpolate(oldValue.getX(), newValue.getX(), progress);
		double y = interpolate(oldValue.getY(), newValue.getY(), progress);
		double z = interpolate(oldValue.getZ(), newValue.getZ(), progress);
		return new Vec3d(x, y, z);
	}

	public static double interpolate(double oldValue, double newValue, double progress) {
		return oldValue + (newValue - oldValue) * progress;
	}
}
