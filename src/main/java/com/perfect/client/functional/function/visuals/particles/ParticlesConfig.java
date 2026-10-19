package com.perfect.client.functional.function.visuals.particles;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class ParticlesConfig extends FunctionConfig<Particles> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgColors = settings.createGroup();

	private final EnumListSetting<Particles.Condition> elements = sgGeneral.add(new EnumListSetting
			.Builder<>(Particles.Condition.values())
			.id("condition")
			.onChanged(mode -> function.getSetModeContainer().set(mode))
			.build());

	private final DoubleSetting particleScale = sgGeneral.add(new DoubleSetting.Builder()
			.id("scale")
			.range(1.0, 100.0)
			.sliderRange(1.0, 100.0)
			.defaultValue(50.0)
			.suffix(Suffix.PERCENT)
			.onChanged(d -> Managers.PARTICLE.clear())
			.build());

	private final DoubleSetting particleSpeed = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("speed")
			.range(0.0, 100.0)
			.sliderRange(0.0, 100.0)
			.defaultValue(30.0)
			.onChanged(d -> Managers.PARTICLE.clear())
			.suffix(Suffix.PERCENT)
			.build());

	private final MultiColorSetting particleColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("color")
			.defaultValue(MultiColor.client())
			.build());

	public ParticlesConfig(Particles function) {
		super(function);
	}
}
