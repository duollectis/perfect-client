package com.perfect.client.gui.widgets.picker.utils;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;

@Setter
@Getter
public class PointRenderer {

	private float size = 3.0f;

	private float centerX;
	private float centerY;
	private Color color = Color.WHITE;

	public void draw(Matrix3x2fStack matrices) {
		ScissorStack.expand(matrices, 2);

		float halfSize = size / 2.0f;

		float x = centerX - halfSize;
		float y = centerY - size / 2.0f;

		float shadowOffset = -0.5f;

		Renderer2D.round()
		          .pos(x - shadowOffset, y - shadowOffset)
		          .size(size + shadowOffset * 2.0f, size + shadowOffset * 2.0f)
		          .rounding(halfSize + shadowOffset)
		          .softness(1.1f)
		          .color(Color.DARK_GRAY)
		          .draw(matrices);

		Renderer2D.round()
		          .pos(x, y, size, size)
		          .rounding(halfSize)
		          .thickness(0.75f)
		          .softness(0.05f)
		          .outlineColor(Color.WHITE)
		          .color(color)
		          .draw(matrices);

		ScissorStack.setLast(matrices);
	}
}
