package com.perfect.client.functional.function.player.autoKit;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.EventHook;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.game.CommandUtils;


public class AutoKit extends Function {

	private final AutoKitConfig config = new AutoKitConfig(this);

	private boolean awaiting;

	public AutoKit() {
		super("auto-kit", "Auto Kit", Category.PLAYER);
	}

	@Override
	public void onEnable() {
		awaiting = false;
		autoKit();
	}

	@Override
	public void onRespawn() {
		autoKit();
	}

	
	private void autoKit() {
		if (awaiting) {
			return;
		}

		if (config.getDoubleKit().get()) {
			awaiting = true;
			CommandUtils.sendCommand(CommandUtils.trimCommand(config.getSecondKitCommand().get()));

			addPreTickHook(() -> {
				CommandUtils.sendCommand(CommandUtils.trimCommand(config.getKillCommand().get()));
				addPreTickHook(() -> {
					CommandUtils.sendCommand(CommandUtils.trimCommand(config.getKitCommand().get()));
					autoDisable();
					awaiting = false;
				});
			});
		}
		else {
			CommandUtils.sendCommand(CommandUtils.trimCommand(config.getKitCommand().get()));
			autoDisable();
		}
	}

	private void autoDisable() {
		if (config.getDisable().get()) {
			disable();
		}
	}

	private void addPreTickHook(Runnable runnable) {
		PerfectClient.EVENT_MANAGER.subscribe(new EventHook() {
			private int timer = config.getDelay().get();

			@Override
			public void onPreTick() {
				if (timer == 0) {
					if (isEnabled()) {
						runnable.run();
					}
					unhook();
				}
				else {
					timer--;
				}
			}
		});
	}
}
