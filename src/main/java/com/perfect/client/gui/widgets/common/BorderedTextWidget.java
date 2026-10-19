package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.core.Font;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public class BorderedTextWidget extends Widget {

	private static final SizedFont DEFAULT_FONT = new SizedFont(() -> MONTSERRAT_MEDIUM, 3.5f);

	private static final float BORDER_SIZE = 5.0f;
	private static final float SCROLL_DEPTH = 4.0f;

	@Setter
	private Supplier<String> textSupplier;

	@Setter
	private SizedFont font = DEFAULT_FONT;

	@Setter
	private Supplier<Color> colorSupplier = () -> Color.WHITE;

	@Setter
	private Supplier<Boolean> scrollCondition = () -> false;

	private float scrolling = 0.0f;
	private boolean needScroll;

	private float borderStart;

	@Setter
	private float textX;

	public BorderedTextWidget(Supplier<String> textSupplier) {
		this.textSupplier = textSupplier;
	}

	@Override
	public void onOpen() {
		scrolling = 0.0f;
	}

	@Override
	public void onPreTick() {
		if (scrollCondition.get() || scrolling > 0.0f) {
			float scrollSpeed = 0.2f;
			scrolling += scrollSpeed;

			float maxScrolling = font.getTextWidth(textSupplier.get()) + SCROLL_DEPTH;

			if (scrolling > maxScrolling) {
				scrolling = 0.0f;
			}
		}

		needScroll = font.getTextWidth(textSupplier.get()) > borderStart;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		borderStart = width - BORDER_SIZE;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		String text = textSupplier.get();
		Color color = colorSupplier.get();

		float textY = height / 2.0f;

		if (needScroll) {
			Fonts.setCustomRenderer((font, builder) -> Renderer2D.BORDERED_FONT_SHADER.draw(
					matrices,
					builder,
					font.getAtlas().getGlId(),
					drawX + width,
					5.0f,
					Font.FONT_HARDNESS
			));

			ScissorStack.pushScissor(matrices, drawX + textX, drawY, width, height);

			MONTSERRAT_MEDIUM.drawYCentered(
					matrices,
					text,
					3.5f,
					drawX + textX - scrolling,
					drawY + textY,
					color
			);

			MONTSERRAT_MEDIUM.drawYCentered(
					matrices,
					text,
					3.5f,
					drawX + textX + font.getTextWidth(text) + SCROLL_DEPTH - scrolling,
					drawY + textY,
					color
			);

			ScissorStack.popScissor(matrices);

			Fonts.setCustomRenderer(null);
		}
		else {
			MONTSERRAT_MEDIUM.drawYCentered(
					matrices,
					text,
					3.5f,
					drawX + textX,
					drawY + textY,
					color
			);
		}
	}

	public void resetScroll() {
		scrolling = 0.0f;
	}
}
