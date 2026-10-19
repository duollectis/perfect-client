package com.perfect.client.functional.function.visuals.chams;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class ChamsConfig extends FunctionConfig<Chams> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgCrystals = settings.createGroup();
	private final SettingGroup sgPlayers  = settings.createGroup();

	private final EnumSetting<Chams.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(Chams.Mode.values())
			.id("mode")
			.defaultValue(Chams.Mode.IMAGE)
			.build());

	private final MultiColorSetting shaderColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("shader-color")
			.defaultValue(MultiColor.client())
			.visible(() -> mode.get(Chams.Mode.IMAGE))
			.build());

	private final DoubleSetting renderDistance = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("render-distance")
			.defaultValue(64.0)
			.range(0, 512.0)
			.sliderRange(0, 128.0)
			.suffix(Suffix.BLOCKS)
			.visible(() -> !mode.get(Chams.Mode.NONE))
			.build());

	private final BoolSetting crystals = sgCrystals.add(new BoolSetting
			.Builder()
			.id("crystals")
			.defaultValue(false)
			.build());

	private final DoubleSetting crystalsScale = sgCrystals.add(new DoubleSetting.Builder()
			.id("crystals-scale")
			.defaultValue(100)
			.sliderRange(0, 200)
			.visible(crystals::get)
			.suffix(Suffix.PERCENT)
			.build());

	private final DoubleSetting crystalsBounce = sgCrystals.add(new DoubleSetting.Builder()
			.id("crystals-bounce")
			.defaultValue(100)
			.sliderRange(0, 200)
			.visible(crystals::get)
			.suffix(Suffix.PERCENT)
			.build());

	private final DoubleSetting crystalsBounceSpeed = sgCrystals.add(new DoubleSetting.Builder()
			.id("crystals-bounce-speed")
			.defaultValue(100)
			.sliderRange(0, 200)
			.visible(crystals::get)
			.suffix(Suffix.PERCENT)
			.build());

	private final DoubleSetting crystalsRotationSpeed = sgCrystals.add(new DoubleSetting.Builder()
			.id("crystals-rotation-speed")
			.defaultValue(100)
			.sliderRange(0, 200)
			.visible(crystals::get)
			.suffix(Suffix.PERCENT)
			.build());

	private final MultiColorSetting crystalsColor = sgCrystals.add(new MultiColorSetting
			.Builder()
			.id("crystals-change-color")
			.defaultValue(MultiColor.client())
			.toggleable(true)
			.visible(crystals::get)
			.build());

	private final MultiColorSetting crystalsOutlineColor = sgCrystals.add(new MultiColorSetting
			.Builder()
			.id("crystals-outline-color")
			.defaultValue(MultiColor.client())
			.visible(() -> crystals.get() && isOutlineMode())
			.build());

	private final MultiColorSetting crystalsFillColor = sgCrystals.add(new MultiColorSetting
			.Builder()
			.id("crystals-fill-color")
			.defaultValue(MultiColor.clientFill())
			.visible(() -> crystals.get() && isOutlineMode())
			.build());

	private final BoolSetting players = sgPlayers.add(new BoolSetting
			.Builder()
			.id("players")
			.defaultValue(false)
			.build());

	private final BoolSetting ignoreSelf = sgPlayers.add(new BoolSetting
			.Builder()
			.id("ignore-self")
			.defaultValue(false)
			.visible(players::get)
			.build());

	private final MultiColorSetting playersColor = sgPlayers.add(new MultiColorSetting
			.Builder()
			.id("players-change-color")
			.defaultValue(MultiColor.client())
			.toggleable(true)
			.visible(players::get)
			.build());

	private final MultiColorSetting playersOutlineColor = sgPlayers.add(new MultiColorSetting
			.Builder()
			.id("players-outline-color")
			.defaultValue(MultiColor.client())
			.visible(() -> isOutlineMode() && players.get())
			.build());

	private final MultiColorSetting playersFillColor = sgPlayers.add(new MultiColorSetting
			.Builder()
			.id("players-fill-color")
			.defaultValue(MultiColor.clientFill())
			.visible(() -> isOutlineMode() && players.get())
			.build());

	public ChamsConfig(Chams function) {
		super(function);
	}

	public boolean isOutlineMode() {
		return mode.get(Chams.Mode.OUTLINE);
	}
}
