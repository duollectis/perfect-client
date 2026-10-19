package com.perfect.client.core.utils.input;

import net.minecraft.client.option.KeyBinding;

public class Binds {

	public static int getKey(KeyBinding bind) {
		return bind.boundKey.getCode();
	}
}
