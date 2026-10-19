package com.perfect.client.core.utils.game;

import static com.perfect.client.PerfectClient.mc;

public class CommandUtils {

	public static void sendCommand(String command) {
		if (mc.player != null) {
			mc.player.networkHandler.sendChatCommand(command);
		}
	}

	public static String trimCommand(String command) {
		return command.replace("/", "");
	}
}
