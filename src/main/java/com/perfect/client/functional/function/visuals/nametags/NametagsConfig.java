package com.perfect.client.functional.function.visuals.nametags;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.list.EntitiesSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.game.entity.EntityGroup;
import lombok.Getter;

@Getter
public class NametagsConfig extends FunctionConfig<Nametags> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgDistanceScale = settings.createGroup();
	private final SettingGroup sgElements = settings.createGroup();

	private final EntitiesSetting entities = sgGeneral.add(new EntitiesSetting
			.Builder()
			.id("entities")
			.defaultGroups(EntityGroup.PLAYERS)
			.build());

	private final DoubleSetting scale = sgGeneral.add(new DoubleSetting.Builder()
			.id("scale")
			.defaultValue(1)
			.range(0.1, 300)
			.sliderRange(0.2, 2)
			.build());

	private final BoolSetting distanceScale = sgDistanceScale.add(new BoolSetting
			.Builder()
			.id("distance-scale")
			.defaultValue(false)
			.build());

	private final DoubleSetting minScale = sgDistanceScale.add(new DoubleSetting.Builder()
			.id("min-scale")
			.defaultValue(1)
			.range(0, 300)
			.sliderRange(0, 2)
			.visible(distanceScale::get)
			.build());

	private final BoolSetting simpleRect = sgDistanceScale.add(new BoolSetting
			.Builder()
			.id("simple-rect")
			.defaultValue(false)
			.build());

	private final BoolSetting armor = sgElements.add(new BoolSetting
			.Builder()
			.id("armor")
			.defaultValue(true)
			.build());

	private final BoolSetting health = sgElements.add(new BoolSetting
			.Builder()
			.id("health")
			.defaultValue(true)
			.build());

	private final BoolSetting totemPops = sgElements.add(new BoolSetting
			.Builder()
			.id("totem-pops")
			.defaultValue(true)
			.build());

	private final BoolSetting ping = sgElements.add(new BoolSetting
			.Builder()
			.id("ping")
			.defaultValue(true)
			.build());

	public NametagsConfig(Nametags function) {
		super(function);
	}
}
