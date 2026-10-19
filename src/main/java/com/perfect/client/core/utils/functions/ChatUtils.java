package com.perfect.client.core.utils.functions;

import com.perfect.client.core.event.system.EventListener;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.inject.mixinInterface.IChatHud;
import com.perfect.client.inject.mixinInterface.IChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class ChatUtils {

	private static final String PREFIX = "[Perfect]";

	public static void init() {
		new ChatUtilsEventListener();
	}

	public static void error(int id, String msg) {
		sendMsg(id, Formatting.DARK_RED + msg);
	}

	public static void sendMsg(String... args) {
		sendMsg(0, args);
	}

	public static void sendMsg(int id, String... args) {
		sendMsg(id, null, args);
	}

	public static void sendMsg(int id, Text customPrefix, String... args) {
		sendMsg(id, customPrefix, null, args);
	}

	public static void sendMsg(int id, Text customPrefix, Formatting textColor, String... args) {
		MutableText msg = Text.empty();
		for (String str : args) {
			if (textColor != null)
				msg.append(str + textColor);
			else
				msg.append(str);
		}
		sendMsg(id, customPrefix, msg);
	}

	public static void sendMsg(int id, Text customPrefix, Text msg) {
		if (mc.world == null || msg == null)
			return;

		MutableText message = Text.empty();
		message.append(getPrefix());
		if (customPrefix != null)
			message.append(customPrefix);
		message.append(msg);

		Utils.safeRun(() -> ((IChatHud) mc.inGameHud.getChatHud()).perfect$add(message, id));
	}

	private static float gradientOffset;

	private static void onRender2D() {
		try {
			if (gradientOffset < 8) {
				gradientOffset++;
			}
			else {
				gradientOffset = 0;
			}

			List<ChatHudLine.Visible> visibles = mc.inGameHud.getChatHud().visibleMessages;

			for (int index = visibles.size() - 1; index > -1; --index) {
				ChatHudLine.Visible line = visibles.get(index);
				if (line == null)
					continue;

				OrderedText content = line.content();
				if (content == null)
					continue;

				MutableText parsed = Text.literal("");
				content.accept((i, style, codePoint) -> {
					parsed.append(Text.literal(new String(Character.toChars(codePoint))).setStyle(style));
					return true;
				});

				String prefix = PREFIX + " ";
				int
						perfectIndex =
						parsed.getString().indexOf(prefix) - (parsed.getString().length() - parsed
								.getSiblings()
								.size()
						);
				if (perfectIndex > -1) {
					parsed
							.getSiblings()
							.subList(
									perfectIndex,
									Math.min(perfectIndex + prefix.length(), parsed.getString().length() - 1)
							)
							.clear();
					parsed.getSiblings().add(perfectIndex, getPrefix());
				}
				else
					continue;

				IChatHudLine hudLine = (IChatHudLine) (Object) visibles.get(index);
				if (hudLine == null)
					continue;

				int id = hudLine.perfect$getId();
				visibles.set(
						index,
						new ChatHudLine.Visible(
								line.addedTime(),
								parsed.asOrderedText(),
								line.indicator(),
								line.endOfEntry()
						)
				);
				((IChatHudLine) (Object) visibles.get(index)).perfect$setId(id);
			}
		}
		catch (Exception ignored) {
		}
	}

	public static MutableText getPrefix() {
		List<Color> colors = ColorUtils.createGradient(
				PREFIX.length(),
				Collections.singletonList(Managers.COLOR.get(Managers.COLOR.MAIN))
		);

		String[] prefixSymbols = PREFIX.split("");

		MutableText prefix = Text.empty();

		int i = 0;
		for (String s : prefixSymbols) {
			Text stext = Text.literal(s).setStyle(prefix.getStyle().withColor(colors.get(i).getRGB()));
			prefix.append(stext);

			i++;
		}

		return prefix.append(" ");
	}

	public static class ChatUtilsEventListener extends EventListener {

//		@Override
//		public void onRender2D(Render2DEvent event) {
//			ChatUtils.onRender2D();
//		}
	}
}
