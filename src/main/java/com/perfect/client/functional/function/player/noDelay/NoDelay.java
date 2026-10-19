package com.perfect.client.functional.function.player.noDelay;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.range.utils.IntRangeGenerator;
import lombok.Getter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;


public class NoDelay extends Function {

	private final NoDelayConfig config = new NoDelayConfig(this);

	@Getter
	private final IntRangeGenerator jumpDelay = new IntRangeGenerator(() -> config.getJumpDelay().get());

	@Getter
	private final IntRangeGenerator placeDelay = new IntRangeGenerator(() -> config.getPlaceDelay().get());

	@Getter
	private final IntRangeGenerator breakDelay = new IntRangeGenerator(() -> config.getBreakDelay().get());

	@Getter
	private final IntRangeGenerator expDelay = new IntRangeGenerator(() -> config.getExpDelay().get());

	public NoDelay() {
		super("no-delay", "No Delay", Category.PLAYER, "Fast Exp");
	}

	
	@Override
	public void onHandleInput() {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		if (config.getActions().get(Actions.JUMP) && mc.player.jumpingCooldown > jumpDelay.getValue()) {
			mc.player.jumpingCooldown = jumpDelay.getValue();
			jumpDelay.update();
		}

		if (config.getActions().get(Actions.PLACE)
				&& mc.player.getMainHandStack().getItem() instanceof BlockItem
				&& mc.itemUseCooldown > placeDelay.getValue()
		) {
			mc.itemUseCooldown = placeDelay.getValue();
			placeDelay.update();
		}

		if (config.getActions().get(Actions.BREAK)
				&& mc.interactionManager.blockBreakingCooldown > breakDelay.getValue()
		) {
			mc.interactionManager.blockBreakingCooldown = breakDelay.getValue();
			breakDelay.update();
		}

		if (config.getActions().get(Actions.EXP)
				&& mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE
				&& mc.itemUseCooldown > expDelay.getValue()
		) {
			mc.itemUseCooldown = expDelay.getValue();
			expDelay.update();
		}
	}

	public enum Actions {
		JUMP,
		PLACE,
		BREAK,
		EXP
	}
}
