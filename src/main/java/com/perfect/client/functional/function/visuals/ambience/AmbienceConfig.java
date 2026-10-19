package com.perfect.client.functional.function.visuals.ambience;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.functional.setting.impl.number.range.DoubleRangeSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import lombok.Getter;

@Getter
public class AmbienceConfig extends FunctionConfig<Ambience> {

	private final SettingGroup sgSky = settings.createGroup();
	private final SettingGroup sgTime = settings.createGroup();
	private final SettingGroup sgFog = settings.createGroup();
	private final SettingGroup sgFogColor = settings.createGroup();
	private final SettingGroup sgFogRange = settings.createGroup();
	private final SettingGroup sgParticles = settings.createGroup();

	private final BoolSetting flatSky = sgSky.add(new BoolSetting
			.Builder()
			.id("flat-sky")
			.defaultValue(false)
			.build());

	private final BoolSetting customSkyColor = sgSky.add(new BoolSetting
			.Builder()
			.id("custom-sky-color")
			.defaultValue(false)
			.build());

	private final MultiColorSetting overworldSkyColor = sgSky.add(new MultiColorSetting
			.Builder()
			.id("overworld-sky-color")
			.defaultValue(MultiColor.client())
			.visible(customSkyColor::get)
			.build());

	private final MultiColorSetting netherSkyColor = sgSky.add(new MultiColorSetting
			.Builder()
			.id("nether-sky-color")
			.defaultValue(MultiColor.client())
			.visible(customSkyColor::get)
			.build());

	private final MultiColorSetting endSkyColor = sgSky.add(new MultiColorSetting
			.Builder()
			.id("end-sky-color")
			.defaultValue(MultiColor.client())
			.visible(customSkyColor::get)
			.build());

	private final BoolSetting worldTime = sgTime.add(new BoolSetting
			.Builder()
			.id("world-time")
			.defaultValue(false)
			.onChanged(function::onWorldTimeChanged)
			.build());

	private final EnumSetting<Ambience.Time> timeMode = sgTime.add(new EnumSetting
			.Builder<>(Ambience.Time.values())
			.id("time")
			.defaultValue(Ambience.Time.EVENING)
			.visible(worldTime::get)
			.build());

	private final IntSetting customTime = sgTime.add(new IntSetting
			.Builder()
			.id("custom-time")
			.defaultValue(0)
			.sliderRange(0, 24000)
			.visible(() -> timeMode.isVisible() && timeMode.get() == Ambience.Time.CUSTOM)
			.build());

	private final BoolSetting customFog = sgFog.add(new BoolSetting
			.Builder()
			.id("custom-fog")
			.defaultValue(false)
			.build());

	private final BoolSetting thickFog = sgFog.add(new BoolSetting
			.Builder()
			.id("thick-fog")
			.defaultValue(true)
			.visible(customFog::get)
			.build());

	private final BoolSetting customFogColor = sgFogColor.add(new BoolSetting
			.Builder()
			.id("custom-fog-color")
			.defaultValue(true)
			.visible(customFog::get)
			.build());

	private final MultiColorSetting overworldFogColor = sgFogColor.add(new MultiColorSetting
			.Builder()
			.id("overworld-fog-color")
			.defaultValue(MultiColor.client())
			.visible(() -> customFog.get() && customFogColor.get())
			.build());

	private final MultiColorSetting netherFogColor = sgFogColor.add(new MultiColorSetting
			.Builder()
			.id("nether-fog-color")
			.defaultValue(MultiColor.client())
			.visible(() -> customFog.get() && customFogColor.get())
			.build());

	private final MultiColorSetting endFogColor = sgFogColor.add(new MultiColorSetting
			.Builder()
			.id("end-fog-color")
			.defaultValue(MultiColor.client())
			.visible(() -> customFog.get() && customFogColor.get())
			.build());

	private final BoolSetting customFogRange = sgFogRange.add(new BoolSetting
			.Builder()
			.id("custom-fog-range")
			.defaultValue(true)
			.visible(customFog::get)
			.build());

	private final DoubleRangeSetting fogRange = sgFogRange.add(new DoubleRangeSetting
			.Builder()
			.id("fog-range")
			.defaultValue(0, 5)
			.sliderRange(0, 20)
			.range(0, 100)
			.visible(customFog::get)
			.build());

	private final BoolSetting particles = sgParticles.add(new BoolSetting
			.Builder()
			.id("particles")
			.defaultValue(false)
			.build());

	private final MultiColorSetting particleColor = sgParticles.add(new MultiColorSetting
			.Builder()
			.id("particle-color")
			.defaultValue(MultiColor.client())
			.visible(particles::get)
			.build());

	public AmbienceConfig(Ambience function) {
		super(function);
	}
}
