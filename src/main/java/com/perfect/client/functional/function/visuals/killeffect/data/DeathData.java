package com.perfect.client.functional.function.visuals.killeffect.data;

import com.perfect.client.core.utils.math.TimerUtils;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;

public class DeathData {

	private final TimerUtils timer = new TimerUtils();
	private final Runnable runnable;

	@Getter
	private LivingEntity entity;
	private long lifetimeMs;
	private boolean wasTriggered = false;

	public DeathData(LivingEntity entity, long lifetimeMs, Runnable runnable) {
		this.entity = entity;
		this.lifetimeMs = lifetimeMs;
		this.runnable = runnable;
		this.timer.reset();
	}

	public void refresh(LivingEntity entity, long lifetimeMs) {
		if (this.entity == null || this.entity.getHealth() != 0.0f) {
			this.entity = entity;
			this.lifetimeMs = lifetimeMs;
			this.timer.reset();
			this.wasTriggered = false;
		}
	}

	public boolean isAlive() {
		return this.entity != null && this.lifetimeMs != 0L && !this.timer.passedMillis(this.lifetimeMs);
	}

	public void checkAndTrigger() {
		if (isAlive() && entity.getHealth() == 0.0f && !wasTriggered) {
			runnable.run();
			wasTriggered = true;
			lifetimeMs = 300L;
		}
	}
}