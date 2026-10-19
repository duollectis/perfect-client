package com.perfect.client.functional.function.combat.attackAura;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionConfig;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionHandler;
import com.perfect.client.core.functional.function.combatVersion.impl.LegacyCombatVersion;
import com.perfect.client.core.functional.function.combatVersion.impl.NewCombatVersion;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.interact.CombatVersion;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.combat.attackAura.data.AttackInfo;
import com.perfect.client.functional.function.combat.attackAura.helpers.RotateHelper;
import com.perfect.client.functional.function.combat.attackAura.helpers.TargetHelper;
import com.perfect.client.functional.function.combat.attackAura.rotate.*;
import com.perfect.client.functional.function.combat.attackAura.utils.RotateHandler;
import com.perfect.client.manager.game.targetManager.ITarget;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

import java.awt.*;

import static com.perfect.client.manager.system.FunctionManager.autoCrystal;

@Getter

public class AttackAura extends Function implements ITarget {

	private final AttackAuraConfig config = new AttackAuraConfig(this);

	private final TargetHelper targetHelper = new TargetHelper(this, config);
	private final RotateHelper rotateHelper = new RotateHelper(this, config);

	private AttackInfo attackInfo;

	private boolean paused;

	private boolean rotated = false;

	private final CombatVersionConfig combatVersionConfig = new CombatVersionConfig(
			() -> config.getPreAttackSprintDelay().get(),
			() -> config.getPostAttackSprintDelay().get(),
			() -> config.getStopSprint().get(),
			() -> config.getSmartCrit().get(),
			() -> config.getOnlyCrit().get(),
			() -> config.getAttackDelay().get(),
			() -> config.getDelayChance().fromPercent(),
			() -> config.getPerfectTiming().get(),
			() -> config.getCpsLimit().get(),
			() -> config.getDamageTiming().get(),
			() -> config.getHitChance().get()
	);

	private final ModeContainer<CombatVersion, CombatVersionHandler<AttackAura, AttackAuraConfig>> combatVersion
			= createModeContainer(config::getCombatVersion)
			.add(CombatVersion.NEW, new NewCombatVersion<>(this, combatVersionConfig))
			.add(CombatVersion.LEGACY, new LegacyCombatVersion<>(this, combatVersionConfig));

	private final ModeContainer<RotateImpl, RotateHandler> rotate = addHandler(
			new ModeContainer<>(this::getRotateMode, this)
	).add(RotateImpl.NONE, new NoneRotate(this, config))
	 .add(RotateImpl.PACKET, new NoneRotate(this, config))
	 .add(RotateImpl.SNAP, new SnapRotate(this, config))
	 .add(RotateImpl.TRACK, new TrackRotate(this, config))
	 .add(RotateImpl.LEGIT_SNAP, new NeuroHandler(this, config))
	 .add(RotateImpl.LEGIT_TRACK, new LegitTrackRotate(this, config));

	public AttackAura() {
		super("attack-aura-2", "Attack Aura", Category.COMBAT, "Kill Aura", "Auto Attack", "Aura");
	}

	@Override
	public String getStatus() {
		LivingEntity target = getTarget();
		String mode = StringUtils.enumToFormattedString(config.getRotate().get());

		if (target == null) {
			return "None, %s".formatted(mode);
		}

		String targetName = target.getType().getName().getString();
		if (target instanceof PlayerEntity player) {
			targetName = player.getGameProfile().getName();
		}

		return "%s, %s".formatted(targetName, mode);
	}

	public void setCombatVersion(CombatVersion version) {
		combatVersion.set(version);
	}

	public void updateRotationMode() {
		rotate.set(getRotateMode());
	}

	public void updateCombatVersionConfig() {
		if (combatVersion.getCurrentMode() != null) {
			combatVersion.getCurrentMode().update();
		}
	}

