package com.perfect.client.core.utils.range.utils;

import com.perfect.client.core.utils.IUpdatable;
import com.perfect.client.core.utils.range.AbstractRange;

import java.util.function.Supplier;

public abstract class AbstractRangeGenerator<T extends Number, K extends AbstractRange<T>> implements IUpdatable {

	private final Supplier<K> rangeSupplier;

	private T nextValue;

	public AbstractRangeGenerator(Supplier<K> rangeSupplier) {
		this.rangeSupplier = rangeSupplier;
		update();
	}

	@Override
	public void update() {
		nextValue = generateNextValue();
	}

	public T getValue() {
		return nextValue;
	}

	protected K getRange() {
		return rangeSupplier.get();
	}

	protected abstract T generateNextValue();
}
