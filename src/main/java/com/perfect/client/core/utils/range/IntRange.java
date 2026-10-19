package com.perfect.client.core.utils.range;

public class IntRange extends AbstractRange<Integer> {

	public IntRange(IntRange range) {
		this(range.from, range.to);
	}

	public IntRange(int from, int to) {
		super(from, to);
	}
}
