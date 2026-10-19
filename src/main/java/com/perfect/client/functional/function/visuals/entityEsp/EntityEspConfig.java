package com.perfect.client.functional.function.visuals.entityEsp;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.list.EntitiesSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.shader.ShaderType;
import com.perfect.client.core.utils.game.entity.EntityGroup;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;
import net.minecraft.entity.EntityType;

@Getter
public class EntityEspConfig extends FunctionConfig<EntityESP> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgShaderSettings = settings.createGroup();
	private final SettingGroup sgGlowSettings = settings.createGroup();
	private final SettingGroup sgColors = settings.createGroup();

	private final EnumSetting<ShaderType> shaderType = sgGeneral.add(new EnumSetting
			.Builder<>(ShaderType.values())
			.id("shader-type")
			.defaultValue(ShaderType.DEFAULT)
			.build());

	private final EntitiesSetting entities = sgGeneral.add(new EntitiesSetting
			.Builder()
			.id("entities")
			.defaultGroups(EntityGroup.PLAYERS)
			.defaultValue(EntityType.PLAYER)
			.build());

	private final DoubleSetting renderDistance = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("render-distance")
			.defaultValue(64.0)
			.range(0, 512.0)
			.sliderRange(0, 128.0)
			.suffix(Suffix.BLOCKS)
			.build());

	private final IntSetting lineWidth = sgGeneral.add(new IntSetting
			.Builder()
			.id("line-width")
			.defaultValue(1)
			.range(0, 3)
			.sliderRange(0, 3)
			.build());

	private final DoubleSetting gradientFactor = sgShaderSettings.add(new DoubleSetting
			.Builder()
			.id("gradient-factor")
			.defaultValue(40.0)
			.range(0, 100.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(this::isGradientShader)
			.build());

	private final DoubleSetting gradientSpeed = sgShaderSettings.add(new DoubleSetting
			.Builder()
			.id("gradient-speed")
			.defaultValue(40.0)
			.range(0, 200.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(this::isGradientShader)
			.build());

	private final DoubleSetting waveDistance = sgShaderSettings.add(new DoubleSetting
			.Builder()
			.id("wave-distance")
			.defaultValue(50.0)
			.range(0, 300.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> shaderType.get(ShaderType.WAVE))
			.build());

	private final DoubleSetting waveWidth = sgShaderSettings.add(new DoubleSetting
			.Builder()
			.id("wave-width")
			.defaultValue(25.0)
			.range(0, 100.0)
			.sliderRange(10.0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> shaderType.get(ShaderType.WAVE))
			.build());

	private final DoubleSetting waveSpeed = sgShaderSettings.add(new DoubleSetting
			.Builder()
			.id("wave-speed")
			.defaultValue(13.0)
			.range(0, 200.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> shaderType.get(ShaderType.WAVE))
			.build());

	private final BoolSetting glow = sgGlowSettings.add(new BoolSetting
			.Builder()
			.id("glow")
			.defaultValue(true)
			.build());

	private final DoubleSetting glowSize = sgGlowSettings.add(new DoubleSetting
			.Builder()
			.id("glow-size")
			.suffix(Suffix.PERCENT)
			.defaultValue(200.0)
			.range(50.0, 300.0)
			.sliderRange(50.0, 300.0)
			.visible(glow::get)
			.build());

	private final DoubleSetting glowMultiplier = sgGlowSettings.add(new DoubleSetting
			.Builder()
			.id("glow-multiplier")
			.suffix(Suffix.PERCENT)
			.defaultValue(100.0)
			.range(50.0, 150.0)
			.sliderRange(50.0, 150.0)
			.visible(glow::get)
			.build());

	private final MultiColorSetting glowColor = sgGlowSettings.add(new MultiColorSetting
			.Builder()
			.id("glow-color")
			.defaultValue(MultiColor.client())
			.visible(() -> glow.get() && shaderType.get(ShaderType.DEFAULT))
			.toggleable(false)
			.build());

	private final MultiColorSetting waveColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("wave-color")
			.defaultValue(MultiColor.clientFill())
			.visible(() -> shaderType.get(ShaderType.WAVE))
			.build());

	private final MultiColorSetting waveOutlineColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("wave-outline-color")
			.defaultValue(MultiColor.client())
			.visible(() -> shaderType.get(ShaderType.WAVE))
			.toggleable(true)
			.build());

	private final DoubleSetting fillOpacity = sgColors.add(new DoubleSetting
			.Builder()
			.id("fill-opacity")
			.defaultValue(30.0)
			.range(0, 100.0)
			.sliderRange(0, 100.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> !shaderType.get(ShaderType.DEFAULT))
			.build());

	private final MultiColorSetting outlineColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("outline-color")
			.defaultValue(MultiColor.client())
			.visible(() -> shaderType.get(ShaderType.DEFAULT))
			.build());

	private final MultiColorSetting fillColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("fill-color")
			.defaultValue(MultiColor.clientFill())
			.visible(() -> shaderType.get(ShaderType.DEFAULT))
			.build());

	public EntityEspConfig(EntityESP function) {
		super(function);
	}

	public boolean isGradientShader() {
		return shaderType.get(ShaderType.GRADIENT);
	}
}
