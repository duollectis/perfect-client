package com.perfect.client.core.render.shader.renderer.round;

import com.perfect.client.gui.utils.Rounding;
import lombok.Getter;
import org.joml.Matrix3x2fStack;

@Getter
@SuppressWarnings("unchecked")
public class RoundBuilder<T extends RoundBuilder<T>> {

	private final IBuiltDraw<T> drawAction;

	protected float x;
	protected float y;
	protected float width;
	protected float height;

	protected Rounding rounding = Rounding.none();

	protected float thickness;
	protected float softness = 0.2f;

	protected float alpha = 1.0f;

	public RoundBuilder(IBuiltDraw<T> drawAction) {
		this.drawAction = drawAction;
	}

	public T pos(float x, float y, float width, float height) {
		pos(x, y);
		size(width, height);

		return (T) this;
	}

	public T pos(float x, float y) {
		this.x = x;
		this.y = y;
		return (T) this;
	}

	public T size(float width, float height) {
		this.width = width;
		this.height = height;
		return (T) this;
	}

	public T rounding(float rounding) {
		return rounding(Rounding.full(rounding));
	}

	public T rounding(Rounding rounding) {
		this.rounding = rounding;
		return (T) this;
	}

	public T thickness(float thickness) {
		this.thickness = thickness;
		return (T) this;
	}

	public T softness(float softness) {
		this.softness = softness;
		return (T) this;
	}

	public T alpha(float alpha) {
		this.alpha = alpha;
		return (T) this;
	}

	public void draw(Matrix3x2fStack matrices) {
		drawAction.draw(matrices, (T) this);
	}
}
