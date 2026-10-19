package com.perfect.client.core.render.animation.value;

import com.perfect.client.core.utils.math.MathUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoubleAnimation extends ValueAnimation<Double> {
	
	public DoubleAnimation(double speed) {
		super(speed);
		current = 0.0;
	}

	@Override
	protected Double interpolate(Double current, Double target, double step) {
		return MathUtils.interpolate(current, target, step);
	}
}