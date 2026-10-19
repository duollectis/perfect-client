package com.perfect.client.core.render.shader.renderer.round;

import com.perfect.client.gui.utils.Coloring;
import lombok.Getter;

import java.awt.*;

@Getter
@SuppressWarnings("unchecked")
public class ColoredRoundBuilder<T extends ColoredRoundBuilder<T>> extends RoundBuilder<T> {

	protected float outlineAlpha = 1.0f;

	protected Color outlineColor = Color.WHITE;

	protected Coloring coloring = Coloring.solid(Color.WHITE);

	public ColoredRoundBuilder(IBuiltDraw<T> drawAction) {
		super(drawAction);
	}

	public T outlineAlpha(float outlineAlpha) {
		this.outlineAlpha = outlineAlpha;
		return (T) this;
	}

	public T outlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
		return (T) this;
	}

	public T color(Color color) {
		return color(Coloring.solid(color));
	}

	public T color(Coloring coloring) {
		this.coloring = coloring;
		return (T) this;
	}
}
