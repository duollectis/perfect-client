package com.perfect.client.gui.widgets.overlay;

import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.widgets.common.Widget;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.mc;

public class OverlayWidget extends Widget {

	private final static float INDENT = 3.0f;

	private final OverlayListWidget upperList = addChild(new OverlayListWidget());

	public OverlayWidget() {
		setInteractional(false);
	}

	@Override
	public void onPreTick() {
		boolean visible = mc.currentScreen instanceof HandledScreen<?>;

		upperList.setVisible(visible);
		upperList.setActive(visible);

		upperList.multiplyScale(clientConfig.getGuiScale());

		if (mc.currentScreen instanceof HandledScreen<?> screen) {
			upperList.setScaleCenterY(() -> (float) screen.y);
		}
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		if (mc.currentScreen instanceof HandledScreen<?> screen) {
			upperList.setY(screen.y - upperList.getHeight() - INDENT);
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(DrawConstants.getScreenWidth());
		setHeight(DrawConstants.getScreenHeight());

		upperList.setWidth(width);
		upperList.setIndent(3.5f);
	}

	public void addUpperOverlay(Widget widget) {
		upperList.addChild(widget);
	}
}
