package com.perfect.client.gui.widgets;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.widgets.common.ColorWidget;
import com.perfect.client.gui.widgets.common.TextWidget;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.button.ToggleButtonWidget;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import lombok.Getter;
import org.joml.Matrix3x2fStack;

public class UIButtonsWidget extends Widget {

	private static final float BUTTON_WIDTH = 30.0f;
	private static final float BUTTON_HEIGHT = 8.3f;
	private static final float BUTTON_ROUNDING = 2.3f;
	private static final float INDENT = 1.8f;

	private final HorizontalListWidget buttonList = addChild(new HorizontalListWidget());

	@Getter
	private final ToggleButtonWidget themeButton = buttonList.addChild(new ToggleButtonWidget(
			active -> onButtonToggle(active, UIWidget.getInstance().getThemeContainer())
	).add(new ColorWidget(() -> Managers.COLOR.get(Managers.COLOR.MAIN))));

	@Getter
	private final ToggleButtonWidget settingsButton = buttonList.addChild(new ToggleButtonWidget(
			active -> onButtonToggle(active, UIWidget.getInstance().getClientSettingsContainer())
	).add(new TextWidget(() -> "Settings")));

	@Getter
	private final ToggleButtonWidget hudSettingsButton = buttonList.addChild(new ToggleButtonWidget(
			active -> onButtonToggle(active, UIWidget.getInstance().getHudSettingsContainer())
	).add(new TextWidget(() -> "HUD")));

	@Getter
	private final ToggleButtonWidget configsButton = buttonList.addChild(new ToggleButtonWidget(
			active -> onButtonToggle(active, UIWidget.getInstance().getConfigsContainer())
	).add(new TextWidget(() -> "Configs")));

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		buttonList.getChildren().forEach(button -> {
			button.setWidth(BUTTON_WIDTH);
			button.setHeight(BUTTON_HEIGHT);
			button.setRounding(BUTTON_ROUNDING);
		});

		themeButton.width(BUTTON_HEIGHT);

		buttonList.setX(INDENT);
		buttonList.setY(INDENT);
		buttonList.setIndent(INDENT);

		width = buttonList.getWidth() + INDENT * 2f;
		height = buttonList.getHeight() + INDENT * 2f;

		setRounding(3.8f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.blurredRound()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.6f)
		          .blur(2.0f)
		          .alpha(0.9f)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.BACKGROUND))
		          .draw(matrices);
	}

	private void onButtonToggle(boolean active, WindowContainer windowContainer) {
		if (active) {
			windowContainer.open();
		}
		else {
			windowContainer.close();
		}
	}
}
