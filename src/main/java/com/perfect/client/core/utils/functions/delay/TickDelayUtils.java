package com.perfect.client.core.utils.functions.delay;

import com.perfect.client.core.utils.range.IntRange;
import com.perfect.client.core.utils.range.utils.IntRangeGenerator;

import java.util.function.Supplier;

public class TickDelayUtils extends IntRangeGenerator {

	private int ticks;

	public TickDelayUtils(Supplier<IntRange> rangeSupplier) {
		super(rangeSupplier);
	}

	public void tick() {
		ticks++;
	}

	public void setPassed() {
		ticks = getValue();
	}

	public void reset() {
		resetTimer();
		update();
	}

	public void resetTimer() {
		ticks = 0;
	}

	public boolean isPassed() {
		return ticks >= getValue();
	}
}
