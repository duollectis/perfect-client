package com.perfect.client.functional.function.combat.offhand;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class OffhandConfig extends FunctionConfig<Offhand> {

	private final SettingGroup sgGeneral = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final BoolSetting smart = sgGeneral.add(new BoolSetting
			.Builder()
			.id("smart")
			.defaultValue(true)
			.build());

	private final BindSetting gAppleBind = sgGeneral.add(new BindSetting
			.Builder()
			.id("gapple-bind")
			.bindAction(this::onGAppleBind)
			.build());

	private final IntSetting delay = sgGeneral.add(new IntSetting
			.Builder()
			.id("delay")
			.defaultValue(0)
			.sliderRange(0, 500)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final EnumSetting<Offhand.OffhandItem> offhandItem = sgGeneral.add(new EnumSetting
			.Builder<>(Offhand.OffhandItem.values())
			.id("item")
			.defaultValue(Offhand.OffhandItem.CRYSTAL)
			.build());

	private final IntSetting minHealth = sgGeneral.add(new IntSetting
			.Builder()
			.id("min-hp")
			.defaultValue(8)
			.range(0, 100)
			.sliderRange(0, 36)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	private boolean gAppleBindPressed;

	public OffhandConfig(Offhand function) {
		super(function);
	}

	private void onGAppleBind(Bind bind, InputEvent event) {
		gAppleBindPressed = event.isPress();
	}
}
