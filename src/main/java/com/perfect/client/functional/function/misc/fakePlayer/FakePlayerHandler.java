package com.perfect.client.functional.function.misc.fakePlayer;

import com.perfect.client.core.event.events.entity.AttackEntityEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import com.perfect.client.functional.function.misc.fakePlayer.data.PlayerState;
import com.perfect.client.functional.function.misc.fakePlayer.utils.FakePlayerEntity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class FakePlayerHandler extends FunctionHandler<FakePlayer, FakePlayerConfig> {

	private int movementTick;

	public FakePlayerHandler(FakePlayer function, FakePlayerConfig config) {
		super(function, config);
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		Utils.safeRun(() -> {
			if (mc.player == null || mc.world == null || isNull()) {
				return;
			}
			if ((!(event.getPacket() instanceof ExplosionS2CPacket packet))) {
				return;
			}

			FakePlayerEntity fakePlayer = function.getFakePlayer();

			if (fakePlayer.canBeAttacked() && isInExplosionRadius(packet)) {
				fakePlayer.damage(getExplosionDamage(packet));
			}
		});
	}

	@Override
	public void onAttackEntity(AttackEntityEvent event) {
		if (mc.player == null || mc.world == null || isNull()) {
			return;
		}

		FakePlayerEntity fakePlayer = function.getFakePlayer();

		if (fakePlayer.equals(event.getEntity()) && fakePlayer.canBeAttacked()) {
			fakePlayer.damage(1.5f);
			playCritSound();
		}
	}

	@Override
	public void onTickEntities() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		switch (function.getState()) {
			case RECORD -> recordTick();
			case PLAY -> playTick();
			default -> movementTick = 0;
		}
	}

	private void playCritSound() {
		if (mc.world == null) {
			return;
		}
		FakePlayerEntity fakePlayer = function.getFakePlayer();

		mc.world.playSound(mc.player,
				fakePlayer.getX(), fakePlayer.getY(), fakePlayer.getZ(),
				SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS,
				1f, 1f
		);
	}

	private boolean isInExplosionRadius(ExplosionS2CPacket packet) {
		FakePlayerEntity fakePlayer = function.getFakePlayer();
		Vec3d source = packet.center();

		double distance = PositionMath.squareDistance(fakePlayer.getPos(), source);
		return distance <= 15;
	}

	private float getExplosionDamage(ExplosionS2CPacket packet) {
		FakePlayerEntity fakePlayer = function.getFakePlayer();

		return DamageCalculator.getExplosionDamage(
				fakePlayer,
				fakePlayer.getBoundingBox(),
				packet.center(),
				false,
				false,
				true,
				6,
				null
		);
	}

	private void recordTick() {
		function.getRecordedPositions().add(function.createState());
		movementTick = 0;
	}

	private void playTick() {
		if (function.getRecordedPositions().isEmpty() || isNull()) {
			resetMovement();
			return;
		}

		movementTick++;

		if (movementTick >= function.getRecordedPositions().size()) {
			resetMovement();
			return;
		}

		FakePlayerEntity fakePlayer = function.getFakePlayer();
		PlayerState state = function.getRecordedPositions().get(movementTick);
		fakePlayer.setPositionAndRotation(state);
	}

	private void resetMovement() {
		movementTick = 0;
		function.heal();
	}

	private boolean isNull() {
		return function.getFakePlayer() == null;
	}
}
