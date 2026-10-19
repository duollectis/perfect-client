package com.perfect.client.core.utils.functions.delay;

import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.core.utils.range.IntRange;
import com.perfect.client.core.utils.range.utils.IntRangeGenerator;

import java.util.function.Supplier;

public class MillisDelayUtils extends IntRangeGenerator {

	private final TimerUtils timer = new TimerUtils();

	public MillisDelayUtils(Supplier<IntRange> rangeSupplier) {
		super(rangeSupplier);
	}

	public boolean isPassedMillis() {
		return timer.passedMillis(getValue());
	}

	public void reset() {
		super.update();
		resetTimer();
	}

	public void resetTimer() {
		timer.reset();
	}
}
