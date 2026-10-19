package com.perfect.client.core.render.animation.easing;


public class Easing {

	public static final IEasing LINEAR = p -> p;

	public static final IEasing IN_QUAD = p -> p * p;

	public static final IEasing OUT_QUAD = p -> 1 - (1 - p) * (1 - p);

	public static final IEasing IN_OUT_SINE = p -> (float) (-(Math.cos(Math.PI * p) - 1) / 2);

	public static final IEasing IN_OUT_CUBIC = p -> {
		if (p < 0.5) {
			return 4 * p * p * p;
		}
		else {
			return (float) (1 - Math.pow(-2 * p + 2, 3) / 2);
		}
	};

	public static final IEasing IN_OUT_CIRCLE = p -> {
		if (p < 0.5) {
			return (float) ((1 - Math.sqrt(1 - Math.pow(2 * p, 2))) / 2);
		}
		else {
			return (float) (Math.sqrt(1 - Math.pow(-2 * p + 2, 2)) + 1) / 2;
		}
	};

	public static final IEasing IN_OUT_DOUBLE = x -> {
		if (x < 0.5) {
			return 8 * x * x * x * x;
		}
		else {
			return (float) (1 - Math.pow(-2 * x + 2, 4) / 2);
		}
	};
}
