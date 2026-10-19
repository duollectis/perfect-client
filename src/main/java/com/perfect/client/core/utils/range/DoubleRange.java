package com.perfect.client.core.utils.range;

public class DoubleRange extends AbstractRange<Double> {

	public DoubleRange(DoubleRange range) {
		this(range.from, range.to);
	}

	public DoubleRange(double from, double to) {
		super(from, to);
	}
}
