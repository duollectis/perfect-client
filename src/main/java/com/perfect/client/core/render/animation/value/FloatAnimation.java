package com.perfect.client.core.render.animation.value;

import com.perfect.client.core.utils.math.MathUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloatAnimation extends ValueAnimation<Float> {

	public FloatAnimation(double speed) {
		super(speed);
		current = 0.0f;
	}

	@Override
	protected Float interpolate(Float current, Float target, double step) {
		return MathUtils.interpolateFloat(current, target, step);
	}
}