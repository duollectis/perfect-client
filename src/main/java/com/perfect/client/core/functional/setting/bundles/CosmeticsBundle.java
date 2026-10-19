package com.perfect.client.core.functional.setting.bundles;

import com.perfect.client.core.functional.function.config.SettingBundle;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.render.box.pattern.Pattern;
import com.perfect.client.core.utils.functions.Segments;
import com.perfect.client.core.utils.text.Suffix;

public class CosmeticsBundle extends SettingBundle {

	private final SettingGroup sgShader = settings.createGroup();
	private final SettingGroup sgGradient = settings.createGroup();
	private final SettingGroup sgPattern = settings.createGroup();

	private final BoolSetting shader = sgShader.add(new BoolSetting
			.Builder()
			.id("shader")
			.defaultValue(false)
			.build()
	);

	private final IntSetting lineWidth = sgShader.add(new IntSetting
			.Builder()
			.id("line-width")
			.defaultValue(1)
			.range(0, 3)
			.sliderRange(0, 3)
			.visible(shader::get)
			.build());

	private final BoolSetting gradient = sgGradient.add(new BoolSetting
			.Builder()
			.id("gradient")
			.defaultValue(false)
			.build()
	);

	private final EnumSetting<Segments> gradientSegments = sgGradient.add(new EnumSetting
			.Builder<>(Segments.values())
			.id("gradient-segments")
			.defaultValue(Segments.TWO)
			.visible(gradient::get)
			.build()
	);

	private final DoubleSetting gradientFactor = sgGradient.add(new DoubleSetting
			.Builder()
			.id("gradient-factor")
			.defaultValue(40.0)
			.range(0, 100.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(this::isGradientShader)
			.build());

	private final DoubleSetting gradientSpeed = sgGradient.add(new DoubleSetting
			.Builder()
			.id("gradient-speed")
			.defaultValue(40.0)
			.range(0, 200.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(this::isGradientShader)
			.build());

	private final EnumSetting<Pattern> pattern = sgPattern.add(new EnumSetting
			.Builder<>(Pattern.values())
			.id("pattern")
			.defaultValue(Pattern.NONE)
			.build()
	);

	public Cosmetics bundle() {
		return new Cosmetics(
                shader.get(),
				lineWidth.get(),
				(float) gradientFactor.fromBPS(),
				(float) gradientSpeed.fromBPS(),
				gradient.get(),
				gradientSegments.get().getSegmentsValue(),
				pattern.get()
		);
	}

	private boolean isGradientShader() {
		return shader.get() && gradient.get();
	}
}
