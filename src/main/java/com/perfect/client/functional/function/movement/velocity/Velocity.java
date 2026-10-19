package com.perfect.client.functional.function.movement.velocity;

import com.perfect.client.core.event.events.entity.EntityPushEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.movement.velocity.modes.GrimMode;
import com.perfect.client.functional.function.movement.velocity.modes.GrimV3Mode;
import com.perfect.client.functional.function.movement.velocity.modes.NormalMode;
import com.perfect.client.functional.function.movement.velocity.modes.WallsMode;
import com.perfect.client.functional.function.movement.velocity.utils.VelocityMode;
import com.perfect.client.inject.mixinInterface.IExplosionS2CPacket;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Getter

public class Velocity extends Function {

	private final VelocityConfig config = new VelocityConfig(this);

	private final ModeContainer<Mode, VelocityMode> modeHandler = createModeContainer(config::getMode)
			.add(Mode.NORMAL, new NormalMode(this, config))
			.add(Mode.WALLS, new WallsMode(this, config))
			.add(Mode.GRIM_V3, new GrimV3Mode(this, config))
			.add(Mode.GRIM, new GrimMode(this, config));

	public Velocity() {
		super("velocity", "Velocity", Category.MOVEMENT, "Anti Knockback");
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	@Override
	public void onDisable() {
		modeHandler.getCurrentMode().disable();
	}

	@Override
	public void onEntityPush(EntityPushEvent event) {
		if (config.getEntities().get() && event.getPushed().equals(mc.player)) {
			event.cancel();
		}
	}

	@Override
	public void onBlockPush(Cancellable event) {
		if (config.getBlocks().get()) {
			event.cancel();
		}
	}

	@Override
	public void onFluidPush(Cancellable event) {
		if (config.getFluids().get()) {
			event.cancel();
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet && config.getKnockback().get()) {
			onUpdateVelocityPacket(event, packet);
		}
		else if (event.getPacket() instanceof ExplosionS2CPacket packet && config.getExplosions().get()) {
			onExplosionPacket(event, packet);
		}
		else if (event.getPacket() instanceof EntityDamageS2CPacket packet) {
			onDamagePacket(event, packet);
		}
		else if (event.getPacket() instanceof EntityStatusS2CPacket packet
				&& packet.getStatus() == EntityStatuses.PULL_HOOKED_ENTITY
				&& config.getFishhook().get()) {
			onFishhookPacket(event, packet);
		}
	}

	private void onUpdateVelocityPacket(Cancellable event, EntityVelocityUpdateS2CPacket packet) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (packet.getEntityId() != mc.player.getId()) {
			return;
		}

		modeHandler.getCurrentMode().onEntityVelocityUpdate(event, packet);
	}

	private void onExplosionPacket(Cancellable event, ExplosionS2CPacket packet) {
		modeHandler.getCurrentMode().onExplosion(event, packet);
	}

	private void onDamagePacket(Cancellable event, EntityDamageS2CPacket packet) {
		modeHandler.getCurrentMode().onEntityDamage(event, packet);
	}

	private void onFishhookPacket(Cancellable event, EntityStatusS2CPacket packet) {
		Entity entity = packet.getEntity(mc.world);
		if (entity instanceof FishingBobberEntity hook && hook.getHookedEntity() == mc.player) {
			event.cancel();
		}
	}

	public void cancelVelocityWalls(Cancellable event, Packet<?> packet) {
		if (isPhased()) {
			cancelVelocity(event, packet);
		}
	}

	public void cancelVelocity(Cancellable event, Packet<?> packet) {
		if (packet instanceof ExplosionS2CPacket p) {
			setZeroVelocity(p);
		}
		else {
			event.cancel();
		}
	}

	public void setZeroVelocity(ExplosionS2CPacket packet) {
		IExplosionS2CPacket p = (IExplosionS2CPacket) (Object) packet;

		assert p != null;
		p.perfect$setVelocity(new Vec3d(0, 0, 0f));
	}

	public boolean isPhased() {
		if (mc.player == null) {
			return false;
		}

		List<BlockPos> intersected = PositionMath.getIntersectedBlocks(mc.player.getBoundingBox());

		for (BlockPos pos : intersected) {
			if (!BlockUtils.isReplaceable(pos)) {
				return true;
			}
		}
		return false;
	}

	public enum Mode {
		NORMAL,
		WALLS,
		GRIM,
		GRIM_V3
	}
}
