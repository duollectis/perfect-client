package com.perfect.client.gui.utils;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;


public class UiUtils {

	@Setter
	@Getter
	private static Widget draggingWidget;

	@Setter
	private static TextFieldWidget listeningWidget;

	public static String getId(Setting<?> setting) {
		String function = setting.getFunction() == null ? "" : (setting.getFunction().getId() + ".");
		return function + setting.getId();
	}

	public static boolean isListening() {
		return listeningWidget != null && listeningWidget.isListening();
	}

	public static void stopListening() {
		if (listeningWidget != null) {
			listeningWidget.stopListening();
			listeningWidget = null;
		}
	}

	public static void startListening(TextFieldWidget widget) {
		stopListening();
		listeningWidget = widget;
	}

	public static void releaseWidget(Widget widget) {
		if (draggingWidget == widget) {
			draggingWidget = null;
		}
	}

	public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
		return draggingWidget == null && isMouseHovered(mouseX, mouseY, x, y, width, height);
	}

	private static boolean isMouseHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
		return BoxUtils.isPointInsideBox(x, y, width, height, mouseX, mouseY);
	}
}
