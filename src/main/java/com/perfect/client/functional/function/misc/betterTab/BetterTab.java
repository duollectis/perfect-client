package com.perfect.client.functional.function.misc.betterTab;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;

import static com.perfect.client.PerfectClient.clientConfig;

public class BetterTab extends Function {

	private final BetterTabConfig config = new  BetterTabConfig(this);

	public BetterTab() {
		super("better-tab", "Better Tab", Category.MISC);
	}

	public boolean getPlayerHeads() {
		return isEnabled() && config.getPlayerHeads().get();
	}

	public long getTabSize(long oldValue) {
		if (isEnabled() && config.getTabSize().isEnabled()) {
			return config.getTabSize().get();
		}

		return oldValue;
	}

	public Text formatName(PlayerListEntry playerListEntry) {
		assert mc.player != null;

		Text name;
		Color color = null;

		name = playerListEntry.getDisplayName();
		if (name == null) {
			name = Text.literal(playerListEntry.getProfile().getName());
		}

		if (playerListEntry.getProfile().getId().toString().equals(mc.player.getGameProfile().getId().toString()) && config.getSelfHighlight().isEnabled()) {
			color = config.getSelfHighlight().get();
		}
		else if (config.getFriendsHighlight().get() && Managers.FRIEND.isFriend(playerListEntry.getProfile().getName())) {
			color = clientConfig.getFriendColor().getColor();
		}

		if (color != null) {
			String nameString = name.getString();

			for (Formatting format : Formatting.values()) {
				if (format.isColor()) nameString = nameString.replace(format.toString(), "");
			}

			name = Text.literal(nameString).setStyle(name.getStyle().withColor(TextColor.fromRgb(color.getRGB())));
		}

		return name;
	}
}
