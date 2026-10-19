package com.perfect.client.functional.function.visuals.hands;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.shader.ShaderType;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class HandsConfig extends FunctionConfig<Hands> {

	private final SettingGroup sgShader = settings.createGroup();
	private final SettingGroup sgViewModel = settings.createGroup();

	private final BoolSetting shader = sgShader.add(new BoolSetting
			.Builder()
			.id("shader")
			.defaultValue(false)
			.build());

	private final EnumSetting<ShaderType> shaderType = sgShader.add(new EnumSetting
			.Builder<>(ShaderType.values())
			.id("shader-type")
			.defaultValue(ShaderType.DEFAULT)
			.visible(shader::get)
			.build());

	private final IntSetting lineWidth = sgShader.add(new IntSetting
			.Builder()
			.id("line-width")
			.defaultValue(1)
			.range(0, 3)
			.sliderRange(0, 3)
			.visible(shader::get)
			.build());

	private final DoubleSetting gradientFactor = sgShader.add(new DoubleSetting
			.Builder()
			.id("gradient-factor")
			.defaultValue(40.0)
			.range(0, 100.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(this::isGradientShader)
			.build());

	private final DoubleSetting gradientSpeed = sgShader.add(new DoubleSetting
			.Builder()
			.id("gradient-speed")
			.defaultValue(40.0)
			.range(0, 200.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(this::isGradientShader)
			.build());

	private final DoubleSetting waveDistance = sgShader.add(new DoubleSetting
			.Builder()
			.id("wave-distance")
			.defaultValue(50.0)
			.range(0, 300.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> shader.get() && shaderType.get(ShaderType.WAVE))
			.build());

	private final DoubleSetting waveWidth = sgShader.add(new DoubleSetting
			.Builder()
			.id("wave-width")
			.defaultValue(25.0)
			.range(0, 100.0)
			.sliderRange(10.0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> shader.get() && shaderType.get(ShaderType.WAVE))
			.build());

	private final DoubleSetting waveSpeed = sgShader.add(new DoubleSetting
			.Builder()
			.id("wave-speed")
			.defaultValue(13.0)
			.range(0, 200.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> shader.get() && shaderType.get(ShaderType.WAVE))
			.build());

	private final BoolSetting glow = sgShader.add(new BoolSetting
			.Builder()
			.id("glow")
			.defaultValue(true)
			.visible(shader::get)
			.build());

	private final DoubleSetting glowSize = sgShader.add(new DoubleSetting
			.Builder()
			.id("glow-size")
			.suffix(Suffix.PERCENT)
			.defaultValue(200.0)
			.range(50.0, 300.0)
			.sliderRange(50.0, 300.0)
			.visible(() -> shader.get() && glow.get())
			.build());

	private final DoubleSetting glowMultiplier = sgShader.add(new DoubleSetting
			.Builder()
			.id("glow-multiplier")
			.suffix(Suffix.PERCENT)
			.defaultValue(100.0)
			.range(50.0, 150.0)
			.sliderRange(50.0, 150.0)
			.visible(() -> shader.get() && glow.get())
			.build());

	private final MultiColorSetting glowColor = sgShader.add(new MultiColorSetting
			.Builder()
			.id("glow-color")
			.defaultValue(MultiColor.client())
			.visible(() -> shader.get() && glow.get() && shaderType.get(ShaderType.DEFAULT))
			.toggleable(false)
			.build());

	private final MultiColorSetting waveColor = sgShader.add(new MultiColorSetting
			.Builder()
			.id("wave-color")
			.defaultValue(MultiColor.clientFill())
			.visible(() -> shader.get() && shaderType.get(ShaderType.WAVE))
			.build());

	private final MultiColorSetting outlineColor = sgShader.add(new MultiColorSetting
			.Builder()
			.id("outline-color")
			.defaultValue(MultiColor.client())
			.visible(() -> shader.get() && shaderType.get(ShaderType.DEFAULT))
			.build());

	private final DoubleSetting fillOpacity = sgShader.add(new DoubleSetting
			.Builder()
			.id("fill-opacity")
			.defaultValue(30.0)
			.range(0, 100.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(shader::get)
			.build());

	private final BoolSetting viewModel = sgViewModel.add(new BoolSetting
			.Builder()
			.id("view-model")
			.defaultValue(false)
			.build());

	private final DoubleSetting scale = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("scale")
			.defaultValue(100)
			.range(10, 500)
			.sliderRange(10, 500)
			.visible(viewModel::get)
			.suffix(Suffix.PERCENT)
			.build());

	private final DoubleSetting x = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("x")
			.defaultValue(0)
			.min(-10000)
			.sliderRange(-3, 3)
			.visible(viewModel::get)
			.build());

	private final DoubleSetting y = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("y")
			.defaultValue(0)
			.min(-10000)
			.sliderRange(-3, 3)
			.visible(viewModel::get)
			.build());

	private final DoubleSetting z = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("z")
			.defaultValue(0)
			.min(-10000)
			.sliderRange(-3, 3)
			.visible(viewModel::get)
			.build());

	private final BoolSetting changeFoodAnimation = sgViewModel.add(new BoolSetting
			.Builder()
			.id("change-food-animation")
			.defaultValue(false)
			.visible(viewModel::get)
			.build());

	private final DoubleSetting eatX = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("eat-x")
			.defaultValue(0)
			.min(-10000)
			.sliderRange(-3, 3)
			.visible(() -> viewModel.get() && changeFoodAnimation.get())
			.build());

	private final DoubleSetting eatY = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("eat-y")
			.defaultValue(0)
			.min(-10000)
			.sliderRange(-3, 3)
			.visible(() -> viewModel.get() && changeFoodAnimation.get())
			.build());

	private final DoubleSetting eatZ = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("eat-z")
			.defaultValue(0)
			.min(-10000)
			.sliderRange(-3, 3)
			.visible(() -> viewModel.get() && changeFoodAnimation.get())
			.build());

	private final DoubleSetting eatMultiplier = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("eat-multiplier")
			.defaultValue(100.0)
			.range(0.0, 100.0)
			.sliderRange(0.0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> viewModel.get() && changeFoodAnimation.get())
			.build());

	private final DoubleSetting rotateX = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("rotate-x")
			.defaultValue(0)
			.range(-180, 180)
			.sliderRange(-180, 180)
			.visible(viewModel::get)
			.build());

	private final DoubleSetting rotateY = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("rotate-y")
			.defaultValue(0)
			.range(-180, 180)
			.sliderRange(-180, 180)
			.visible(viewModel::get)
			.build());

	private final DoubleSetting rotateZ = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("rotate-z")
			.defaultValue(0)
			.range(-180, 180)
			.sliderRange(-180, 180)
			.visible(viewModel::get)
			.build());

	private final BoolSetting changeSwingAnimation = sgViewModel.add(new BoolSetting
			.Builder()
			.id("change-swing-animation")
			.defaultValue(false)
			.visible(viewModel::get)
			.build());

	private final DoubleSetting swingSpeed = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("swing-speed")
			.defaultValue(30.0)
			.range(0.0, 300.0)
			.sliderRange(0.0, 200.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> viewModel.get() && changeSwingAnimation.get())
			.build());

	private final DoubleSetting swingEnd = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("swing-end-progress")
			.defaultValue(30.0)
			.range(0.0, 100.0)
			.sliderRange(0.0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> viewModel.get() && changeSwingAnimation.get())
			.build());

	private final DoubleSetting swingEndY = sgViewModel.add(new DoubleSetting
			.Builder()
			.id("swing-end-y")
			.defaultValue(0.0)
			.min(-10000)
			.sliderRange(-10.0, 10.0)
			.visible(() -> viewModel.get() && changeSwingAnimation.get())
			.build());

	private final BoolSetting resetSwingAnimation = sgViewModel.add(new BoolSetting
			.Builder()
			.id("reset-swing-animation")
			.defaultValue(false)
			.visible(() -> viewModel.get() && changeSwingAnimation.get())
			.build());

	public HandsConfig(Hands function) {
		super(function);
	}

	public boolean isGradientShader() {
		return shader.get() && shaderType.get(ShaderType.GRADIENT);
	}
}
