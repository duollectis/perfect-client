package com.perfect.client.functional.function.combat.blocker;

import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class BlockerConfig extends PlaceFunctionConfig<Blocker> {

	private final SettingGroup sgBlockerGeneral = settings.createGroup(0);
	private final SettingGroup sgBlockerNormal = settings.createGroup(1);
	private final SettingGroup sgBlockerBlind = settings.createGroup(2);
	private final SettingGroup sgBlockerBind = settings.createGroup(3);

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final EnumSetting<Blocker.Mode> mode = sgBlockerGeneral.add(new EnumSetting
			.Builder<>(Blocker.Mode.values())
			.id("mode")
			.defaultValue(Blocker.Mode.NORMAL)
			.onChanged(mode -> function.getModeContainer().set(mode))
			.build());

	private final DoubleSetting breakProgress = sgBlockerGeneral.add(new DoubleSetting
			.Builder()
			.id("break-progress")
			.range(0, 100)
			.sliderMax(100)
			.defaultValue(90)
			.suffix(Suffix.PERCENT)
			.build());

	// ————————————————————————————— NORMAL ——————————————————————————————— //

	private final EnumSetting<Blocker.ProtectMode> protection = sgBlockerNormal.add(new EnumSetting
			.Builder<>(Blocker.ProtectMode.values())
			.id("protection")
			.defaultValue(Blocker.ProtectMode.FULL)
			.visible(this::isNormalMode)
			.build());

	private final BoolSetting extraPositions = sgBlockerNormal.add(new BoolSetting
			.Builder()
			.id("extra-positions")
			.defaultValue(true)
			.visible(() -> protection.get().selfTrap() && isNormalMode())
			.build());

	private final BoolSetting airTrigger = sgBlockerNormal.add(new BoolSetting
			.Builder()
			.id("air-trigger")
			.defaultValue(true)
			.visible(() -> protection.get().selfTrap() && isNormalMode())
			.build());

	private final DoubleSetting minDamage = sgBlockerNormal.add(new DoubleSetting
			.Builder()
			.id("min-trigger-damage")
			.range(0, 36)
			.defaultValue(5d)
			.visible(this::isNormalMode)
			.build());

	// ————————————————————————————— BLIND ——————————————————————————————— //

	private final DoubleSetting miningRange = sgBlockerBlind.add(new DoubleSetting
			.Builder()
			.id("mining-range")
			.range(0, 7)
			.defaultValue(6)
			.visible(this::isBlindMode)
			.build());

	private final BoolSetting targetInHole = sgBlockerBind.add(new BoolSetting
			.Builder()
			.id("only-targets-in-hole")
			.defaultValue(false)
			.build());

	private final EnumSetting<Blocker.BlindProtectMode> surroundPredict = sgBlockerBlind.add(new EnumSetting
			.Builder<>(Blocker.BlindProtectMode.values())
			.id("surround")
			.defaultValue(Blocker.BlindProtectMode.DELAYED)
			.visible(this::isBlindMode)
			.build());

	private final EnumSetting<Blocker.BlindProtectMode> selfTrapPredict = sgBlockerBlind.add(new EnumSetting
			.Builder<>(Blocker.BlindProtectMode.values())
			.id("self-trap")
			.defaultValue(Blocker.BlindProtectMode.PREDICTIVE)
			.visible(this::isBlindMode)
			.build());

	// ————————————————————————————— BIND ——————————————————————————————— //

	private final BindSetting surroundBind = sgBlockerBind.add(new BindSetting
			.Builder()
			.id("force-surround-place")
			.action(function::onSurroundBind)
			.build());

	private final BindSetting selfTrapBind = sgBlockerBind.add(new BindSetting
			.Builder()
			.id("force-self-trap-place")
			.action(function::onSelfTrapBind)
			.build());

	public BlockerConfig(Blocker function) {
		super(function);
	}

	private boolean isNormalMode() {
		return mode.get(Blocker.Mode.NORMAL);
	}

	private boolean isBlindMode() {
		return mode.get(Blocker.Mode.BLIND);
	}
}
