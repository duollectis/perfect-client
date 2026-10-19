package com.perfect.client.functional.function.combat.crystalOptimizer;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.inject.mixinInterface.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.Packet;


public class CrystalOptimizer extends Function {

	private final CrystalOptimizerConfig config = new CrystalOptimizerConfig(this);

	public CrystalOptimizer() {
		super("crystal-optimizer", "Crystal Optimizer", Category.COMBAT, "Multi Task", "Crystal Helper");
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		Entity entity = getEntityFromPacket(event.getPacket());

		if (entity == null || entity.getType() != EntityType.END_CRYSTAL) {
			return;
		}

		if (config.getSetDead().get()) {
			entity.remove(Entity.RemovalReason.KILLED);
		}
	}

	@Override
	public void onMultiTask(Cancellable event) {
		if (config.getMultiTask().get()) {
			event.cancel();
		}
	}

	private Entity getEntityFromPacket(Packet<?> p) {
		if (p instanceof IPlayerInteractEntityC2SPacket packet && packet.perfect$isAttack()) {
			return packet.perfect$getEntity();
		}

		return null;
	}
}
