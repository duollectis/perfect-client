package com.perfect.client.manager.game;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.game.PacketUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

import java.util.Random;
import java.util.UUID;

public class EntityManager extends Manager {

	private volatile int lastId;

	private final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap<>();
	private final Object2IntMap<UUID> chatIdMap = new Object2IntOpenHashMap<>();
	private final Random random = new Random();

	@Override
	public void onGameJoin() {
		totemPopMap.clear();
		chatIdMap.clear();
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof EntityTrackerUpdateS2CPacket packet) {
			if (packet.id() > lastId) {
				lastId = packet.id();
			}
		}
		else if (event.getPacket() instanceof EntitySpawnS2CPacket packet) {
			if (packet.getEntityId() > lastId) {
				lastId = packet.getEntityId();
			}
		}
		else if (event.getPacket() instanceof EntitiesDestroyS2CPacket packet) {
			for (int id : packet.getEntityIds()) {
				if (id > lastId) {
					lastId = id;
				}
			}
		}
		else if (event.getPacket() instanceof EntityStatusS2CPacket packet) {
			if (!PacketUtils.isTotemPop(packet)) {
				return;
			}

			Entity entity = packet.getEntity(mc.world);

			if (!(entity instanceof PlayerEntity player))
				return;

			synchronized (totemPopMap) {
				int pops = totemPopMap.getOrDefault(entity.getUuid(), 0);
				totemPopMap.put(entity.getUuid(), ++pops);

				PerfectClient.EVENT_MANAGER.onPlayerTotemPop(new PlayerEvent(
						player,
						getChatId(entity),
						pops
				));
			}
		}
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null)
			return;

		synchronized (totemPopMap) {
			for (PlayerEntity player : mc.world.getPlayers()) {
				if (!totemPopMap.containsKey(player.getUuid()))
					continue;

				if (player.deathTime > 0 || player.getHealth() <= 0) {
					int pops = totemPopMap.removeInt(player.getUuid());

					PerfectClient.EVENT_MANAGER.onPlayerDeath(new PlayerEvent(
							player,
							getChatId(player),
							pops
					));

					chatIdMap.removeInt(player.getUuid());
				}
			}
		}
	}

	private int getChatId(Entity entity) {
		return chatIdMap.computeIfAbsent(entity.getUuid(), value -> random.nextInt());
	}

	public int getPops(Entity entity) {
		return totemPopMap.getOrDefault(entity.getUuid(), 0);
	}

	public int getId() {
		return lastId;
	}
}