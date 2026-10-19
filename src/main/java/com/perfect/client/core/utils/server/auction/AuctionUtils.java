package com.perfect.client.core.utils.server.auction;

import com.perfect.client.core.utils.text.StringUtils;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.perfect.client.PerfectClient.mc;

public class AuctionUtils {

	public static boolean isAuctionScreen() {
		return checkScreen("аукцион", "поиск");
	}

	public static boolean isPayScreen() {
		return checkScreen("подтверждение покупки", "подозрительная цена");
	}

	private static boolean checkScreen(String... names) {
		if (!(mc.currentScreen instanceof GenericContainerScreen)) {
			return false;
		}
		String str = mc.currentScreen.getTitle().getString().toLowerCase();
		return Arrays.stream(names).anyMatch(str::contains);
	}

	public static Optional<Integer> getPrice(ItemStack stack) {
		LoreComponent lore = stack.get(DataComponentTypes.LORE);

		if (lore == null || lore.lines().isEmpty()) {
			return Optional.empty();
		}

		for (Text line : lore.lines()) {
			String string = StringUtils.textToString(line);

			if (Stream.of("$", "₽", "Цена:", "Стоимость").anyMatch(string::contains)) {
				List<Character> digits = new ArrayList<>();

				for (char character : string.toCharArray()) {
					if (character == '.') {
						break;
					}

					if (Character.isDigit(character)) {
						digits.add(character);
					}
				}

				if (!digits.isEmpty()) {
					StringBuilder stringBuilder = new StringBuilder();

					for (Character character : digits) {
						stringBuilder.append(character);
					}

					return StringUtils.parseInt(stringBuilder.toString());
				}
			}
		}

		return Optional.empty();
	}
}
