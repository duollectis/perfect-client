package com.perfect.client.functional.function.movement.speed;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class SpeedConfig extends FunctionConfig<Speed> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgStrafe = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();
	private final SettingGroup sgEffectBoost = settings.createGroup();
	private final SettingGroup sgDamageBoost = settings.createGroup();

	// ——————————————————————————————————————————————————————————————————— //

	private final Setting<Speed.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(Speed.Mode.values())
			.id("mode")
			.defaultValue(Speed.Mode.STRAFE)
			.onChangedMode(function::getModeHandler)
			.build());

	// ——————————————————————————————————————————————————————————————————— //

	public final DoubleSetting ncpGroundSpeed = sgStrafe.add(new DoubleSetting
			.Builder()
			.id("speed")
			.defaultValue(5.6)
			.sliderRange(5.0, 9.0)
			.onChanged(i -> function.resetStage())
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.suffix(Suffix.BPS)
			.build());

	public final DoubleSetting ncpStrafeSpeed = sgStrafe.add(new DoubleSetting
			.Builder()
			.id("strafe-multiplier")
			.defaultValue(1.8)
			.sliderRange(0.5, 3.0)
			.onChanged(i -> function.resetStage())
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.build());

	public final BoolSetting ncpStrict = sgStrafe.add(new BoolSetting
			.Builder()
			.id("strict")
			.defaultValue(false)
			.onChanged(i -> function.resetStage())
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.build());

	public final BoolSetting ncpAutoJump = sgStrafe.add(new BoolSetting
			.Builder()
			.id("auto-jump")
			.defaultValue(false)
			.onChanged(i -> function.resetStage())
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.build());

	// ——————————————————————————————————————————————————————————————————— //

	public final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-eat")
			.defaultValue(false)
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.build());

	public final BoolSetting pauseOnShift = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-shift")
			.defaultValue(true)
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.build());

	public final BoolSetting pauseOnElytra = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-elytra")
			.defaultValue(true)
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.build());

	public final BoolSetting pauseInLiquid = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-in-fluid")
			.defaultValue(true)
			.visible(() -> mode.get() == Speed.Mode.STRAFE)
			.build());

	public SpeedConfig(Speed function) {
		super(function);
	}
}
