package com.perfect.client.core.utils.text.filter;

public interface ITextFilter {

	default boolean test(String string) {
		StringBuilder builder = new StringBuilder();
		char[] chars = string.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			TextFieldState state = new TextFieldState(i, i, builder.toString());

			if (!test(state, c)) {
				return false;
			}

			builder.append(c);
		}

		return true;
	}

	boolean test(TextFieldState state, char c);
}