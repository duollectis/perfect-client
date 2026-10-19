package com.perfect.client.core.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutableInt {

	private int value;

	public MutableInt() {}

	public MutableInt(int value) {
		this.value = value;
	}
}
