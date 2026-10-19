package com.perfect.client.gui.widgets.picker.multi.panel.settings.table;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.render.animation.value.ColorAnimation;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MutableColor;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Consumer;

public class ColorElementWidget extends Widget {

	private static final float SIZE = 6.25f;

	@Getter
	@Setter
	private boolean selected;

	private final MutableColor color;

	private final Consumer<ColorElementWidget> selectConsumer;

	private final ColorAnimation selectColorAnimation = new ColorAnimation().speed(0.1).defined(false);

	public ColorElementWidget(MutableColor color, Consumer<ColorElementWidget> selectConsumer) {
		this.color = color;
		this.selectConsumer = selectConsumer;
	}

	public MutableColor getMutableColor() {
		return color;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(SIZE);
		setHeight(SIZE);
		setRounding(SIZE / 2.0f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .color(color.getColor())
		          .draw(matrices);

		if (selected) {
			float centerX = drawX + SIZE / 2.0f;
			float centerY = drawY + SIZE / 2.0f;

			float outerSize = 5.25f;
			float innerSize = 3.75f;

			Renderer2D.round()
			          .pos(centerX - outerSize / 2.0f, centerY - outerSize / 2.0f)
			          .size(outerSize, outerSize)
			          .rounding(outerSize / 2.0f)
			          .color(selectColorAnimation.updateAndGet(getSelectColorAnimation()))
			          .draw(matrices);

			Renderer2D.round()
			          .pos(centerX - innerSize / 2.0f, centerY - innerSize / 2.0f)
			          .size(innerSize, innerSize)
			          .rounding(innerSize / 2.0f)
			          .color(color.getColor())
			          .draw(matrices);
		}
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY)) {
			selectConsumer.accept(this);
		}
	}

	private Color getSelectColorAnimation() {
		if (isBright(color.getColor())) {
			return Color.BLACK;
		}
		else {
			return Color.WHITE;
		}
	}

	private boolean isBright(Color color) {
		float[] hsb = ColorUtils.toHsb(color);

		boolean h = hsb[0] > 0.12f && hsb[0] < 0.55f;
		boolean s = hsb[1] < 0.3f || h;
		boolean b = hsb[2] > 0.25f;

		return s && b;
	}
}
