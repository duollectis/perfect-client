package com.perfect.client.functional.function.combat.attackAura;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.list.EntitiesSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.functional.setting.impl.number.range.DoubleRangeSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.core.utils.functions.interact.CombatVersion;
import com.perfect.client.core.utils.functions.target.TargetPriority;
import com.perfect.client.core.utils.game.entity.EntityGroup;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;
import net.minecraft.entity.EntityType;

@Getter
public class AttackAuraConfig extends FunctionConfig<AttackAura> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgHitChance = settings.createGroup();
	private final SettingGroup sgCriticals = settings.createGroup();
	private final SettingGroup sgRanges = settings.createGroup();
	private final SettingGroup sgTarget = settings.createGroup();
	private final SettingGroup sgRaytrace = settings.createGroup();
	private final SettingGroup sgIgnore = settings.createGroup();
	private final SettingGroup sgRotate = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final EnumSetting<AttackAura.Weapon> weapon = sgGeneral.add(new EnumSetting
			.Builder<>(AttackAura.Weapon.values())
			.id("weapon")
			.defaultValue(AttackAura.Weapon.ANY)
			.build());

	private final EnumSetting<CombatVersion> combatVersion = sgGeneral.add(new EnumSetting
			.Builder<>(CombatVersion.values())
			.id("version")
			.defaultValue(CombatVersion.NEW)
			.onChanged(function::setCombatVersion)
			.build());

	// ————————————————————————————— HIT CHANCE ——————————————————————————————— //

	private final IntRangeSetting attackDelay = sgHitChance.add(new IntRangeSetting
			.Builder()
			.id("attack-delay")
			.range(0, 10)
			.sliderRange(0, 3)
			.defaultValue(0, 0)
			.visible(this::isNewCombat)
			.onChanged(function::updateCombatVersionConfig)
			.build());

	private final DoubleSetting delayChance = sgHitChance.add(new DoubleSetting
			.Builder()
			.id("delay-chance")
			.range(0, 100)
			.sliderRange(0, 100)
			.defaultValue(40)
			.visible(() -> isNewCombat() && attackDelay.getTo() > 0)
			.onChanged(function::updateCombatVersionConfig)
			.build());

	private final BoolSetting perfectTiming = sgHitChance.add(new BoolSetting
			.Builder()
			.id("perfect-timing")
			.defaultValue(false)
			.visible(this::isNewCombat)
			.onChanged(function::updateCombatVersionConfig)
			.build());

	private final DoubleSetting cpsLimit = sgHitChance.add(new DoubleSetting
			.Builder()
			.id("cps-limit")
			.range(0, 20)
			.sliderRange(0, 20)
			.defaultValue(5)
			.visible(this::isLegacyCombat)
			.onChanged(function::updateCombatVersionConfig)
			.build());

	private final DoubleSetting hitChance = sgHitChance.add(new DoubleSetting
			.Builder()
			.id("hit-chance")
			.range(0, 100)
			.sliderRange(0, 100)
			.defaultValue(30)
			.onChanged(function::updateCombatVersionConfig)

			.visible(this::isLegacyCombat)
			.build());

	private final BoolSetting damageTiming = sgHitChance.add(new BoolSetting
			.Builder()
			.id("damage-timing")
			.defaultValue(false)
			.onChanged(function::updateCombatVersionConfig)
			.visible(this::isLegacyCombat)
			.build());

	// ————————————————————————————— CRITICALS ——————————————————————————————— //

	private final BoolSetting smartCrit = sgCriticals.add(new BoolSetting
			.Builder()
			.id("smart-crit")
			.defaultValue(false)
			.visible(this::isNewCombat)
			.build());

	private final BoolSetting onlyCrit = sgCriticals.add(new BoolSetting
			.Builder()
			.id("only-crit")
			.defaultValue(false)
			.visible(() -> smartCrit.isVisible() && smartCrit.get())
			.onChanged(function::updateCombatVersionConfig)
			.build());

	private final BoolSetting stopSprint = sgCriticals.add(new BoolSetting
			.Builder()
			.id("stop-sprint")
			.defaultValue(false)
			.visible(() -> isNewCombat() && smartCrit.get())
			.build());

	private final IntRangeSetting preAttackSprintDelay = sgCriticals.add(new IntRangeSetting
			.Builder()
			.id("pre-attack-sprint-delay")
			.defaultValue(1, 2)
			.range(1, 4)
			.visible(() -> stopSprint.isVisible() && stopSprint.get())
			.onChanged(function::updateCombatVersionConfig)
			.suffix(Suffix.TICKS)
			.build());

	private final IntRangeSetting postAttackSprintDelay = sgCriticals.add(new IntRangeSetting
			.Builder()
			.id("post-attack-sprint-delay")
			.defaultValue(1, 2)
			.range(0, 4)
			.visible(() -> stopSprint.isVisible() && stopSprint.get())
			.suffix(Suffix.TICKS)
			.build());

	// ————————————————————————————— RANGES ——————————————————————————————— //

	private final DoubleSetting targetRange = sgRanges.add(new DoubleSetting.Builder()
			.id("target-range")
			.defaultValue(3.5)
			.sliderRange(0, 7)
			.range(0, 10)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting attackRange = sgRanges.add(new DoubleSetting.Builder()
			.id("attack-range")
			.defaultValue(3)
			.sliderRange(0, 7)
			.range(0, 10)
			.suffix(Suffix.METERS)
			.build());

	// ————————————————————————————— TARGET ——————————————————————————————— //

	private final EntitiesSetting targets = sgTarget.add(new EntitiesSetting
			.Builder()
			.id("targets")
			.defaultGroups(EntityGroup.PLAYERS, EntityGroup.MONSTERS)
			.defaultValue(EntityType.PLAYER)
			.onlyAttackable()
			.build());

	private final EnumSetting<TargetPriority> priority = sgTarget.add(new EnumSetting
			.Builder<>(TargetPriority.values())
			.id("target-priority")
			.defaultValue(TargetPriority.FIELD_OF_VIEW)
			.build());

	private final DoubleSetting fieldOfView = sgTarget.add(new DoubleSetting
			.Builder()
			.id("field-of-view")
			.range(0, 180)
			.defaultValue(180)
			.sliderRange(0, 180)
			.build());

	private final BoolSetting targeting = sgTarget.add(new BoolSetting
			.Builder()
			.id("targeting")
			.defaultValue(false)
			.build());

	// ————————————————————————————— IGNORE ——————————————————————————————— //

	private final BoolSetting ignorePassive = sgIgnore.add(new BoolSetting
			.Builder()
			.id("ignore-passive")
			.defaultValue(true)
			.build());

	private final BoolSetting ignoreNamed = sgIgnore.add(new BoolSetting
			.Builder()
			.id("ignore-named")
			.defaultValue(false)
			.build());

	private final BoolSetting ignoreSitting = sgIgnore.add(new BoolSetting
			.Builder()
			.id("ignore-sitting")
			.defaultValue(false)
			.build());

	private final BoolSetting ignoreInvisible = sgIgnore.add(new BoolSetting
			.Builder()
			.id("ignore-invisible")
			.defaultValue(false)
			.build());

	private final BoolSetting ignoreNaked = sgIgnore.add(new BoolSetting
			.Builder()
			.id("ignore-naked")
			.defaultValue(false)
			.build());

	// ————————————————————————————— RAYTRACE ——————————————————————————————— //

	private final BoolSetting raytraceBlocks = sgRaytrace.add(new BoolSetting
			.Builder()
			.id("raytrace-blocks")
			.defaultValue(false)
			.build());

	private final BoolSetting raytraceEntities = sgRaytrace.add(new BoolSetting
			.Builder()
			.id("raytrace-entities")
			.defaultValue(false)
			.build());

	// ————————————————————————————— ROTATE ——————————————————————————————— //

	private final EnumSetting<AttackAura.RotateType> rotate = sgRotate.add(new EnumSetting
			.Builder<>(AttackAura.RotateType.values())
			.id("rotate")
			.defaultValue(AttackAura.RotateType.NONE)
			.onChanged(function::updateRotationMode)
			.build());

	private final BoolSetting legitRotate = sgRotate.add(new BoolSetting
			.Builder()
			.id("legit-rotate")
			.visible(this::isNormalRotate)
			.defaultValue(false)
			.onChanged(function::updateRotationMode)
			.build());

	private final DoubleSetting legitRotationSpeed = sgRotate.add(new DoubleSetting
			.Builder()
			.id("rotation-speed")
			.sliderRange(0.0, 2.0)
			.range(0.0, 3.0)
			.defaultValue(1.0)
			.visible(() -> isNormalRotate() && legitRotate.get())
			.build());

	private final DoubleRangeSetting legitRotationAlpha = sgRotate.add(new DoubleRangeSetting
			.Builder()
			.id("rotation-alpha")
			.sliderRange(0.0, 1.0)
			.range(0.0, 1.0)
			.defaultValue(0.55, 0.7)
			.visible(() -> isNormalRotate() && legitRotate.get())
			.build());

	private final IntSetting snapTime = sgRotate.add(new IntSetting.Builder()
			.id("snap-time")
			.range(1, 5)
			.sliderRange(1, 5)
			.defaultValue(1)
			.visible(() -> rotate.get(AttackAura.RotateType.SNAP) && !legitRotate.get())
			.suffix(Suffix.TICKS)
			.build());

	private final DoubleSetting yawStep = sgRotate.add(new DoubleSetting.Builder()
			.id("yaw-step")
			.range(0, 180)
			.sliderRange(0, 180)
			.defaultValue(60)
			.visible(() -> isNormalRotate() && !legitRotate.get())
			.build());

	private final DoubleSetting pitchStep = sgRotate.add(new DoubleSetting.Builder()
			.id("pitch-step")
			.range(0, 180)
			.sliderRange(0, 180)
			.defaultValue(60)
			.visible(() -> isNormalRotate() && !legitRotate.get())
			.build());

	// ————————————————————————————— PAUSE ——————————————————————————————— //

	private final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-eat")
			.defaultValue(false)
			.build());

	private final BoolSetting pauseOnBlink = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-blink")
			.defaultValue(true)
			.build());

	private final BoolSetting pauseOnAutoCrystal = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-auto-crystal")
			.defaultValue(true)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	public AttackAuraConfig(AttackAura function) {
		super(function);
	}

	private boolean isNewCombat() {
		return combatVersion.get(CombatVersion.NEW);
	}

	private boolean isLegacyCombat() {
		return combatVersion.get(CombatVersion.LEGACY);
	}

	private boolean isRotate() {
		return !rotate.get(AttackAura.RotateType.NONE);
	}

	public boolean isNormalRotate() {
		return isRotate() && !rotate.get(AttackAura.RotateType.PACKET);
	}
}
