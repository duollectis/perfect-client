package com.perfect.client.functional.function.misc.betterChat;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.StringSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class BetterChatConfig extends FunctionConfig<BetterChat> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgChatHistory = settings.createGroup();
	private final SettingGroup sgPrefixSuffix = settings.createGroup();

	private final BoolSetting animation = sgGeneral.add(new BoolSetting
			.Builder()
			.id("animation")
			.defaultValue(true)
			.build());

	private final BoolSetting timeStamps = sgGeneral.add(new BoolSetting
			.Builder()
			.id("time-stamps")
			.defaultValue(true)
			.build());

	private final BoolSetting playerHeads = sgGeneral.add(new BoolSetting
			.Builder()
			.id("player-heads")
			.defaultValue(true)
			.build());


	private final BoolSetting keepHistory = sgChatHistory.add(new BoolSetting
			.Builder()
			.id("keep-history")
			.defaultValue(true)
			.build());

	private final BoolSetting extraChatLength = sgChatHistory.add(new BoolSetting
			.Builder()
			.id("extra-chat-length")
			.defaultValue(true)
			.build());

	private final BoolSetting extraMessageLength = sgChatHistory.add(new BoolSetting
			.Builder()
			.id("extra-message-length")
			.defaultValue(true)
			.build());

	private final DoubleSetting chatWidth = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("chat-width")
			.range(1.0, 200.0)
			.sliderRange(100.0, 200.0)
			.defaultValue(100.0)
			.suffix(Suffix.PERCENT)
			.onChanged(function::updateChatHud)
			.toggleable(true)
			.build());


	private final StringSetting prefix = sgPrefixSuffix.add(new StringSetting
			.Builder()
			.id("prefix-string")
			.defaultValue("> ")
			.toggleable(false)
			.build());

	private final StringSetting suffix = sgPrefixSuffix.add(new StringSetting
			.Builder()
			.id("suffix-string")
			.defaultValue(" | perfect-client.fun")
			.toggleable(false)
			.build());

	public BetterChatConfig(BetterChat function) {
		super(function);
	}
}
