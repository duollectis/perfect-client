package com.perfect.client.functional.function.misc.fakePlayer.utils;

import com.perfect.client.core.utils.game.entity.CopyPositionEntity;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.functional.function.misc.fakePlayer.data.PlayerState;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.jetbrains.annotations.Nullable;

import static com.perfect.client.PerfectClient.mc;

public class FakePlayerEntity extends CopyPositionEntity {

	public FakePlayerEntity(PlayerEntity player, String name) {
		super(player, name);

		setHealth(36f);

		updateStatusEffects();

		getInventory().clone(player.getInventory());
		setId(generateId());
	}

	private int generateId() {
		if (mc.world == null) {
			return -1;
		}

		int id = -MathUtils.random(10, 100000);

		if (EntityUtils.isExist(id)) {
			return generateId();
		}

		return id;
	}

	public boolean canBeAttacked() {
		return hurtTime == 0;
	}

	private void updateStatusEffects() {
		clearStatusEffects();

		addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 1));
		addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 3));
		addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 0));
	}

	public void setPositionAndRotation(PlayerState s) {
		setPose(s.pose());

		setYaw(s.yaw());
		setPitch(s.pitch());
		setHeadYaw(s.yaw());

		updateTrackedPositionAndAngles(s.pos(), s.yaw(), s.pitch());
	}

	public void spawn() {
		if (mc.world == null) {
			return;
		}
		unsetRemoved();
		mc.world.addEntity(this);
	}

	public void remove() {
		if (mc.world == null) {
			return;
		}
		mc.world.removeEntity(getId(), RemovalReason.DISCARDED);
		setRemoved(RemovalReason.DISCARDED);
	}

	public void damage(float amount) {
		if (mc.world == null || mc.player == null || mc.player.networkHandler == null) {
			return;
		}

		subtractHealth(amount);
		onDamaged(mc.world.getDamageSources().generic());

		if (isDead()) {
			useTotem();
		}
	}

	private void useTotem() {
		if (mc.player == null || mc.player.networkHandler == null) {
			return;
		}

		setHealth(2f);
		setAbsorptionAmount(10f);

		new EntityStatusS2CPacket(this, EntityStatuses.USE_TOTEM_OF_UNDYING).apply(mc.player.networkHandler);

		updateStatusEffects();
	}

	public void setHealth(float health) {
		super.setHealth(health);

		if (health > 20) {
			super.setAbsorptionAmount(health - 20);
		}
	}

	private void subtractHealth(float amount) {
		float remain = amount - getAbsorptionAmount();

		setAbsorptionAmount(Math.max(getAbsorptionAmount() - amount, 0));
		super.setHealth(getHealth() - Math.max(remain, 0));
	}

	@Nullable
	@Override
	protected PlayerListEntry getPlayerListEntry() {
		if (mc.getNetworkHandler() == null || mc.player == null) {
			return null;
		}

		if (playerListEntry == null) {
			playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
		}

		return playerListEntry;
	}
}
