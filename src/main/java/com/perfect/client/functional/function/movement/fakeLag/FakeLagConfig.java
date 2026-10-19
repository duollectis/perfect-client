package com.perfect.client.functional.function.movement.fakeLag;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class FakeLagConfig extends FunctionConfig<FakeLag> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgAutoToggle = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();

	private final BindSetting cancelBind = sgGeneral.add(new BindSetting
			.Builder()
			.id("cancel-bind")
			.action(function::cancel)
			.build());

	private final EnumSetting<FakeLag.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(FakeLag.Mode.values())
			.id("mode")
			.defaultValue(FakeLag.Mode.NORMAL)
			.onChangedMode(function::getModeContainer)
			.build());

	private final BoolSetting autoDisable = sgAutoToggle.add(new BoolSetting
			.Builder()
			.id("auto-disable")
			.defaultValue(true)
			.visible(this::isNormalMode)
			.build());

	private final IntSetting timeToDisable = sgAutoToggle.add(new IntSetting
			.Builder()
			.id("time-to-disable")
			.sliderRange(100, 3000)
			.defaultValue(500)
			.suffix(Suffix.MILLISECONDS)
			.visible(this::isNormalMode)
			.build());

	private final IntSetting lagTime = sgAutoToggle.add(new IntSetting
			.Builder()
			.id("lag-time")
			.sliderRange(100, 3000)
			.defaultValue(500)
			.suffix(Suffix.MILLISECONDS)
			.visible(this::isTogglingMode)
			.build());

	private final BoolSetting render = sgRender.add(new BoolSetting
			.Builder()
			.id("render")
			.defaultValue(true)
			.build());

	private final MultiColorSetting outlineColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("outline-color")
			.defaultValue(MultiColor.client())
			.build());

	private final MultiColorSetting fillColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("fill-color")
			.defaultValue(MultiColor.clientFill())
			.build());

	public FakeLagConfig(FakeLag function) {
		super(function);
	}

	private boolean isNormalMode() {
		return mode.get(FakeLag.Mode.NORMAL);
	}

	private boolean isTogglingMode() {
		return mode.get(FakeLag.Mode.TOGGLING);
	}
}
