package com.perfect.client.functional.function.visuals.targetEsp;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.CosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class TargetEspConfig extends FunctionConfig<TargetESP> {

	private final SettingGroup sgMode = settings.createGroup();

	private final EnumSetting<TargetESP.Mode> mode = sgMode.add(new EnumSetting
			.Builder<>(TargetESP.Mode.values())
			.id("mode")
			.defaultValue(TargetESP.Mode.GHOSTS)
			.onChangedMode(function::getModeContainer)
			.build());

	private final BoolSetting throughObstacles = sgMode.add(new BoolSetting
			.Builder()
			.id("through-obstacles")
			.defaultValue(true)
			.build());

	private final DoubleSetting ghostsSpeed = sgMode.add(new DoubleSetting
			.Builder()
			.id("ghosts-speed")
			.range(10.0, 100.0)
			.sliderRange(10.0, 100.0)
			.defaultValue(80.0)
			.suffix(Suffix.PERCENT)
			.visible(() -> mode.is(TargetESP.Mode.GHOSTS))
			.build());

	private final SettingGroup sgFunction = settings.createGroup();

	private final BoolSetting attackAura = sgFunction.add(new BoolSetting
			.Builder()
			.id("attack-aura")
			.defaultValue(true)
			.build());

	private final BoolSetting anchorAura = sgFunction.add(new BoolSetting
			.Builder()
			.id("anchor-aura")
			.defaultValue(true)
			.build());

	private final BoolSetting autoCrystal = sgFunction.add(new BoolSetting
			.Builder()
			.id("auto-crystal")
			.defaultValue(true)
			.build());

	private final SettingGroup sgColor = settings.createGroup();

	private final BundleSetting<CosmeticsBundle> boxCosmetics = sgColor.add(new BundleSetting
			.Builder<CosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new CosmeticsBundle())
			.visible(this::isBox)
			.build());

	private final MultiColorSetting boxLineColor = sgColor.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.visible(this::isBox)
			.build());

	private final MultiColorSetting boxSideColor = sgColor.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.visible(this::isBox)
			.build());

	private final MultiColorSetting circleLineColor = sgColor.add(new MultiColorSetting
			.Builder()
			.id("circle-line-color")
			.defaultValue(MultiColor.client())
			.visible(this::isCircle)
			.build());

	private final MultiColorSetting circleTrailColor = sgColor.add(new MultiColorSetting
			.Builder()
			.id("circle-trail-color")
			.defaultValue(MultiColor.client(0.8f))
			.visible(this::isCircle)
			.build());

	private final MultiColorSetting color = sgColor.add(new MultiColorSetting
			.Builder()
			.id("color")
			.defaultValue(MultiColor.client(0.8f))
			.visible(this::isSingleColor)
			.build());

	public TargetEspConfig(TargetESP function) {
		super(function);
	}

	private boolean isBox() {
		return mode.is(TargetESP.Mode.BOX);
	}

	private boolean isCircle() {
		return mode.is(TargetESP.Mode.CIRCLE);
	}

	private boolean isSingleColor() {
		return mode.is(TargetESP.Mode.GHOSTS);
	}
}
