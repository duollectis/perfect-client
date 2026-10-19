package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.gui.utils.Align;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

@Setter
public class TextWidget extends Widget {

	private SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 4.0f);

	private Supplier<String> textSupplier;

	private Align xAlign = Align.CENTER;
	private Align yAlign = Align.CENTER;

	private Supplier<Color> colorSupplier = () -> Color.WHITE;

	public TextWidget(Supplier<String> textSupplier) {
		this.textSupplier = textSupplier;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		font.draw(
				matrices,
				textSupplier.get(),
				drawX,
				drawY,
				parent.width,
				parent.height,
				xAlign,
				yAlign,
				colorSupplier.get()
		);
	}

	public TextWidget font(SizedFont font) {
		this.font = font;
		return this;
	}
}
