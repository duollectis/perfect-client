package com.perfect.client.gui.hud.elements.functions;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.config.HudConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import lombok.Getter;

@Getter
public class FunctionsHudConfig extends HudConfig<FunctionsHud> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumListSetting<Category> categories = sgGeneral.add(new EnumListSetting
			.Builder<>(Category.values())
			.id("categories")
			.build());

	public FunctionsHudConfig(FunctionsHud hudWidget) {
		super(hudWidget);
	}
}
