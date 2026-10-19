package com.perfect.client.functional.function.misc.antiBot;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ToggleContainer;
import com.perfect.client.functional.function.misc.antiBot.handlers.*;
import com.perfect.client.functional.function.misc.antiBot.utils.AntiBotHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import static com.perfect.client.manager.system.FunctionManager.fakePlayer;

public class AntiBot extends Function {

	private final AntiBotConfig config = new AntiBotConfig(this);

	private final ToggleContainer<AntiBotHandler> toggleContainer = createToggleHandler();

	public AntiBot() {
		super("anti-bot", "Anti Bot", Category.MISC);

		toggleContainer.add(config.getAge(), new AgeHandler(this, config));
		toggleContainer.add(config.getMovement(), new MovementHandler(this, config));
		toggleContainer.add(config.getHealth(), new HealthHandler(this, config));
		toggleContainer.add(config.getPing(), new PingHandler(this, config));
		toggleContainer.add(config.getUuid(), new UuidHandler(this, config));
		toggleContainer.add(config.getNickname(), new NicknameHandler(this, config));
		toggleContainer.add(config.getSpeed(), new SpeedHandler(this, config));
	}

	@Override
	public void onGameLeft() {
		clear();
	}

	public void clear() {
		toggleContainer.getActiveHandlers().forEach(AntiBotHandler::clear);
	}

	public boolean isBot(Entity entity) {
		if (entity instanceof PlayerEntity player) {
			return isBot(player);
		}
		return false;
	}

	public boolean isBot(PlayerEntity player) {
		if (!isEnabled()) {
			return false;
		}

		if (player.equals(fakePlayer.getFakePlayer()) || player.equals(mc.player)) {
			return false;
		}

		for (AntiBotHandler handler : toggleContainer.getActiveHandlers()) {
			if (handler.isBot(player)) {
				return true;
			}
		}

		return false;
	}

	public void onToggled() {
		toggleContainer.onToggled();
	}
}
