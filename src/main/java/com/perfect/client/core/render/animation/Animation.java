package com.perfect.client.core.render.animation;

import com.perfect.client.core.render.animation.easing.IEasing;
import com.perfect.client.core.utils.math.TimerUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Animation {

	private final TimerUtils timer;

	private int time;
	private boolean direction;
	private float size = 1;

	private IEasing easing;

	public Animation(IEasing easing, int time) {
		this(new TimerUtils(), easing, 1, time, true);
	}

	public Animation(IEasing easing, int time, boolean direction) {
		this(new TimerUtils(), easing, 1, time, direction);
	}

	public Animation(TimerUtils timer, IEasing easing, float size, int time, boolean direction) {
		this.easing = easing;
		this.size = size;
		this.time = time;
		this.direction = direction;
		this.timer = timer;
	}

	
	public float get() {
		return changeDirectionAndGet(direction);
	}

	
	public float changeDirectionAndGet(boolean direction) {
		changeDirection(direction);

		if (this.direction) {
			if (timer.passedMillis(time)) {
				return size;
			}

			return getRaw() * size;
		}
		else {
			if (timer.passedMillis(time)) {
				return 0f;
			}

			return (1f - getRaw()) * size;
		}
	}

	private float getRaw() {
		return Math.clamp(easing.apply((float) timer.getMillis() / time), 0.0f, 1.0f);
	}

	public void changeDirection(boolean direction) {
		if (this.direction != direction) {
			timer.setMillis(Math.max(time - timer.getMillis(), 0));
			setDirection(direction);
		}
	}

	public Animation finish(boolean direction) {
		setDirection(direction);
		return finish();
	}

	public Animation finish() {
		timer.setMillis(time);
		return this;
	}

	public boolean isFinished(boolean direction) {
		return timer.passedMillis(time) && direction == this.direction;
	}

	public void cycle() {
		if (isFinished(true)) {
			setDirection(false);
		}
		else if (isFinished(false)) {
			setDirection(true);
		}
	}

	public void reset() {
		timer.reset();
	}
}
