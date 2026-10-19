package com.perfect.client.core.utils.math;

import com.perfect.client.core.manager.Managers;


public class TimerUtils {

	private long time = System.currentTimeMillis();

	public void reset() {
		time = System.currentTimeMillis();
	}

	public void setMillis(long time) {
		this.time = System.currentTimeMillis() - time;
	}

	public long getMillis() {
		return System.currentTimeMillis() - time;
	}

	public double getSeconds() {
		return millisToSeconds(getMillis());
	}

	public double getTicks() {
		return millisToTicks(getMillis());
	}

	public boolean passedMillis(long time) {
		return getMillis() >= time;
	}

	public boolean passedMillisTps(long time) {
		return passedMillis(Managers.TICKRATE.getDelay(time));
	}

	public boolean passedSeconds(double time) {
		return getSeconds() >= time;
	}

	public boolean passedTicks(double ticks) {
		return getTicks() >= ticks;
	}

	public static double millisToTicks(long millis) {
		return millis / 50.0;
	}

	public static double millisToSeconds(long millis) {
		return millis / 1000.0;
	}
}
