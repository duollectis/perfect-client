package com.perfect.client.core.functional.function.config;

import com.perfect.client.core.functional.setting.Settings;
import lombok.Getter;

@Getter
public abstract class Config {

	protected final Settings settings;

	public Config(Settings settings) {
		this.settings = settings;
	}
}
