package com.perfect.client.gui.widgets.picker.color.text;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorTextFidget extends HorizontalListWidget {

	private final Supplier<Color> colorGetter;
	private final Consumer<Color> colorSetter;

	private final int channelsCount;

	private float channelWidth;

	public ColorTextFidget(Supplier<Color> colorGetter, Consumer<Color> colorSetter, Channels channels) {
		this.colorGetter = colorGetter;
		this.colorSetter = colorSetter;

		setNoWidthCalc(true);

		switch (channels) {
			case RGBA -> {
				initRGB();

				addChild(new ColorChannelWidget(
						() -> (int) (colorGetter.get().getAlpha() / 255.0f * 100.0f),
						a -> setAlpha(ColorUtils.clamp((int) (a / 100.0f * 255.0f + 1)))
				).suffix("%"));

				channelsCount = 4;
			}
			case RGB -> {
				initRGB();
				channelsCount = 3;
			}
			default -> throw new RuntimeException();
		}
	}

	private void initRGB() {
		addChild(new ColorChannelWidget(
				() -> colorGetter.get().getRed(),
				r -> setRed(ColorUtils.clamp(r))
		));

		addChild(new ColorChannelWidget(
				() -> colorGetter.get().getGreen(),
				g -> setGreen(ColorUtils.clamp(g))
		));

		addChild(new ColorChannelWidget(
				() -> colorGetter.get().getBlue(),
				b -> setBlue(ColorUtils.clamp(b))
		));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		setHeight(5.75f);
		setRounding(0.75f);

		channelWidth = width / channelsCount;

		getChildren().forEach(widget -> {
			widget.setWidth(channelWidth);
			widget.setHeight(height);
		});
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .draw(matrices);

		for (int i = 1; i < channelsCount; i++) {
			Renderer2D.round()
			          .pos(drawX + channelWidth * i, drawY + 0.25f, 0.2f, height - 0.5f)
			          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
			          .draw(matrices);
		}
	}

	public void set(int r, int g, int b, int a) {
		colorSetter.accept(new Color(r, g, b, a));
	}

	public void setRed(int r) {
		Color c = colorGetter.get();
		set(r, c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public void setGreen(int g) {
		Color c = colorGetter.get();
		set(c.getRed(), g, c.getBlue(), c.getAlpha());
	}

	public void setBlue(int b) {
		Color c = colorGetter.get();
		set(c.getRed(), c.getGreen(), b, c.getAlpha());
	}

	public void setAlpha(int a) {
		Color c = colorGetter.get();
		set(c.getRed(), c.getGreen(), c.getBlue(), a);
	}

	public enum Channels {
		RGBA,
		RGB
	}
}
