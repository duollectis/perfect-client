package com.perfect.client.gui.widgets.settings.elements.color;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public abstract class AbstractColorWidget<T, S extends ISetting<T>> extends SettingWidget<T, S> {

	private static final float SIZE = 4.4f;

	public AbstractColorWidget(S setting) {
		super(setting);
	}

	@Override
	public float getBorderedWidth() {
		return SIZE;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(SIZE);
		setRounding(SIZE / 2.0f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		float boxX = drawX + width - SIZE;

		Color color = getDrawColor();

		Renderer2D.round()
		          .pos(boxX, drawY, SIZE, SIZE)
		          .rounding(rounding)
		          .alpha(color.getAlpha() / 255.0f)
		          .color(color)
		          .draw(matrices);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY)) {
			openWindow();
		}
	}

	protected abstract Color getDrawColor();

	protected abstract void openWindow();
}

