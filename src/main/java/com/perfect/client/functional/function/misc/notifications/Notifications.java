package com.perfect.client.functional.function.misc.notifications;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.functions.ChatUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class Notifications extends Function {

	private final NotificationsConfig config = new NotificationsConfig(this);

	public Notifications() {
		super("notifications", "Notifications", Category.MISC);
	}

	@Override
	public void onPlayerTotemPop(PlayerEvent event) {
		sendTotemNotify(event.getPlayerChatId(), event.getPlayer(), event.getPoppedTotemsCount(), false);
	}

	@Override
	public void onPlayerDeath(PlayerEvent event) {
		sendTotemNotify(event.getPlayerChatId(), event.getPlayer(), event.getPoppedTotemsCount(), true);
	}

	private void sendTotemNotify(int id, PlayerEntity player, int pops, boolean dead) {
		if (!config.getTotems().get() || !isEnabled()) {
			return;
		}

		if (config.getIgnoreSelf().get() && player.equals(mc.player)) {
			return;
		}

		String text = dead ? "has died after popping" : "has popped";

		ChatUtils.sendMsg(id, getInfoText(player.getName().getString(), pops, text));
	}

	private MutableText getInfoText(String name, int pops, String info) {
		MutableText text = Text.empty();
		text.append(name + " ");

		String totemsInfo = getCase(pops) + ".";

		String[] array = info.split("");

		List<Color> colors = ColorUtils.createGradient(
				array.length + totemsInfo.length(),
				Collections.singletonList(Managers.COLOR.get(Managers.COLOR.MAIN))
		);

		int i = 0;
		for (String s : array) {
			text.append(Text.literal(s).setStyle(text.getStyle().withColor(colors.get(i).getRGB())));
			i++;
		}

		text.append(" " + pops + " ");

		for (String s : totemsInfo.split("")) {
			text.append(Text.literal(s).setStyle(text.getStyle().withColor(colors.get(i).getRGB())));
			i++;
		}

		return text;
	}

	private static String getCase(int value) {
		if (value <= 1) {
			return "totem";
		}
		else {
			return "totems";
		}
	}
}
