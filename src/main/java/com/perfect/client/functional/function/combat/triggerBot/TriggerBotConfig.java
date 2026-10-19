package com.perfect.client.functional.function.combat.triggerBot;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.core.utils.functions.interact.CombatVersion;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class TriggerBotConfig extends FunctionConfig<TriggerBot> {

	private final SettingGroup sgVersion = settings.createGroup();
	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgHitChance = settings.createGroup();
	private final SettingGroup sgCriticals = settings.createGroup();

	// ——————————————————————————— VERSION —————————————————————————————— //

	private final EnumSetting<CombatVersion> version = sgVersion.add(new EnumSetting
			.Builder<>(CombatVersion.values())
			.id("version")
			.defaultValue(CombatVersion.NEW)
			.onChanged(function::update)
			.build());

	// —————————————————————————— GENERAL —————————————————————————————— //

	private final BoolSetting onAttackButton = sgGeneral.add(new BoolSetting
			.Builder()
			.id("on-attack-key")
			.defaultValue(true)
			.build());

	private final BoolSetting toggleOnDeath = sgGeneral.add(new BoolSetting
			.Builder()
			.id("toggle-on-death")
			.defaultValue(true)
			.build());

	private final BoolSetting ignoreInvisibles = sgGeneral.add(new BoolSetting
			.Builder()
			.id("ignore-invisible")
			.defaultValue(false)
			.build());

	// —————————————————————————— CHANCE —————————————————————————————— //

	private final IntRangeSetting attackDelay = sgHitChance.add(new IntRangeSetting
			.Builder()
			.id("attack-delay")
			.range(0, 10)
			.sliderRange(0, 3)
			.defaultValue(0, 0)
			.visible(this::isNewVersion)
			.onChanged(function::update)
			.build());

	private final DoubleSetting delayChance = sgHitChance.add(new DoubleSetting
			.Builder()
			.id("delay-chance")
			.range(0, 100)
			.sliderRange(0, 100)
			.defaultValue(40)
			.visible(() -> isNewVersion() && attackDelay.getTo() > 0)
			.onChanged(function::update)
			.build());

	private final BoolSetting perfectTiming = sgHitChance.add(new BoolSetting
			.Builder()
			.id("perfect-timing")
			.defaultValue(false)
			.visible(this::isNewVersion)
			.onChanged(function::update)
			.build());

	private final DoubleSetting cpsLimit = sgHitChance.add(new DoubleSetting
			.Builder()
			.id("cps-limit")
			.range(0, 20)
			.sliderRange(0, 20)
			.defaultValue(5)
			.visible(this::isLegacyVersion)
			.onChanged(function::update)
			.build());

	private final DoubleSetting hitChance = sgHitChance.add(new DoubleSetting
			.Builder()
			.id("hit-chance")
			.range(0, 100)
			.sliderRange(0, 100)
			.defaultValue(30)
			.visible(this::isLegacyVersion)
			.onChanged(function::update)
			.build());

	private final BoolSetting damageTiming = sgHitChance.add(new BoolSetting
			.Builder()
			.id("damage-timing")
			.defaultValue(false)
			.visible(this::isLegacyVersion)
			.onChanged(function::update)
			.build());

	// ————————————————————————— CRITICALS ———————————————————————————— //

	private final BoolSetting smartCrit = sgCriticals.add(new BoolSetting
			.Builder()
			.id("smart-crit")
			.defaultValue(true)
			.visible(this::isNewVersion)
			.onChanged(function::update)
			.build());

	private final BoolSetting onlyCrit = sgCriticals.add(new BoolSetting
			.Builder()
			.id("only-crit")
			.defaultValue(false)
			.visible(() -> smartCrit.isVisible() && smartCrit.get())
			.onChanged(function::update)
			.build());

	private final BoolSetting stopSprint = sgCriticals.add(new BoolSetting
			.Builder()
			.id("stop-sprint")
			.defaultValue(true)
			.visible(() -> smartCrit.isVisible() && smartCrit.get())
			.onChanged(function::update)
			.build());

	private final IntRangeSetting preAttackSprintDelay = sgCriticals.add(new IntRangeSetting
			.Builder()
			.id("pre-attack-sprint-delay")
			.defaultValue(1, 2)
			.range(1, 4)
			.visible(() -> stopSprint.isVisible() && stopSprint.get())
			.onChanged(function::update)
			.suffix(Suffix.TICKS)
			.onChanged(function::update)
			.build());

	private final IntRangeSetting postAttackSprintDelay = sgCriticals.add(new IntRangeSetting
			.Builder()
			.id("post-attack-sprint-delay")
			.defaultValue(1, 2)
			.range(0, 4)
			.visible(() -> stopSprint.isVisible() && stopSprint.get())
			.suffix(Suffix.TICKS)
			.onChanged(function::update)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	public TriggerBotConfig(TriggerBot function) {
		super(function);
	}

	private boolean isNewVersion() {
		return version.get(CombatVersion.NEW);
	}

	private boolean isLegacyVersion() {
		return version.get(CombatVersion.LEGACY);
	}
}
