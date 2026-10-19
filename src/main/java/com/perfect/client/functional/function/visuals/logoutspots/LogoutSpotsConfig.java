package com.perfect.client.functional.function.visuals.logoutspots;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import lombok.Getter;

@Getter
public class LogoutSpotsConfig extends FunctionConfig<LogoutSpots> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgElements = settings.createGroup();

	private final DoubleSetting scale = sgGeneral.add(new DoubleSetting.Builder()
			.id("scale")
			.defaultValue(1)
			.range(0.1, 10)
			.sliderRange(0.2, 5)
			.build());

	private final BoolSetting ignoreFriends = sgElements.add(new BoolSetting.Builder()
			.id("ignore-friends")
			.defaultValue(true)
			.build());

	private final BoolSetting armorCheck = sgGeneral.add(new BoolSetting.Builder()
			.id("check-armor")
			.defaultValue(false)
			.build());

	private final BoolSetting messageWhileLogBack = sgGeneral.add(new BoolSetting.Builder()
			.id("log-back-message")
			.defaultValue(false)
			.build());

	private final BoolSetting background = sgGeneral.add(new BoolSetting.Builder()
			.id("background")
			.defaultValue(true)
			.build());

	private final MultiColorSetting outlineColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("outline-color")
			.defaultValue(MultiColor.client())
			.build());

	private final MultiColorSetting fillColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("fill-color")
			.defaultValue(MultiColor.clientFill())
			.build());

	private final BoolSetting nameRender = sgElements.add(new BoolSetting.Builder()
			.id("name")
			.defaultValue(true)
			.build());

	private final BoolSetting healthRender = sgElements.add(new BoolSetting.Builder()
			.id("health")
			.defaultValue(true)
			.build());

	private final BoolSetting coordRender = sgElements.add(new BoolSetting.Builder()
			.id("coordinates")
			.defaultValue(true)
			.build());

	private final BoolSetting timePassed = sgElements.add(new BoolSetting.Builder()
			.id("time")
			.defaultValue(true)
			.build());

	public LogoutSpotsConfig(LogoutSpots function) {
		super(function);
	}
}