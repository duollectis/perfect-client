package com.perfect.client.functional.function.movement.elytraFly;

import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class ElytraFlyConfig extends FunctionConfig<ElytraFly> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgBounce = settings.createGroup();
	private final SettingGroup sgBoost = settings.createGroup();
	private final SettingGroup sgBoostStop = settings.createGroup();

	private final EnumSetting<ElytraFly.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(ElytraFly.Mode.values())
			.id("mode")
			.defaultValue(ElytraFly.Mode.CONTROL)
			.onChangedMode(function::getModeHandler)
			.build());

	private final BoolSetting sprint = sgBounce.add(new BoolSetting
			.Builder()
			.id("sprint")
			.defaultValue(true)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final BoolSetting autoTakeoff = sgBounce.add(new BoolSetting
			.Builder()
			.id("auto-takeoff")
			.defaultValue(false)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final BoolSetting restart = sgBounce.add(new BoolSetting
			.Builder()
			.id("restart")
			.defaultValue(false)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final IntSetting restartDelay = sgBounce.add(new IntSetting
			.Builder()
			.id("restart-delay")
			.range(0, 20)
			.sliderRange(0, 20)
			.defaultValue(10)
			.visible(() -> restart.isVisible() && restart.get())
			.build());

	private final BoolSetting accelerate = sgBounce.add(new BoolSetting
			.Builder()
			.id("accelerate")
			.defaultValue(true)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final DoubleSetting accelerateFactor = sgBounce.add(new DoubleSetting
			.Builder()
			.id("accelerate-factor")
			.range(0, 1.5)
			.sliderRange(0, 1.5)
			.defaultValue(0.12)
			.visible(() -> accelerate.isVisible() && accelerate.get())
			.build());

	private final DoubleSetting maxSpeed = sgBounce.add(new DoubleSetting
			.Builder()
			.id("max-speed")
			.range(0, 10)
			.sliderRange(0, 10)
			.defaultValue(0.1)
			.visible(() -> accelerate.isVisible() && accelerate.get())
			.build());

	private final BoolSetting changeYaw = sgBounce.add(new BoolSetting
			.Builder()
			.id("change-yaw")
			.defaultValue(true)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final DoubleSetting yaw = sgBounce.add(new DoubleSetting
			.Builder()
			.id("yaw")
			.range(-180, 180)
			.sliderRange(-180, 180)
			.defaultValue(0)
			.visible(() -> changeYaw.isVisible() && changeYaw.get())
			.build());

	private final BoolSetting changePitch = sgBounce.add(new BoolSetting
			.Builder()
			.id("change-pitch")
			.defaultValue(true)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final DoubleSetting pitch = sgBounce.add(new DoubleSetting
			.Builder()
			.id("pitch")
			.range(-90, 90)
			.sliderRange(-90, 90)
			.defaultValue(90)
			.visible(() -> changePitch.isVisible() && changePitch.get())
			.build());

	private final BoolSetting autoJump = sgBounce.add(new BoolSetting
			.Builder()
			.id("auto-jump")
			.defaultValue(false)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final BoolSetting customJump = sgBounce.add(new BoolSetting
			.Builder()
			.id("custom-jump")
			.defaultValue(false)
			.visible(() -> mode.get() == ElytraFly.Mode.BOUNCE)
			.build());

	private final DoubleSetting jumpMultiplier = sgBounce.add(new DoubleSetting
			.Builder()
			.id("jump-multiplier")
			.min(0.1)
			.sliderRange(0.1, 2)
			.defaultValue(0.5)
			.visible(() -> customJump.isVisible() && customJump.get())
			.build());

	private final BoolSetting ncpBypass = sgBoost.add(new BoolSetting
			.Builder()
			.id("ncp-bypass")
			.defaultValue(true)
			.visible(() -> mode.get() == ElytraFly.Mode.BOOST)
			.build());

	private final BindSetting boostBind = sgBoost.add(new BindSetting
			.Builder()
			.id("boost-bind")
			.bindAction(function::switchBoost)
			.visible(() -> mode.get() == ElytraFly.Mode.BOOST)
			.defaultValue(Bind.NONE)
			.build());

	private final DoubleSetting boost = sgBoost.add(new DoubleSetting
			.Builder()
			.id("boost")
			.range(0.001, 100)
			.sliderRange(1, 10)
			.defaultValue(3)
			.visible(() -> mode.get() == ElytraFly.Mode.BOOST)
			.build());

	private final DoubleSetting speedLimit = sgBoost.add(new DoubleSetting
			.Builder()
			.id("speed-limit")
			.range(1, 1000)
			.sliderRange(10, 80)
			.defaultValue(60)
			.visible(() -> mode.get() == ElytraFly.Mode.BOOST)
			.suffix(Suffix.BPS)
			.build());

	private final BindSetting stopBind = sgBoostStop.add(new BindSetting
			.Builder()
			.id("stop-bind")
			.visible(() -> mode.get() == ElytraFly.Mode.BOOST)
			.build());

	private final DoubleSetting fallSpeed = sgBoostStop.add(new DoubleSetting
			.Builder()
			.id("fall-speed")
			.sliderRange(0, 1)
			.min(-10)
			.defaultValue(0.3)
			.visible(() -> mode.get() == ElytraFly.Mode.BOOST && stopBind.get().isSet())
			.suffix(Suffix.BPS)
			.build());

	private final BoolSetting stopBypass = sgBoostStop.add(new BoolSetting
			.Builder()
			.id("stop-bypass")
			.defaultValue(true)
			.visible(() -> mode.get() == ElytraFly.Mode.BOOST && stopBind.get().isSet())
			.build());

	public ElytraFlyConfig(ElytraFly function) {
		super(function);
	}
}
