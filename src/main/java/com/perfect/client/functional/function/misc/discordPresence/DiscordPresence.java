package com.perfect.client.functional.function.misc.discordPresence;

import com.perfect.client.Entry;
import com.perfect.client.core.discord.system.DiscordIPC;
import com.perfect.client.core.discord.system.RichPresence;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import net.minecraft.client.network.ServerInfo;


public class DiscordPresence extends Function {

	public static final long APP_ID = 1491068760734568649L;

	public static final String ICON_URL = "https://github.com/TheFirstPlaceCoder/installer-resource/raw/refs/heads/main/images/icon.gif";
	public static final String SERVER_AVATAR = "https://api.mcsrvstat.us/icon/";
	public static final String PLAYER_AVATAR = "https://mc-heads.net/avatar/";
	public static final String WEBSITE_URL = "https://perfect-client.fun/";
	public static final String DISCORD_URL = "https://discord.com/invite/A6cBWazp8V";

	private final DiscordPresenceConfig config = new DiscordPresenceConfig(this);

	public final RichPresence rpc = new RichPresence();
	public boolean init;

	public DiscordPresence() {
		super("discord-presence", "Discord Presence", Category.MISC, "rpc");
	}

	@Override
	public void onEnable() {
		initPresence();
	}

	@Override
	public void onDisable() {
		DiscordIPC.stop();
	}

	@Override
	public void onPreTick() {
		if (config.getSmallImageMode().get(Type.SERVER_AVATAR)) {
			updateServer();
		}
		else if (config.getSmallImageMode().get(Type.PLAYER_AVATAR)) {
			updatePlayer();
		}
		else {
			clearIcon();
		}

		DiscordIPC.setActivity(rpc);
	}

	private void initPresence() {
		DiscordIPC.start(APP_ID, null);

		rpc.setStart(System.currentTimeMillis() / 1000L);
		rpc.setLargeImage(ICON_URL, "v" + Entry.getCLIENT_VERSION());

		rpc.setDetails("UID >> " + 0);
		rpc.setState("Version >> " + Entry.DISPLAY_VERSION);

		rpc.addButton("Buy", WEBSITE_URL);
		rpc.addButton("Discord", DISCORD_URL);

		DiscordIPC.setActivity(rpc);
	}

	private void updateServer() {
		if (mc != null && mc.isInSingleplayer()) {
			clearIcon();
			return;
		}

		ServerInfo info = null;

		if (mc != null && mc.player != null && mc.player.networkHandler != null) {
			info = mc.player.networkHandler.getServerInfo();
		}

		if (info == null) {
			clearIcon();
			return;
		}

		rpc.setSmallImage(SERVER_AVATAR + info.address, info.address);

		if (info.players != null) {
			int size;

			if (mc.player != null && mc.player.networkHandler != null) {
				size = mc.player.networkHandler.getPlayerList().size();
			}
			else {
				size = info.players.online();
			}

			String text = info.address + " (" + size + "/" + info.players.max() + ")";

			rpc.setSmallImage(SERVER_AVATAR + info.address, text);
		}
	}

	private void updatePlayer() {
		if (mc != null && mc.getGameProfile() != null) {
			String nickname = mc.getGameProfile().getName();

			rpc.setSmallImage(PLAYER_AVATAR + nickname, nickname);
		}
		else {
			clearIcon();
		}
	}

	private void clearIcon() {
		rpc.setSmallImage(null, null);
	}

	public enum Type {
		NONE,
		PLAYER_AVATAR,
		SERVER_AVATAR
	}
}
