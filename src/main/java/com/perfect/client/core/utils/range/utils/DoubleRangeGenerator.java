package com.perfect.client.core.utils.range.utils;

import com.perfect.client.core.utils.math.SecureRandomUtils;
import com.perfect.client.core.utils.range.DoubleRange;

import java.util.function.Supplier;

public class DoubleRangeGenerator extends AbstractRangeGenerator<Double, DoubleRange> {

	public DoubleRangeGenerator(Supplier<DoubleRange> rangeSupplier) {
		super(rangeSupplier);
	}

	@Override
	protected Double generateNextValue() {
		return SecureRandomUtils.random(getRange());
	}
}
