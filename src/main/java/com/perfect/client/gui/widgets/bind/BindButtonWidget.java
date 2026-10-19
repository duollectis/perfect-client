package com.perfect.client.gui.widgets.bind;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.utils.StringIndicator;
import com.perfect.client.gui.widgets.common.Widget;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class BindButtonWidget extends Widget {

	private static final float TEXT_X_OFFSET = 1.0f;
	private static final float BIND_TEXT_X_OFFSET = 1.5f;

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 3.5f);

	private final StringIndicator indicator = new StringIndicator();

	private final AbstractBindListener bindListener;

	public BindButtonWidget(AbstractBindListener bindListener) {
		this.bindListener = bindListener;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(5.0f);
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		if (bindListener.isBinding()) {
			container.get().disableEscape();
		}
		else {
			indicator.reset();
		}
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		font.drawYCentered(matrices, "Bind", drawX + TEXT_X_OFFSET, drawY + height / 2.0f, Color.WHITE);

		String bind = bindListener.isBinding() ? indicator.get() : bindListener.getBind().toString();

		float boxWidth = font.getTextWidth(bind) + BIND_TEXT_X_OFFSET * 2.0f;
		float boxX = drawX + width - boxWidth;

		Renderer2D.round()
		          .pos(boxX, drawY, boxWidth, height)
		          .rounding(1.0f)
		          .thickness(0.5f).color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .draw(matrices);

		font.drawYCentered(
				matrices,
				bind,
				boxX + BIND_TEXT_X_OFFSET,
				drawY + height / 2.0f,
				getBindColor()
		);
	}

	private Color getBindColor() {
		if (bindListener.getBind().isSet() || bindListener.isBinding()) {
			return Managers.COLOR.get(Managers.COLOR.MAIN);
		}

		return Color.WHITE;
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!Managers.BIND.isWaitMouseRelease()
				&& button.isClick(event, mouseX, mouseY)
				&& !bindListener.isBinding()) {
			bindListener.setBinding(true);
		}
	}
}
