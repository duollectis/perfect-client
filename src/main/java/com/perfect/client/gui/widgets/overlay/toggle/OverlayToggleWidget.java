package com.perfect.client.gui.widgets.overlay.toggle;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.settings.elements.toggle.ToggleWidget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

@Setter
public class OverlayToggleWidget extends Widget {

	private final static float X_OFFSET = 3.25f;

	private final static SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 4.0f);

	private Runnable toggleAction = () -> {};

	private Supplier<Boolean> toggledSupplier = () -> false;
	private Supplier<String> nameSupplier = () -> "";

	private final ToggleWidget toggle = addChild(new ToggleWidget(() -> toggledSupplier.get()));

	public void finishAnimation(boolean direction) {
		toggle.finishAnimation(direction);
	}

	@Override
	public void onPreTick() {
		toggle.setToggledSupplier(toggledSupplier);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(font.getTextWidth(nameSupplier.get()) + toggle.getWidth() + X_OFFSET * 3.0f);
		setHeight(9.0f);
		setRounding(1.5f);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		toggle.setX(width - X_OFFSET - toggle.getWidth());
		toggle.setY(height / 2.0f - toggle.getHeight() / 2.0f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .thickness(0.5f)
		          .rounding(rounding)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .draw(matrices);

		font.drawYCentered(matrices, nameSupplier.get(), drawX + X_OFFSET, drawY + height / 2.0f, Color.WHITE);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!event.isPress() || !isHovered()) {
			return;
		}

		if (event.isLeft() || event.isRight()) {
			toggleAction.run();
		}
	}
}
