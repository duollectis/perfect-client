package com.perfect.client.core.utils.text.filter;

public class FilterUtils {

	public static boolean isNumber(boolean fractional, TextFieldState state, char c) {
		return isNumber(fractional, state.cursorStart(), state.cursorEnd(), state.string(), c);
	}

	private static boolean isNumber(boolean fractional, int cursorStart, int cursorEnd, String string, char c) {
		if (c == '-' && cursorStart == 0 && !contains(cursorStart, cursorEnd, string, '-')) {
			return true;
		}

		if (fractional && c == '.' && !contains(cursorStart, cursorEnd, string, '.')) {
			return true;
		}

		if (isRepeatingZero(cursorStart, string, c)) {
			return false;
		}

		return Character.isDigit(c);
	}

	private static boolean isRepeatingZero(int cursorStart, String string, char c) {
		if (c != '0') {
			return false;
		}

		if (cursorStart == 1 && string.startsWith("0")) {
			return true;
		}
		return cursorStart == 2 && string.startsWith("-0");
	}

	private static boolean contains(int cursorStart, int cursorEnd, String string, char c) {
		int index = string.indexOf(c);
		if (index == -1) {
			return false;
		}
		return !willBeReplaced(cursorStart, cursorEnd, index);
	}

	private static boolean willBeReplaced(int cursorStart, int cursorEnd, int index) {
		return cursorStart <= index && cursorEnd > index;
	}
}
