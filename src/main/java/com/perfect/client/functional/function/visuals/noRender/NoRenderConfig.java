package com.perfect.client.functional.function.visuals.noRender;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.list.EntitiesSetting;
import lombok.Getter;
import net.minecraft.entity.EntityType;

@Getter
public class NoRenderConfig extends FunctionConfig<NoRender> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EntitiesSetting entities = sgGeneral.add(new EntitiesSetting
			.Builder()
			.id("entities")
			.defaultValue(EntityType.ARMOR_STAND)
			.build());

	private final EnumListSetting<Elements> elements = sgGeneral.add(new EnumListSetting
			.Builder<>(Elements.values())
			.id("elements")
			.build());

	public NoRenderConfig(NoRender function) {
		super(function);
	}
}
