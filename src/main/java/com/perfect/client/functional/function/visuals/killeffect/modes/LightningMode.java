package com.perfect.client.functional.function.visuals.killeffect.modes;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.functional.function.visuals.killeffect.KillEffect;
import com.perfect.client.functional.function.visuals.killeffect.KillEffectConfig;
import com.perfect.client.functional.function.visuals.killeffect.data.KillEffectMode;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.Vec3d;

public class LightningMode extends KillEffectMode {

	public LightningMode(
			KillEffect function,
			KillEffectConfig config
	) {
		super(function, config);
	}

	@Override
	public void onPlayerDeath(PlayerEvent event) {
		if (mc.world == null) {
			return;
		}

		Vec3d pos = event.getPlayer().getPos();

		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();

		LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);

		lightning.updatePosition(x, y, z);
		lightning.refreshPositionAfterTeleport(x, y, z);

		mc.world.addEntity(lightning);
	}
}