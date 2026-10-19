package com.perfect.client.gui.hud.elements.potions;

import com.perfect.client.core.functional.function.config.HudConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class PotionsHudConfig extends HudConfig<PotionsHud> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<PotionsHud.ColorMode> textColorMode = sgGeneral.add(new EnumSetting
			.Builder<>(PotionsHud.ColorMode.values())
			.id("text-color-mode")
			.defaultValue(PotionsHud.ColorMode.CLIENT)
			.build());

	private final EnumSetting<PotionsHud.ColorMode> backgroundColorMode = sgGeneral.add(new EnumSetting
			.Builder<>(PotionsHud.ColorMode.values())
			.id("background-color-mode")
			.defaultValue(PotionsHud.ColorMode.CLIENT)
			.build());

	public PotionsHudConfig(PotionsHud hudWidget) {
		super(hudWidget);
	}
}
