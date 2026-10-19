package com.perfect.client.functional.function.movement.fakeLag.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.movement.fakeLag.FakeLag;
import com.perfect.client.functional.function.movement.fakeLag.FakeLagConfig;

public class FakeLagMode extends FunctionHandler<FakeLag, FakeLagConfig> {

	public FakeLagMode(FakeLag function, FakeLagConfig config) {
		super(function, config);
	}

	public void onTick() {}
	public void reset() {}
}
