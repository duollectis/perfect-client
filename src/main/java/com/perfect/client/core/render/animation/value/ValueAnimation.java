package com.perfect.client.core.render.animation.value;

import com.google.common.base.Supplier;
import com.perfect.client.core.utils.math.TimerUtils;
import lombok.Getter;

@Getter
@SuppressWarnings("unchecked")
public abstract class ValueAnimation<T extends Number> {

	private final TimerUtils timer = new TimerUtils();

	protected T current;
	private Supplier<T> targetSupplier;
	private double speed;

	private boolean defined = true;

	public ValueAnimation(double speed) {
		this.speed = speed;
	}

	public <K extends ValueAnimation<T>> K current(T current) {
		this.current = current;
		return (K) this;
	}

	public <K extends ValueAnimation<T>> K target(T target) {
		return target(() -> target);
	}

	public <K extends ValueAnimation<T>> K target(Supplier<T> targetSupplier) {
		this.targetSupplier = targetSupplier;
		return (K) this;
	}

	public <K extends ValueAnimation<T>> K speed(double speed) {
		this.speed = speed;
		return (K) this;
	}

	public <K extends ValueAnimation<T>> K defined(boolean defined) {
		this.defined = defined;
		return (K) this;
	}

	public T updateAndGet() {
		return updateAndGet(getTarget());
	}

	public T updateAndGet(T target) {
		if (!defined) {
			current = target;
			defined = true;
		}
		else {
			int steps = (int) (timer.getMillis() / 10L);

			if (steps > 0) {
				timer.reset();
				double step = Math.min(speed * 1.25f, 1f);

				for (int i = 0; i < steps; i++) {
					current = interpolate(current, target, step);
				}
			}
		}

		return current;
	}

	public T getTarget() {
		return targetSupplier.get();
	}

	protected abstract T interpolate(T current, T target, double step);

	public void set(T value) {
		this.current = value;
		timer.reset();
	}
}