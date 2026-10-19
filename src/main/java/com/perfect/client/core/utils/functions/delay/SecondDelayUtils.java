package com.perfect.client.core.utils.functions.delay;

import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.core.utils.range.DoubleRange;
import com.perfect.client.core.utils.range.utils.DoubleRangeGenerator;

import java.util.function.Supplier;

public class SecondDelayUtils extends DoubleRangeGenerator {

	private final TimerUtils timer = new TimerUtils();

	public SecondDelayUtils(Supplier<DoubleRange> rangeSupplier) {
		super(rangeSupplier);
	}

	public boolean isPassedSeconds() {
		return timer.passedSeconds(getValue());
	}

	public void reset() {
		super.update();
		timer.reset();
	}
}
