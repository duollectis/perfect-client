package com.perfect.client.functional.function.visuals.firefly;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class FireFlyConfig extends FunctionConfig<FireFly> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgColors = settings.createGroup();

	private final EnumSetting<FireFly.ParticleType> particleType = sgGeneral.add(new EnumSetting
			.Builder<>(FireFly.ParticleType.values())
			.id("physics")
			.defaultValue(FireFly.ParticleType.FALL)
			.onChanged(d -> Managers.PARTICLE.clear())
			.build());

	private final IntSetting count = sgGeneral.add(new IntSetting
			.Builder()
			.id("count")
			.range(1, 60)
			.sliderRange(1, 40)
			.defaultValue(20)
			.build());

	private final DoubleSetting particleScale = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("scale")
			.range(1.0, 100.0)
			.sliderRange(1.0, 100.0)
			.defaultValue(50.0)
			.onChanged(d -> Managers.PARTICLE.clear())
			.suffix(Suffix.PERCENT)
			.build());

	private final DoubleSetting speed = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("speed")
			.range(0.0, 5.0)
			.sliderRange(0.0, 5.0)
			.defaultValue(1.0)
			.suffix(Suffix.BPS)
			.onChanged(d -> Managers.PARTICLE.clear())
			.build());

	private final DoubleSetting lifeTime = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("life-time")
			.range(1.5, 5.0)
			.sliderRange(1.5, 3.0)
			.defaultValue(2.5)
			.suffix(Suffix.SECONDS)
			.build());

	private final MultiColorSetting colorSetting = sgColors.add(new MultiColorSetting
			.Builder()
			.id("color")
			.defaultValue(MultiColor.client())
			.build());

	public FireFlyConfig(FireFly function) {
		super(function);
	}
}
