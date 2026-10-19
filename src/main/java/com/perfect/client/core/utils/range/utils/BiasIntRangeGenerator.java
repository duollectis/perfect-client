package com.perfect.client.core.utils.range.utils;

import com.perfect.client.core.utils.math.SecureRandomUtils;
import com.perfect.client.core.utils.range.IntRange;

import java.util.function.Supplier;

public class BiasIntRangeGenerator extends AbstractRangeGenerator<Integer, IntRange> {

	private final Supplier<Double> biasSupplier;

	public BiasIntRangeGenerator(Supplier<IntRange> rangeSupplier, Supplier<Double> biasSupplier) {
		super(rangeSupplier);
		this.biasSupplier = biasSupplier;

		update();
	}

	@Override
	protected Integer generateNextValue() {
		if (biasSupplier == null) {
			return 0;
		}

		int min = getRange().getFrom();
		int max = getRange().getTo();

		double uniform = SecureRandomUtils.random(0.0, 1.0);
		double biased = Math.pow(uniform, 1.0 / biasSupplier.get());

		return min + Math.clamp((int) Math.round(biased * (max - min)), min, max);
	}
}
