package com.perfect.client.functional.function.misc.autoEz;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.manager.core.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class AutoEz extends Function {

	private final AutoEzConfig config = new AutoEzConfig(this);

	private final TimerUtils timer = new TimerUtils();
	private final String[] builtInKills = new String[] {
			"<player> died after popping <pops> tots!",
			"<player> take the L, kid",
			"<player> stop crying",
			"<player> got memed",
			"<player> got killed after <pops> tots",
			"thanks for free kill <player>!",
			"that was a pretty bad move <player>",
			"my grandma plays minecraft better than you <player>",
			"<player> does not have a good client",
			"<player> gg e z kid",
			"<player> u r so ez",
			"eeezzzzzz <player>",
			"<player> lol GG!!!",
			"<player> lol bad client what is it exhibition?",
			"<player> imagine having <pops> tots",
			"<player> L",
			"<player> lololololol mad? lololololol",
			"<player> are you even trying?",
			"<player> You. Are. Terrible.",
			"<player>'s dad is bald",
			"<player> нou died in a fucking block game",
			"<player> chilled out with <pops> tots!",
			"<player> buy normal client, nigga",
			"<player> is nigger",
			"<player> is black",
			"<player> your dick is so small, that you bang cheerios"
	};

	public AutoEz() {
		super("auto-ez", "Auto Ez", Category.MISC);
	}

	@Override
	public void onEnable() {
		checkFile();
	}

	@Override
	public void onPlayerDeath(PlayerEvent event) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (!timer.passedSeconds(config.getSendDelay().get())) {
			return;
		}

		PlayerEntity player = event.getPlayer();
		if (player == mc.player || Managers.FRIEND.isFriend(player)) {
			return;
		}

		if (mc.player.distanceTo(player) > config.getTargetRange().get()) {
			return;
		}

		sendMessage(player, event.getPoppedTotemsCount(), config.getKills().get());

		timer.reset();
	}

	private void sendMessage(PlayerEntity player, int totemsCount, MessageMode messageMode) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		String message = "";
		switch (messageMode) {
			case Own -> {
				message = config.getKillsMessage().get();
			}
			case Built_In -> {
				int messageId = MathUtils.random(0, builtInKills.length - 1);
				message = builtInKills[messageId];
			}
			case File -> {
				File messagesFile = ConfigManager.AUTO_EZ_MESSAGES;

				if (messagesFile.exists()) {
					try {
						List<String> lines = Files.readAllLines(messagesFile.toPath());

						lines.removeIf(String::isBlank);

						if (!lines.isEmpty()) {
							int randomIdx = MathUtils.random(0, lines.size() - 1);
							message = lines.get(randomIdx);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (message.isBlank()) {
			return;
		}

		mc.player.networkHandler.sendChatMessage(formatMessage(message, player, totemsCount));
	}

	private String formatMessage(String message, PlayerEntity player, int totemsCount) {
		return message.replace("<player>", player.getGameProfile().getName())
		              .replace("<pops>", String.valueOf(totemsCount));
	}

	public void checkFile() {
		if (config.getKills().get() != MessageMode.File) {
			return;
		}

		File messagesFile = ConfigManager.AUTO_EZ_MESSAGES;

		if (messagesFile.exists()) {
			try {
				List<String> lines = Files.readAllLines(messagesFile.toPath());

				lines.removeIf(String::isBlank);

				if (lines.isEmpty()) {
					error("AutoEz file is empty!");
					disable();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			error("AutoEz file does not exist!");
			disable();
		}
	}

	public enum MessageMode {
		Built_In,
		Own,
		File
	}
}
