package com.perfect.client.functional.function.misc.fakePlayer;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.functional.function.misc.fakePlayer.data.PlayerState;
import com.perfect.client.functional.function.misc.fakePlayer.utils.FakePlayerEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FakePlayer extends Function {

	private final FakePlayerConfig config = new FakePlayerConfig(this);

	private PlayerState spawnState;

	private final List<PlayerState> recordedPositions = new ArrayList<>();

	private FakePlayerEntity fakePlayer;

	private State state = State.NONE;

	public FakePlayer() {
		super("fake-player", "Fake Player", Category.MISC);

		FakePlayerHandler handler = new FakePlayerHandler(this, config);
		handler.enable();
	}

	@Override
	public void onEnable() {
		if (mc.world == null) {
			return;
		}

		spawn();
	}

	@Override
	public void onDisable() {
		remove();
	}

	public void spawnOrRemove() {
		if (fakePlayer == null) {
			spawn();
		}
		else {
			remove();
		}
	}

	public void spawn() {
		if (mc.world == null) {
			return;
		}
		if (!isEnabled()) {
			enable();
			return;
		}

		if (fakePlayer != null) {
			fakePlayer.remove();
		}

		fakePlayer = new FakePlayerEntity(mc.player, config.getNickname().get());
		fakePlayer.spawn();
		spawnState = createState();

		stop();
	}

	public void remove() {
		if (mc.world == null || fakePlayer == null) {
			return;
		}
		if (isEnabled()) {
			disable();
			return;
		}
		fakePlayer.remove();
		fakePlayer = null;
		state = State.NONE;
	}

	public void record() {
		if (state == State.RECORD) {
			stop();
			return;
		}
		state = State.RECORD;
		recordedPositions.clear();
	}

	public void play() {
		if (state == State.PLAY) {
			stop();
			return;
		}
		if (fakePlayer == null) {
			spawn();
		}
		state = State.PLAY;
	}

	public void stop() {
		if (state == State.PLAY && spawnState != null && fakePlayer != null) {
			fakePlayer.setPositionAndRotation(spawnState);
		}

		state = State.NONE;
	}

	public void heal() {
		if (fakePlayer != null) {
			fakePlayer.setHealth(36f);
		}
	}

	public void setPos() {
		if(fakePlayer != null && mc.player != null) {
			fakePlayer.updatePosition(mc.player.getX(), mc.player.getY(), mc.player.getZ());
		}
	}

	public PlayerState createState() {
		if (mc.player == null) {
			return null;
		}

		return new PlayerState(
				mc.player.getPose(),
				mc.player.getPos(),
				mc.player.getYaw(),
				mc.player.getPitch()
		);
	}

	public enum State {
		NONE,
		RECORD,
		PLAY
	}
}