	private RotateImpl getRotateMode() {
		if (config.getLegitRotate().get()) {
			return switch (config.getRotate().get()) {
				case SNAP -> RotateImpl.LEGIT_SNAP;
				case TRACK -> RotateImpl.LEGIT_TRACK;
				default -> RotateImpl.NONE;
			};
		}

		return switch (config.getRotate().get()) {
			case NONE -> RotateImpl.NONE;
			case PACKET -> RotateImpl.PACKET;
			case SNAP -> RotateImpl.SNAP;
			case TRACK -> RotateImpl.TRACK;
		};
	}

	@Override
	public void onDisable() {
		reset();
	}

	@Override
	public void onGameLeft() {
		reset();
	}

	@Override
	public void onRespawn() {
		reset();
	}

	private void reset() {
		attackInfo = null;
		rotated = false;
	}

	
	@Override
	public void onRotate() {
		if (mc.player == null || mc.world == null || mc.interactionManager == null) {
			return;
		}

		paused = shouldPause();

		if (paused) {
			combatVersion.getCurrentMode().setPaused(true);
			return;
		}

		if (needUpdateAttackInfo()) {
			updateAttackInfo();
		}

		if (attackInfo == null) {
			combatVersion.getCurrentMode().setPaused(true);
			return;
		}

		combatVersion.getCurrentMode().setPaused(false);

		rotated = rotate.getCurrentMode().rotate(attackInfo);

		if (rotated && combatVersion.getCurrentMode().shouldAttack(attackInfo.getEntity())) {
			preAttack();

			mc.interactionManager.attackEntity(mc.player, attackInfo.getEntity());
			mc.player.swingHand(Hand.MAIN_HAND);

			postAttack();
		}
	}

	private void preAttack() {
		combatVersion.getCurrentMode().preAttack();

		if (config.getRotate().get(RotateType.PACKET)) {
			Managers.ROTATION.packetRotate(attackInfo.getAttackVec3d());
		}
	}

	private void postAttack() {
		combatVersion.getCurrentMode().postAttack();
		updateAttackInfo();
	}

	private boolean shouldPause() {
		return !isHoldingWeapon()
				|| PlayerUtils.shouldPause(config.getPauseOnEat().get(), config.getPauseOnBlink().get())
				|| (config.getPauseOnAutoCrystal().get() && autoCrystal.shouldPauseOther());
	}

	private boolean isHoldingWeapon() {
		if (mc.player == null) {
			return false;
		}

		return switch (config.getWeapon().get()) {
			case AXE -> mc.player.getMainHandStack().getItem() instanceof AxeItem;
			case SWORD -> mc.player.getMainHandStack().isIn(ItemTags.SWORDS);
			default -> true;
		};
	}

	private void updateAttackInfo() {
		attackInfo = targetHelper.getAttackInfo();
	}

	private boolean needUpdateAttackInfo() {
		if (!config.getLegitRotate().get()) {
			return true;
		}
		if (attackInfo == null) {
			return true;
		}
		if (!EntityUtils.isExist(attackInfo.getEntity())) {
			return true;
		}
		return !targetHelper.isGoodAttackPoint(attackInfo.getEntity(), attackInfo.getAttackPoint()).isGood();
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (attackInfo != null) {
			Box b = PositionMath.boxFromCenter(0.05, attackInfo.getAttackVec3d());

			BoxRenderer.drawBox(
					event.getMatrices(),
					b,
					new MultiColor(Color.WHITE),
					new MultiColor(new Color(255, 255, 255, 0)),
					Cosmetics.createEmpty()
			);
		}
	}

	@Override
	public LivingEntity getTarget() {
		if (paused || attackInfo == null) {
			return null;
		}
		return attackInfo.getEntity();
	}

	public enum RotateImpl {
		NONE,
		PACKET,
		SNAP,
		LEGIT_SNAP,
		TRACK,
		LEGIT_TRACK
	}

	public enum RotateType {
		NONE,
		PACKET,
		SNAP,
		TRACK
	}

	public enum Weapon {
		SWORD,
		AXE,
		ANY
	}
}
