package com.perfect.client.functional.function.player.selfBow;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.player.selfBow.utils.SelfBowUtils;
import lombok.Getter;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SelfBow extends Function {

	private final SelfBowConfig config = new SelfBowConfig(this);
	private final SelfBowUtils selfBowUtils = new SelfBowUtils(this, config);

	private final List<Integer> arrowSlots = new ArrayList<>();
	private final TimerUtils timer = new TimerUtils();

	private FindItemResult bow;
	private int prevSlot = -1;

	public SelfBow() {
		super("self-bow", "Self Bow", Category.PLAYER, "Quiver");
	}

	@Override
	public void onEnable() {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		prevSlot = -1;
		bow = InvUtils.find(Items.BOW);

		if (selfBowUtils.check()) {
			return;
		}

		selfBowUtils.resetPlayerAction();
		timer.reset();

		if (!bow.isMainHand()) {
			if (bow.isHotbar()) {
				prevSlot = mc.player.getInventory().getSelectedSlot();

				InvUtils.setSlot(bow.slot(), false);
			}
		}

		selfBowUtils.scanAndFillArrows();
	}

	@Override
	public void onDisable() {
		InvUtils.setSlot(prevSlot, false);
	}

	@Override
	public void onPreTick() {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		if (arrowSlots.isEmpty()) {
			disable();
			return;
		}

		if (!timer.passedTicks(config.getDelay().get())) {
			return;
		}

		if (!mc.options.useKey.isPressed()) {
			selfBowUtils.prepareArrowToSlot();
		}
		else {
			selfBowUtils.processBowShot();
		}
	}

	public enum Effects {
		SPEED,
		STRENGTH,
		JUMP_BOOST,
		REGENERATION
	}
}
