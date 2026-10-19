package com.perfect.client.functional.function.misc.betterChat;

import com.mojang.authlib.GameProfile;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.functional.function.misc.betterChat.handlers.ChatMessageHandler;
import com.perfect.client.inject.mixinInterface.IChatHudLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BetterChat extends Function {

	private static final Pattern USERNAME_REGEX = Pattern.compile("^(?:<[0-9]{2}:[0-9]{2}>\\s)?<(.*?)>.*");
	private static final Pattern TIMESTAMP_REGEX = Pattern.compile("^<\\d{1,2}:\\d{1,2}>");
	private static final Pattern GROUP_REGEX = Pattern.compile("^\\[.*\\]$");

	private static final List<CustomHead> HEAD_LIST = new ArrayList<>();
	private final BetterChatConfig config = new BetterChatConfig(this);

	public BetterChat() {
		super("better-chat", "Better Chat", Category.MISC);

		registerCustomHead("[Perfect]", PerfectClient.getIdentifier("icon.png"));
		addHandler(new ChatMessageHandler(this, config));
	}

	public static void registerCustomHead(String prefix, Identifier texture) {
		HEAD_LIST.add(new CustomHead(prefix, texture));
	}

	public int modifyChatWidth(int width) {
		if (isEnabled() && config.getChatWidth().isEnabled()) {
			width = (int) (width * config.getChatWidth().fromPercent());
		}

		if (isEnabled() && config.getPlayerHeads().get()) {
			return width + 12;
		}

		return width;
	}

	@SuppressWarnings("DataFlowIssue")
	public void beforeDrawMessage(DrawContext context, ChatHudLine.Visible line, int y, int color) {
		if (!isEnabled() || !config.getPlayerHeads().get()) {
			return;
		}

		boolean drawed = false;

		if (((IChatHudLine) (Object) line).perfect$isStartOfEntry())  {
			drawed = drawTexture(context, (IChatHudLine) (Object) line, y, color);
		}

		context.getMatrices().pushMatrix();
		if (drawed) {
			context.getMatrices().translate(12, 0);
		}
	}

	public void afterDrawMessage(DrawContext context) {
		if (!isEnabled() || !config.getPlayerHeads().get()) {
			return;
		}

		context.getMatrices().popMatrix();
	}

	private GameProfile getSender(IChatHudLine line, String text) {
		assert mc.getNetworkHandler() != null;

		GameProfile sender = line.perfect$getSender();

		if (sender == null) {
			Matcher usernameMatcher = USERNAME_REGEX.matcher(text);

			if (usernameMatcher.matches()) {
				String username = usernameMatcher.group(1);

				PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(username);
				if (entry != null) sender = entry.getProfile();
			}
		}

		return sender;
	}

	private boolean drawTexture(DrawContext context, IChatHudLine line, int y, int color) {
		assert mc.getNetworkHandler() != null;

		String text = line.perfect$getText().trim();

		int startOffset = 0;
		float xOffset = 0;

		if (config.getAnimation().get()) {
			xOffset = line.perfect$getX();
		}

		try {
			Matcher timestamp = TIMESTAMP_REGEX.matcher(text);
			if (timestamp.find()) {
				startOffset = timestamp.end() + 1;
			}

			Matcher group = GROUP_REGEX.matcher(text);
			if (group.find()) {
				startOffset = group.end() + 1;
			}
		}
		catch (IllegalStateException ignored) {}

		for (CustomHead entry : HEAD_LIST) {
			if (text.startsWith(entry.getPrefix(), startOffset)) {
				context.drawTexture(RenderPipelines.GUI_TEXTURED, entry.getTexture(),
						(int) xOffset, y, 0, 0, 8, 8, 64, 64, 64, 64, color);

				return true;
			}
		}

		GameProfile sender = getSender(line, text);
		if (sender == null) {
			return false;
		}

		PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(sender.getId());
		if (entry == null) {
			return false;
		}

		PlayerSkinDrawer.draw(context, entry.getSkinTextures(), (int) xOffset, y, 8, color);

		return true;
	}

	public boolean isAnimation() {
		return isEnabled() && config.getAnimation().get();
	}

	public boolean getExtraMessageLength() {
		return isEnabled() && config.getExtraMessageLength().get();
	}

	public boolean getExtraChatLength() {
		return isEnabled() && config.getExtraChatLength().get();
	}

	public boolean getKeepHistory() { return isEnabled() && config.getKeepHistory().get(); }

	public int getExtraChatLines() {
		return 1000;
	}

	public void updateChatHud() {
		if (mc.inGameHud != null && mc.inGameHud.getChatHud() != null) {
			mc.inGameHud.getChatHud().reset();
		}
	}

	@Getter
	@AllArgsConstructor
	public static class CustomHead {
		private String prefix;
		private Identifier texture;
	}
}
