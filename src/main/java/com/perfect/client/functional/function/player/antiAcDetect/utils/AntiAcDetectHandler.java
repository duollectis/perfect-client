package com.perfect.client.functional.function.player.antiAcDetect.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetect;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetectConfig;

public abstract class AntiAcDetectHandler extends FunctionHandler<AntiAcDetect, AntiAcDetectConfig> {

	public AntiAcDetectHandler(AntiAcDetect function, AntiAcDetectConfig config) {
		super(function, config);
	}
}
