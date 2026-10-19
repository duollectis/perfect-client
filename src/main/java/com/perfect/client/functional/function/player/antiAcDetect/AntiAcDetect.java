package com.perfect.client.functional.function.player.antiAcDetect;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ToggleContainer;
import com.perfect.client.functional.function.player.antiAcDetect.handlers.*;
import com.perfect.client.functional.function.player.antiAcDetect.utils.AntiAcDetectHandler;

public class AntiAcDetect extends Function {

	private final AntiAcDetectConfig config = new AntiAcDetectConfig(this);

	private final ToggleContainer<AntiAcDetectHandler> toggleContainer = createToggleHandler();

	public AntiAcDetect() {
		super("anti-ac-detect", "Anti AC Detect", Category.PLAYER);

		toggleContainer.add(config.getNoMultiSwap(), new MultiSwapHandler(this, config));
		toggleContainer.add(config.getNoAimDuplicate(), new AimDuplicateHandler(this, config));
		toggleContainer.add(config.getNoSlotDuplicate(), new SlotDuplicateHandler(this, config));
		toggleContainer.add(config.getNoSprintDuplicate(), new SprintDuplicateHandler(this, config));
		toggleContainer.add(config.getNoInputDuplicate(), new InputDuplicateHandler(this, config));
		toggleContainer.add(config.getNoSelfInteract(), new SelfInteractHandler(this, config));
	}

	public void onToggled() {
		toggleContainer.onToggled();
	}
}
