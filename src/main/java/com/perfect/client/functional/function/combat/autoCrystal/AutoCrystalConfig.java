package com.perfect.client.functional.function.combat.autoCrystal;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.AnimatedCosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.list.EntitiesSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.game.entity.EntityGroup;
import com.perfect.client.core.utils.text.Suffix;
import com.perfect.client.functional.function.combat.autoCrystal.helpers.CrystalSwap;
import lombok.Getter;

@Getter
public class AutoCrystalConfig extends FunctionConfig<AutoCrystal> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgRotate = settings.createGroup();
	private final SettingGroup sgPlace = settings.createGroup();
	private final SettingGroup sgBreak = settings.createGroup();
	private final SettingGroup sgAutoBase = settings.createGroup();
	private final SettingGroup sgPredict = settings.createGroup();
	private final SettingGroup sgDamages = settings.createGroup();
	private final SettingGroup sgFacePlace = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();
	private final SettingGroup sgRenderEffects = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final EntitiesSetting targets = sgGeneral.add(new EntitiesSetting
			.Builder()
			.id("targets")
			.defaultGroups(EntityGroup.PLAYERS)
			.onlyAttackable()
			.build());

	private final DoubleSetting targetRange = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("target-range")
			.defaultValue(10)
			.sliderRange(5, 20)
			.range(1, 50)
			.suffix(Suffix.METERS)
			.build());

	private final BoolSetting ignoreNaked = sgGeneral.add(new BoolSetting
			.Builder()
			.id("ignore-naked")
			.defaultValue(false)
			.build());

	private final BoolSetting ignoreTerrain = sgGeneral.add(new BoolSetting
			.Builder()
			.id("ignore-terrain")
			.defaultValue(true)
			.build());

	// ————————————————————————————— ROTATE ———————————————————————————————— //

	private final EnumSetting<AutoCrystal.Rotate> rotate = sgRotate.add(new EnumSetting
			.Builder<>(AutoCrystal.Rotate.values())
			.id("rotate")
			.defaultValue(AutoCrystal.Rotate.NONE)
			.build());

	private final BoolSetting yawStep = sgRotate.add(new BoolSetting
			.Builder()
			.id("yaw-step")
			.defaultValue(false)
			.visible(() -> rotate.get() != AutoCrystal.Rotate.NONE)
			.build());

	private final IntSetting yawAngle = sgRotate.add(new IntSetting
			.Builder()
			.id("yaw-step")
			.defaultValue(180)
			.range(1, 180)
			.sliderRange(1, 180)
			.visible(() -> yawStep.isVisible() && yawStep.get())
			.build());

	// —————————————————————————————— PLACE ———————————————————————————————— //

	private final EnumSetting<CrystalSwap> swap = sgPlace.add(new EnumSetting
			.Builder<>(CrystalSwap.values())
			.id("swap")
			.defaultValue(CrystalSwap.HOTBAR)
			.build());

	private final IntSetting placeDelay = sgPlace.add(new IntSetting
			.Builder()
			.id("place-delay")
			.defaultValue(0)
			.range(0, 1000)
			.sliderRange(0, 300)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final DoubleSetting placeRange = sgPlace.add(new DoubleSetting
			.Builder()
			.id("place-range")
			.defaultValue(5)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting wallsPlaceRange = sgPlace.add(new DoubleSetting
			.Builder()
			.id("walls-place-range")
			.defaultValue(5)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.toggleable(true)
			.build());

	private final EnumSetting<AutoCrystal.RayTraceMode> rayTraceMode = sgPlace.add(new EnumSetting
			.Builder<>(AutoCrystal.RayTraceMode.values())
			.id("ray-trace-mode")
			.defaultValue(AutoCrystal.RayTraceMode.NONE)
			.build());

	private final BoolSetting sequential = sgPlace.add(new BoolSetting
			.Builder()
			.id("sequential")
			.defaultValue(false)
			.build());

	private final BoolSetting antiHitboxDesync = sgPlace.add(new BoolSetting
			.Builder()
			.id("anti-hitbox-desync")
			.defaultValue(false)
			.build());

	private final BoolSetting lowerHitbox = sgPlace.add(new BoolSetting
			.Builder()
			.id("lower-hitbox")
			.defaultValue(false)
			.build());

	private final BoolSetting legacy = sgPlace.add(new BoolSetting
			.Builder()
			.id("legacy")
			.defaultValue(false)
			.build());

	// —————————————————————————————— BREAK ———————————————————————————————— //

	private final IntSetting breakDelay = sgBreak.add(new IntSetting.Builder()
			.id("break-delay")
			.defaultValue(0)
			.range(0, 1000)
			.sliderRange(0, 300)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final DoubleSetting breakRange = sgBreak.add(new DoubleSetting
			.Builder()
			.id("break-range")
			.defaultValue(5)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting wallsBreakRange = sgBreak.add(new DoubleSetting
			.Builder()
			.id("walls-break-range")
			.defaultValue(5)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.toggleable(true)
			.build());

	private final IntSetting crystalAge = sgBreak.add(new IntSetting
			.Builder()
			.id("crystal-age")
			.defaultValue(0)
			.range(0, 200)
			.sliderRange(0, 200)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final BoolSetting await = sgBreak.add(new BoolSetting
			.Builder()
			.id("await")
			.defaultValue(false)
			.build());

	private final BoolSetting inhibit = sgBreak.add(new BoolSetting
			.Builder()
			.id("inhibit")
			.defaultValue(false)
			.build());

	private final IntSetting breakAttempts = sgBreak.add(new IntSetting
			.Builder()
			.id("break-attempts")
			.defaultValue(1)
			.range(1, 10)
			.visible(inhibit::get)
			.build());

	private final IntSetting retryDelay = sgBreak.add(new IntSetting
			.Builder()
			.id("retry-delay")
			.range(0, 1000)
			.sliderRange(0, 300)
			.suffix(Suffix.MILLISECONDS)
			.visible(() -> breakAttempts.isVisible() && breakAttempts.get() > 1)
			.build());

	// ——————————————————————————— AUTO-BASE ———————————————————————————— //

	private final BoolSetting support = sgAutoBase.add(new BoolSetting
			.Builder()
			.id("auto-base")
			.defaultValue(false)
			.build());

	private final IntSetting supportDelay = sgAutoBase.add(new IntSetting
			.Builder()
			.id("base-delay")
			.range(0, 2000)
			.sliderRange(0, 500)
			.suffix(Suffix.MILLISECONDS)
			.visible(support::get)
			.build());

	// ———————————————————————————— PREDICT —————————————————————————————— //

	private final IntSetting extrapolation = sgPredict.add(new IntSetting
			.Builder()
			.id("extrapolation")
			.defaultValue(3)
			.range(0, 20)
			.suffix(Suffix.TICKS)
			.build());

	// ———————————————————————————— DAMAGES —————————————————————————————— //

	private final DoubleSetting minDamage = sgDamages.add(new DoubleSetting
			.Builder()
			.id("min-damage")
			.defaultValue(10)
			.range(0, 10000)
			.sliderRange(0, 36)
			.build());

	private final DoubleSetting minDamageRatio = sgDamages.add(new DoubleSetting
			.Builder()
			.id("min-damage-ratio")
			.sliderRange(0, 3)
			.range(0, 10)
			.defaultValue(0.5)
			.build());

	private final DoubleSetting maxSelfDamage = sgDamages.add(new DoubleSetting
			.Builder()
			.id("max-self-damage")
			.defaultValue(5)
			.range(0, 10000)
			.sliderRange(0, 36)
			.build());

	private final BoolSetting antiSelfPop = sgDamages.add(new BoolSetting
			.Builder()
			.id("anti-self-pop")
			.defaultValue(true)
			.build());

	private final BoolSetting antiFriendPop = sgDamages.add(new BoolSetting
			.Builder()
			.id("anti-friend-pop")
			.defaultValue(true)
			.build());

	private final BoolSetting antiResourcesExplosion = sgDamages.add(new BoolSetting
			.Builder()
			.id("anti-resources-explosion")
			.defaultValue(false)
			.build());

	private final DoubleSetting minHealth = sgDamages.add(new DoubleSetting
			.Builder()
			.id("min-health")
			.defaultValue(6)
			.range(0, 10000)
			.sliderRange(0, 36)
			.visible(() -> antiSelfPop.get() || antiFriendPop.get())
			.build());

	// ———————————————————————————— FACE PLACE ————————————————————————————— //

	private final BoolSetting facePlace = sgFacePlace.add(new BoolSetting
			.Builder()
			.id("face-place")
			.defaultValue(true)
			.build());

	private final EnumSetting<AutoCrystal.Slowness> facePlaceSlowness = sgFacePlace.add(new EnumSetting
			.Builder<>(AutoCrystal.Slowness.values())
			.id("face-place-slowness")
			.defaultValue(AutoCrystal.Slowness.PLACE)
			.visible(facePlace::get)
			.build());

	private final DoubleSetting facePlaceHp = sgFacePlace.add(new DoubleSetting
			.Builder()
			.id("face-place-hp")
			.defaultValue(5)
			.range(0, 10000)
			.sliderRange(0, 36)
			.visible(facePlace::get)
			.build());

	private final DoubleSetting facePlaceArmor = sgFacePlace.add(new DoubleSetting
			.Builder()
			.id("face-place-armor")
			.defaultValue(5)
			.range(0, 100)
			.sliderRange(0, 20)
			.visible(facePlace::get)
			.suffix(Suffix.PERCENT)
			.build());

	private final IntSetting facePlaceDelay = sgFacePlace.add(new IntSetting
			.Builder()
			.id("face-place-delay")
			.range(0, 2000)
			.sliderRange(0, 800)
			.defaultValue(480)
			.suffix(Suffix.MILLISECONDS)
			.visible(facePlace::get)
			.build()
	);

	// —————————————————————————————— PAUSE ———————————————————————————————— //

	private final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-eat")
			.defaultValue(true)
			.build());

	private final BoolSetting pauseOnAutoExp = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-auto-exp")
			.defaultValue(true)
			.build());

	private final BoolSetting pauseOnBlink = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-blink")
			.defaultValue(true)
			.build());

	// ————————————————————————————— RENDER ———————————————————————————————— //

	private final BoolSetting swingHand = sgRender.add(new BoolSetting
			.Builder()
			.id("swing-hand")
			.defaultValue(true)
			.build());

	private final BoolSetting render = sgRender.add(new BoolSetting
			.Builder()
			.id("render")
			.defaultValue(true)
			.build());

	private final BundleSetting<AnimatedCosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
			.Builder<AnimatedCosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new AnimatedCosmeticsBundle())
			.build());

	private final MultiColorSetting lineColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.visible(render::get)
			.build());

	private final MultiColorSetting sideColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.visible(render::get)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	public AutoCrystalConfig(AutoCrystal function) {
		super(function);
	}
}
