package com.perfect.client.core.render.shader.renderer.round;

import com.perfect.client.core.utils.Orientation;
import lombok.Getter;

@Getter
public class HueBuilder extends RoundBuilder<HueBuilder> {

	protected float hueOffset = 0.0f;
	protected Orientation orientation = Orientation.HORIZONTAL;

	public HueBuilder(IBuiltDraw<HueBuilder> drawAction) {
		super(drawAction);
	}

	public HueBuilder hueOffset(float hueOffset) {
		this.hueOffset = hueOffset;
		return this;
	}

	public HueBuilder orientation(Orientation orientation) {
		this.orientation = orientation;
		return this;
	}
}
