package com.perfect.client.functional.function.combat.autoCrystal.handlers;

import com.google.common.collect.Lists;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class CrystalBreakHandler extends FunctionHandler<AutoCrystal, AutoCrystalConfig> {

	public CrystalBreakHandler(AutoCrystal function, AutoCrystalConfig config) {
		super(function, config);
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof ExplosionS2CPacket packet) {
			onExplosionPacket(packet);
		}
		else if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
			onPlaySoundPacket(packet);
		}
		else if (event.getPacket() instanceof EntitiesDestroyS2CPacket packet) {
			onEntitiesDestroyPacket(packet);
		}
	}

	
	private void onExplosionPacket(ExplosionS2CPacket packet) {
		if (mc.world == null) {
			return;
		}

		for (Entity entity : Lists.newArrayList(mc.world.getEntities())) {

			if (!isEndCrystal(entity, packet)) {
				continue;
			}

			mc.executeSync(() -> mc.world.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED));

			Long attackTime = function.getAttackPackets().remove(entity.getId());

			if (attackTime != null) {
				function.getAttackLatency().add(function.getTime() - attackTime);
			}
		}
	}

	
	private void onPlaySoundPacket(PlaySoundS2CPacket packet) {
		if (mc.world == null) {
			return;
		}
		if (!isExplodeSound(packet)) {
			return;
		}

		for (Entity entity : Lists.newArrayList(mc.world.getEntities())) {
			if (!isEndCrystal(entity, packet)) {
				continue;
			}

			mc.executeSync(() -> mc.world.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED));

			Long attackTime = function.getAttackPackets().remove(entity.getId());

			if (attackTime != null) {
				function.getAttackLatency().add(function.getTime() - attackTime);
			}
		}
	}

	
	private void onEntitiesDestroyPacket(EntitiesDestroyS2CPacket packet) {
		for (int id : packet.getEntityIds()) {

			Long attackTime = function.getAttackPackets().remove(id);

			if (attackTime != null) {
				function.getAttackLatency().add(function.getTime() - attackTime);
			}
		}
	}

	private boolean isExplodeSound(PlaySoundS2CPacket packet) {
		return packet.getSound().value() == SoundEvents.ENTITY_GENERIC_EXPLODE.value()
				&& packet.getCategory() == SoundCategory.BLOCKS;
	}

	private boolean isEndCrystal(Entity entity, PlaySoundS2CPacket packet) {
		return isEndCrystal(entity, packet.getX(), packet.getY(), packet.getZ());
	}

	private boolean isEndCrystal(Entity entity, ExplosionS2CPacket packet) {
		Vec3d pos = packet.center();
		return isEndCrystal(entity, pos.getX(), pos.getY(), pos.getZ());
	}

	private boolean isEndCrystal(Entity entity, double x, double y, double z) {
		if (!(entity instanceof EndCrystalEntity)) {
			return false;
		}

		return entity.squaredDistanceTo(x, y, z) < 30.0;
	}
}
