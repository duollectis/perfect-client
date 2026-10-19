package com.perfect.client.core.utils.range;

import lombok.Getter;

import java.util.Objects;

@Getter
public class AbstractRange<T extends Number> {

	protected final T from;
	protected final T to;

	public AbstractRange(T from, T to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractRange<?> range) {
			return Objects.equals(from, range.from) && Objects.equals(to, range.to);
		}
		return false;
	}
}
