package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.render.renderer.Renderer2D;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.util.function.Supplier;

@Setter
public class TextureWidget extends Widget {

	private Supplier<Integer> textureSupplier;

	private float size = 3.7f;

	public TextureWidget(Supplier<Integer> textureSupplier) {
		this.textureSupplier = textureSupplier;
	}

	public TextureWidget size(float size) {
		this.size = size;
		return this;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		float centerX = drawX + parent.width / 2.0f;
		float centerY = drawY + parent.height / 2.0f;

		Renderer2D.drawCenteredTexture(
				matrices,
				textureSupplier.get(),
				centerX, centerY,
				size,
				color,
				null
		);
	}
}
