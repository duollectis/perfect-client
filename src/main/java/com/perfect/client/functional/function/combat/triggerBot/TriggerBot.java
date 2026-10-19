package com.perfect.client.functional.function.combat.triggerBot;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionConfig;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionHandler;
import com.perfect.client.core.functional.function.combatVersion.impl.LegacyCombatVersion;
import com.perfect.client.core.functional.function.combatVersion.impl.NewCombatVersion;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.utils.functions.interact.CombatVersion;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;


public class TriggerBot extends Function {

	private final TriggerBotConfig config = new TriggerBotConfig(this);

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
			= createModeContainer(config::getVersion)
			.add(CombatVersion.NEW, new NewCombatVersion<>(this, combatVersionConfig))
			.add(CombatVersion.LEGACY, new LegacyCombatVersion<>(this, combatVersionConfig));

	public TriggerBot() {
		super("trigger-bot", "Trigger Bot", Category.COMBAT, "Auto Attack");
	}

	public void update() {
		if (combatVersion.getCurrentMode() != null) {
			combatVersion.getCurrentMode().update();
		}
	}

	@Override
	public void onRespawn() {
		if (config.getToggleOnDeath().get()) {
			disable();
		}
	}

	
	@Override
	public void onHandleInput() {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		if (!(mc.crosshairTarget instanceof EntityHitResult entityHitResult)) {
			return;
		}

		Entity entity = entityHitResult.getEntity();

		if (!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		if (canAttack() && isGoodTarget(livingEntity) && combatVersion.getCurrentMode().shouldAttack(livingEntity)) {
			combatVersion.getCurrentMode().preAttack();

			mc.interactionManager.attackEntity(mc.player, entity);
			mc.player.swingHand(Hand.MAIN_HAND);

			combatVersion.getCurrentMode().postAttack();
		}
	}

	public boolean isGoodTarget(LivingEntity entity) {
		if (!entity.isAlive()) {
			return false;
		}
		return !(config.getIgnoreInvisibles().get() && EntityUtils.isInvisible(entity));
	}

	public boolean canAttack() {
		if (mc.player == null) {
			return false;
		}
		if (mc.player.isUsingItem()) {
			return false;
		}
		return checkAttackKey();
	}

	private boolean checkAttackKey() {
		if (!config.getOnAttackButton().get()) {
			return true;
		}
		return mc.options.attackKey.isPressed();
	}
}
