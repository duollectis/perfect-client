package com.perfect.client.functional.function.misc.fastLatency;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.manager.game.ping.PingHandler;
import com.perfect.client.manager.game.ping.method.CommandCompletionsPingHandler;

public class FastLatency extends Function {

	private final FastLatencyConfig config = new FastLatencyConfig(this);

	private final TimerUtils timer = new TimerUtils();

	public FastLatency() {
		super("fast-latency", "Fast Latency", Category.MISC, "Ping");
	}

	@Override
	public void onGameJoin() {
		timer.reset();
	}

	@Override
	public void onGameLeft() {
		timer.reset();
	}

	@Override
	public void onPreTick() {
		if (timer.passedSeconds(config.getDelay().get())) {
			timer.reset();
			getMethod().sendPacket();
		}
	}

	private PingHandler getMethod() {
		PingHandler method = config.getMethod().get().getPingHandler();

		if (method instanceof CommandCompletionsPingHandler commandCompletionsMethod) {
			commandCompletionsMethod.setCommand(config.getCommand().get());
		}

		return method;
	}
}
