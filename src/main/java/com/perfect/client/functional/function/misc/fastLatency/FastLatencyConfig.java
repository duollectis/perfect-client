package com.perfect.client.functional.function.misc.fastLatency;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.StringSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.text.Suffix;
import com.perfect.client.manager.game.ping.PingHandler;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class FastLatencyConfig extends FunctionConfig<FastLatency> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final Setting<PingMethod> method = sgGeneral.add(new EnumSetting
			.Builder<>(PingMethod.values())
			.id("method")
			.build());

	private final StringSetting command = sgGeneral.add(new StringSetting
			.Builder()
			.id("request-command")
			.defaultValue("/w")
			.visible(() -> method.get() == PingMethod.COMMAND)
			.build());

	private final DoubleSetting delay = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("delay")
			.min(0.05)
			.defaultValue(3)
			.suffix(Suffix.SECONDS)
			.build());

	public FastLatencyConfig(FastLatency function) {
		super(function);
	}

	public enum PingMethod {
		STATS(Managers.PING::getStatsHandler),
		COMMAND(Managers.PING::getCommandCompletionsHandler);

		private final Supplier<PingHandler> provider;

		PingMethod(Supplier<PingHandler> provider) {
			this.provider = provider;
		}

		public PingHandler getPingHandler() {
			return provider.get();
		}
	}
}
