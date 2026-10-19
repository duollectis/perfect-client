package com.perfect.client.core.utils.text;

import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.StringUtils;

import static org.lwjgl.glfw.GLFW.*;

public class NameUtils {

	public static boolean isValidIdChar(char c) {
		if (c >= 'a' && c <= 'z') {
			return true;
		}
		return c == '-';
	}

	
	public static String getKeyName(int key) {
		return switch (key) {
			case GLFW_KEY_UNKNOWN -> "Unknown";
			case GLFW_KEY_ESCAPE -> "Esc";
			case GLFW_KEY_GRAVE_ACCENT -> "~";
			case GLFW_KEY_WORLD_1 -> "World 1";
			case GLFW_KEY_WORLD_2 -> "World 2";
			case GLFW_KEY_PRINT_SCREEN -> "Print";
			case GLFW_KEY_PAUSE -> "Pause";
			case GLFW_KEY_INSERT -> "Insert";
			case GLFW_KEY_DELETE -> "Del";
			case GLFW_KEY_HOME -> "Home";
			case GLFW_KEY_PAGE_UP -> "Pg Up";
			case GLFW_KEY_PAGE_DOWN -> "Pg Dn";
			case GLFW_KEY_END -> "End";
			case GLFW_KEY_TAB -> "Tab";
			case GLFW_KEY_LEFT_CONTROL -> "LCtrl";
			case GLFW_KEY_RIGHT_CONTROL -> "RCtrl";
			case GLFW_KEY_LEFT_ALT -> "LAlt";
			case GLFW_KEY_RIGHT_ALT -> "RAlt";
			case GLFW_KEY_LEFT_SHIFT -> "LShift";
			case GLFW_KEY_RIGHT_SHIFT -> "RShift";
			case GLFW_KEY_UP -> "Up";
			case GLFW_KEY_DOWN -> "Down";
			case GLFW_KEY_LEFT -> "Left";
			case GLFW_KEY_RIGHT -> "Right";
			case GLFW_KEY_APOSTROPHE -> "`";
			case GLFW_KEY_BACKSPACE -> "Back";
			case GLFW_KEY_CAPS_LOCK -> "Caps";
			case GLFW_KEY_MENU -> "Menu";
			case GLFW_KEY_LEFT_SUPER -> "LWin";
			case GLFW_KEY_RIGHT_SUPER -> "RWin";
			case GLFW_KEY_ENTER -> "Enter";
			case GLFW_KEY_KP_ENTER -> "Enter";
			case GLFW_KEY_NUM_LOCK -> "Num Lk";
			case GLFW_KEY_SPACE -> "Space";
			case GLFW_KEY_F1 -> "F1";
			case GLFW_KEY_F2 -> "F2";
			case GLFW_KEY_F3 -> "F3";
			case GLFW_KEY_F4 -> "F4";
			case GLFW_KEY_F5 -> "F5";
			case GLFW_KEY_F6 -> "F6";
			case GLFW_KEY_F7 -> "F7";
			case GLFW_KEY_F8 -> "F8";
			case GLFW_KEY_F9 -> "F9";
			case GLFW_KEY_F10 -> "F10";
			case GLFW_KEY_F11 -> "F11";
			case GLFW_KEY_F12 -> "F12";
			case GLFW_KEY_F13 -> "F13";
			case GLFW_KEY_F14 -> "F14";
			case GLFW_KEY_F15 -> "F15";
			case GLFW_KEY_F16 -> "F16";
			case GLFW_KEY_F17 -> "F17";
			case GLFW_KEY_F18 -> "F18";
			case GLFW_KEY_F19 -> "F19";
			case GLFW_KEY_F20 -> "F20";
			case GLFW_KEY_F21 -> "F21";
			case GLFW_KEY_F22 -> "F22";
			case GLFW_KEY_F23 -> "F23";
			case GLFW_KEY_F24 -> "F24";
			case GLFW_KEY_F25 -> "F25";
			case GLFW_KEY_EQUAL -> "=";
			case GLFW_KEY_MINUS -> "-";
			case GLFW_KEY_SLASH -> "/";
			case GLFW_KEY_BACKSLASH -> "\\";
			case GLFW_KEY_COMMA -> ",";
			case GLFW_KEY_PERIOD -> ".";
			case GLFW_KEY_SEMICOLON -> ";";
			case GLFW_KEY_LEFT_BRACKET -> "{";
			case GLFW_KEY_RIGHT_BRACKET -> "}";
			case GLFW_KEY_KP_ADD -> "+";
			case GLFW_KEY_KP_SUBTRACT -> "-";
			case GLFW_KEY_KP_EQUAL -> "=";
			case GLFW_KEY_KP_DIVIDE -> "/";
			case GLFW_KEY_KP_MULTIPLY -> "*";
			case GLFW_KEY_KP_DECIMAL -> "Del";
			default -> getKeyEnglish(key);
		};
	}

	
	public static String getKeyEnglish(int key) {
		return StringUtils
				.capitalize(InputUtil.fromKeyCode(key, 0).getTranslationKey())
				.toUpperCase()
				.replace("KEY.KEYBOARD", "")
				.replace(".", "")
				.replace("KEYPAD", "");
	}

	
	public static String getButtonName(int button) {
		return switch (button) {
			case -1 -> "Unknown";
			case 0 -> "LMB";
			case 1 -> "RMB";
			case 2 -> "MMB";
			default -> "Mouse " + button;
		};
	}
}
