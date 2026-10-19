package com.perfect.client.core.utils.range.utils;

import com.perfect.client.core.utils.math.SecureRandomUtils;
import com.perfect.client.core.utils.range.IntRange;

import java.util.function.Supplier;

public class IntRangeGenerator extends AbstractRangeGenerator<Integer, IntRange> {

	public IntRangeGenerator(Supplier<IntRange> rangeSupplier) {
		super(rangeSupplier);
	}

	@Override
	protected Integer generateNextValue() {
		return SecureRandomUtils.random(getRange());
	}
}
