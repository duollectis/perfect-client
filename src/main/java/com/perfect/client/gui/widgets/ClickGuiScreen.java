package com.perfect.client.gui.widgets;

import com.perfect.client.gui.WidgetContainer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {

	private final WidgetContainer widgetContainer = new WidgetContainer().setMainWidget(new UIWidget());

	public ClickGuiScreen() {
		super(Text.of("perfect-click-gui"));
	}

	@Override
	protected void init() {
		widgetContainer.open();
	}

	@Override
	public void removed() {
		widgetContainer.close();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return widgetContainer.isEscapeEnabled();
	}

	@Override
	public void applyBlur(DrawContext context) {
		// disable blur
	}
}
