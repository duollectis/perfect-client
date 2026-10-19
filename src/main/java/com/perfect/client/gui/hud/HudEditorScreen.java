package com.perfect.client.gui.hud;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.gui.hud.base.GridAlignmentWidget;
import com.perfect.client.gui.utils.UiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HudEditorScreen extends Screen {

	private GridAlignmentWidget gridWidget;

	public HudEditorScreen() {
		super(Text.of("perfect-hud-editor"));
	}

	@Override
	protected void init() {
		Managers.HUD.getWidgetContainer().open();

		gridWidget = new GridAlignmentWidget();
		Managers.HUD.getHudWidget().addChild(gridWidget);
	}

	@Override
	public void removed() {
		if (gridWidget != null) {
			Managers.HUD.getHudWidget().getChildren().remove(gridWidget);
		}

		UiUtils.setDraggingWidget(null);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return Managers.HUD.getWidgetContainer().isEscapeEnabled();
	}

	@Override
	public void applyBlur(DrawContext context) {
		// disable blur
	}
}