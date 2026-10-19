package com.perfect.client.functional.function.player.noServerRotate;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.Angle;
import com.perfect.client.inject.mixinInterface.IPlayerPosition;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import static com.perfect.client.manager.system.FunctionManager.elytraFly;


public class NoServerRotate extends Function {

	private final NoServerRotateConfig config = new NoServerRotateConfig(this);

	private Angle strictAngle;

	public NoServerRotate() {
		super("no-server-rotate", "No Server Rotate", Category.PLAYER, "No Rotate");
	}

	@Override
	public void onEnable() {
		reset();
	}

	@Override
	public void onGameJoin() {
		reset();
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (mc.player == null) {
			return;
		}
		if (!elytraFly.needNoRotate() && !config.getOnElytra().get() && PlayerUtils.isElytraFlying()) {
			return;
		}

		if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
			PlayerPosition change = packet.change();
			((IPlayerPosition) (Object) change).perfect$setLook(mc.player.getYaw(), mc.player.getPitch());

			if (config.getStrict().get()) {
				strictAngle = new Angle(packet.change().yaw(), packet.change().pitch());
			}
		}
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (mc.player == null) {
			return;
		}

		if (strictAngle != null && event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			double x = packet.getX(mc.player.getX());
			double y = packet.getY(mc.player.getY());
			double z = packet.getZ(mc.player.getZ());

			float yaw = strictAngle.getYaw();
			float pitch = strictAngle.getPitch();

			boolean onGround = packet.isOnGround();
			boolean horizontalCollision = packet.horizontalCollision();

			strictAngle = null;

			event.cancel();

			sendPacket(new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround, horizontalCollision));
		}
	}

	public void reset() {
		strictAngle = null;
	}
}
