package com.perfect.client.core.utils.text;

import com.perfect.client.core.utils.math.MathUtils;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringUtils {

	public static final String EMPTY = "";

	private static final int DOUBLE_DECIMAL_PLACES = 5;

	public static String removeLastChar(String str) {
		String output = "";
		if (str != null && !str.isEmpty()) {
			output = str.substring(0, str.length() - 1);
		}
		return output;
	}

	public static String openQuotes(String string) {
		if (string.startsWith("\"")) {
			string = string.substring(1);
		}

		if (string.endsWith("\"")) {
			string = removeLastChar(string);
		}

		return string;
	}

	public static Optional<Float> parseFloat(String string) {
		try {
			return Optional.of(Float.parseFloat(string));
		}
		catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public static Optional<Double> parseDoubleLimited(String string) {
		Optional<Double> val = parseDouble(string);
		return val.map(v -> MathUtils.roundTo(v, DOUBLE_DECIMAL_PLACES));
	}

	public static Optional<Double> parseDouble(String string) {
		try {
			return Optional.of(Double.parseDouble(string));
		}
		catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public static Optional<Integer> parseInt(String string) {
		try {
			return Optional.of(Integer.parseInt(string));
		}
		catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public static int getMatchCount(String string, char target) {
		return getMatchCount(string, Character.toString(target));
	}

	public static int getMatchCount(String string, String target) {
		return string.length() - string.replace(target, "").length();
	}

	public static String[] split(String string) {
		return string.split("[,\\s]+");
	}

	public static String textToString(Text text) {
		if (text == null) {
			return StringUtils.EMPTY;
		}

		String name = text.getString();

		if (name == null) {
			return StringUtils.EMPTY;
		}

		return name;
	}

	public static String formatDoubles(double value, int numbersAfterPoint) {
		String format = "%." + numbersAfterPoint + "f";

		return String.format(format, value).replace(",", ".");
	}

	public static String separateNumber(String str, String separator) {
		char[] chars = str.toCharArray();

		StringBuilder builder = new StringBuilder();

		int lastSeparator = 0;

		for (int i = chars.length - 1; i >= 0; i--) {
			if (lastSeparator == 3) {
				builder.append(separator);
				lastSeparator = 0;
			}

			builder.append(chars[i]);
			lastSeparator++;
		}

		return builder.reverse().toString();
	}

	public static String enumToFormattedString(Enum<?> enumValue) {
		return Arrays.stream(enumValue.name().split("_"))
		             .map(word -> word.isEmpty() ? "" :
		                          Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
		             .filter(word -> !word.isEmpty())
		             .collect(Collectors.joining(" "));
	}
}
