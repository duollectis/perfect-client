package com.perfect.client.gui.hud.elements.speed;

import com.perfect.client.core.functional.function.config.HudConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class SpeedHudConfig extends HudConfig<SpeedHud> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<SpeedHud.FormatMode> formatMode = sgGeneral.add(new EnumSetting
			.Builder<>(SpeedHud.FormatMode.values())
			.id("format-mode")
			.defaultValue(SpeedHud.FormatMode.BPS)
			.onChanged(hudWidget::updateInfo)
			.build());

	public SpeedHudConfig(SpeedHud hudWidget) {
		super(hudWidget);
	}
}
