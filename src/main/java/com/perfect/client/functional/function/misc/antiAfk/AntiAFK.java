package com.perfect.client.functional.function.misc.antiAfk;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.functions.delay.SecondDelayUtils;
import lombok.Getter;

public class AntiAFK extends Function {

	private final AntiAfkConfig config = new AntiAfkConfig(this);

	@Getter
	private final SecondDelayUtils jumpDelay = new SecondDelayUtils(() -> config.getJumpDelay().get());

	public AntiAFK() {
		super("anti-afk", "Anti AFK", Category.MISC);
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (mc.player != null && mc.player.isOnGround() && jumpDelay.isPassedSeconds()) {
			event.jumping.set(true);
			jumpDelay.reset();
		}
	}

	public enum Actions {
		JUMP
	}
}
