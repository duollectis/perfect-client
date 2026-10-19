package com.perfect.client.gui.widgets.list.blockColor;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.widgets.list.ItemPreviewWidget;
import org.joml.Matrix3x2fStack;

public class BlockColorItemPreviewWidget extends ItemPreviewWidget {

	private static final float COLOR_SIZE = 5.0f;

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		super.onDraw(matrices);

		Renderer2D.round()
		          .pos(drawX + width - COLOR_SIZE - X_OFFSET, drawY + height / 2.0f - COLOR_SIZE / 2.0f)
		          .size(COLOR_SIZE, COLOR_SIZE)
		          .rounding(COLOR_SIZE / 2.0f)
		          .color(color)
		          .draw(matrices);
	}

	@Override
	public float getRightTextOffset() {
		return COLOR_SIZE + X_OFFSET;
	}
}
