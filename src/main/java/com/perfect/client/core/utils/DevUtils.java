package com.perfect.client.core.utils;

import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.system.EventListener;
import com.perfect.client.core.utils.functions.ChatUtils;
import net.minecraft.client.util.InputUtil;

import static com.perfect.client.manager.system.FunctionManager.testFunction;

public class DevUtils {

	public static double i = 1.0, k = 1.0;

	public static void init() {
		new DevUtilsEventListener();
	}

	private static void out(String s, double d) {
		ChatUtils.sendMsg(s.hashCode(), s + d);
	}

	public static class DevUtilsEventListener extends EventListener {

		@Override
		public void onKey(KeyEvent event) {
			if (!testFunction.devUtil.get()) {
				return;
			}

			if (event.isPress()) {
				if (event.getValue() == InputUtil.GLFW_KEY_L) {
					i += 0.1;
					out("i ", i);
				}
				else if (event.getValue() == InputUtil.GLFW_KEY_K) {
					i -= 0.1;
					out("i ", i);
				}
				else if (event.getValue() == InputUtil.GLFW_KEY_N) {
					k -= 0.1;
					ChatUtils.sendMsg(52525252, "k " + k);
				}
				else if (event.getValue() == InputUtil.GLFW_KEY_M) {
					k += 0.1;
					ChatUtils.sendMsg(52525252, "k " + k);
				}
			}
		}
	}
}
