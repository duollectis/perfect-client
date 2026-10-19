package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.render.renderer.Renderer2D;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

@Setter
public class ColorWidget extends Widget {

	private Supplier<Color> colorSupplier;

	private float size = 3.7f;

	public ColorWidget(Supplier<Color> colorSupplier) {
		this.colorSupplier = colorSupplier;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		float centerX = drawX + parent.width / 2.0f;
		float centerY = drawY + parent.height / 2.0f;

		float halfSize = size / 2.0f;

		float x = centerX - halfSize;
		float y = centerY - halfSize;

		Renderer2D.round()
		          .pos(x, y, size, size)
		          .rounding(halfSize)
		          .color(colorSupplier.get())
		          .draw(matrices);
	}
}
