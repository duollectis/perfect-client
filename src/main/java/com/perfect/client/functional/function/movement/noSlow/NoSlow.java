package com.perfect.client.functional.function.movement.noSlow;

import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.movement.noSlow.modes.GrimMode;
import com.perfect.client.functional.function.movement.noSlow.modes.GrimV3Mode;
import com.perfect.client.functional.function.movement.noSlow.utils.NoSlowMode;
import lombok.Getter;


public class NoSlow extends Function {

	private final NoSlowConfig config = new NoSlowConfig(this);

	@Getter
	private final ModeContainer<Mode, NoSlowMode> modeContainer = createModeContainer(config::getMode)
			.add(Mode.VANILLA, new NoSlowMode(this, config))
			.add(Mode.GRIM, new GrimMode(this, config))
			.add(Mode.GRIM_V3, new GrimV3Mode(this, config));

	public NoSlow() {
		super("no-slow", "No Slow", Category.MOVEMENT, "No Slow Down");
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	@Override
	public void onItemSlowDown(Cancellable event) {
		if (config.getItemUse().get() && canNoItemSlowDown() && modeContainer.getCurrentMode().canNoItemSlowDown()) {
			event.cancel();
		}
	}

	@Override
	public void onSneakSlowDown(Cancellable event) {
		if (config.getSneak().get()) {
			event.cancel();
		}
	}

	@Override
	public void onCrawlSlowDown(Cancellable event) {
		if (config.getCrawl().get()) {
			event.cancel();
		}
	}

	private boolean canNoItemSlowDown() {
		if (mc.player == null) {
			return false;
		}
		if (mc.player.isBlocking()) {
			return config.getShields().get();
		}
		return true;
	}

	public enum Mode {
		VANILLA,
		GRIM,
		GRIM_V3
	}
}
