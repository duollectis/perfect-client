package com.perfect.client.functional.function.misc.autoOminous;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.misc.autoOminous.handlers.RenderHandler;
import lombok.Getter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.OminousBottleAmplifierComponent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoOminous extends Function {

	private static final int DEFAULT_SLOT = 4;
	private static final int RESET_MILLIS = 8000;

	private final AutoOminousConfig config = new AutoOminousConfig(this);

	private final TimerUtils usingTimer = new TimerUtils();

	@Getter
	private BlockPos farmPos;

	private boolean swapped;

	@Getter
	private boolean using;

	@Getter
	private boolean found;

	public AutoOminous() {
		super("auto-ominous", "Auto Ominous", Category.MISC, "Bottles", "Raid");

		addHandler(new RenderHandler(this, config));
	}

	public void updateFarmPos() {
		if (mc.player != null) {
			farmPos = mc.player.getBlockPos();
		}
	}

	@Override
	protected void onDisable() {
		stopUsing();
		swapBack();
		reset();
	}

	@Override
	public void onGameJoin() {
		reset();
	}

	@Override
	public void onGameLeft() {
		reset();
	}

	private void reset() {
		swapped = false;
		using = false;
		usingTimer.reset();
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (swapped && event.getPacket() instanceof UpdateSelectedSlotC2SPacket) {
			event.cancel();
		}

		if (using) {
			if (event.getPacket() instanceof PlayerActionC2SPacket packet
					&& packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM
			) {
				event.cancel();
			}

			if (event.getPacket() instanceof PlayerInteractItemC2SPacket) {
				event.cancel();
			}

			if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
				event.cancel();
			}

			if (event.getPacket() instanceof PlayerInteractBlockC2SPacket) {
				event.cancel();
			}
		}
	}

	@Override
	public void onPreTick() {
		if (mc.player == null || !mc.player.isOnGround()) {
			return;
		}

		if (isOutOfRange()) {
			stopUsing();
			swapBack();
			return;
		}

		if (checkEffects()) {
			using = false;
			swapBack();
			return;
		}

		if (using && usingTimer.passedMillis(RESET_MILLIS)) {
			stopUsing();
			swapBack();
			return;
		}

		if (mc.currentScreen != null || mc.player.isUsingItem()) {
			return;
		}

		if (swapped) {
			startUsing();
			return;
		}

		FindItemResult maxBottleResult = findMaxLevelBottle();

		found = true;

		if (maxBottleResult.isHotbar()) {
			if (!swapped) {
				int slot = maxBottleResult.slot();

				if (!Managers.NETWORK.isHolding(slot)) {
					sendPacket(new UpdateSelectedSlotC2SPacket(slot));
				}

				swapped = true;
			}

			return;
		}

		if (maxBottleResult.found()) {
			FindItemResult emptyResult = InvUtils.findInHotbar(false, ItemStack::isEmpty);

			int slot = maxBottleResult.slot();

			if (emptyResult.found()) {
				InvUtils.move(slot, emptyResult.slot());
			}
			else {
				InvUtils.move(slot, DEFAULT_SLOT);
			}

			return;
		}

		found = false;
	}

	public boolean checkEffects() {
		assert mc.player != null;
		return mc.player.hasStatusEffect(StatusEffects.BAD_OMEN) || mc.player.hasStatusEffect(StatusEffects.RAID_OMEN);
	}

	public boolean isOutOfRange() {
		if (farmPos == null || mc.player == null || !PlayerUtils.isSurvival()) {
			return true;
		}

		double range = config.getTriggerRange().get();
		return farmPos.getSquaredDistance(mc.player.getPos()) > range * range;
	}

	private void swapBack() {
		if (swapped) {
			swapped = false;

			if (mc.player != null) {
				sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().getSelectedSlot()));
			}
		}
	}

	private void startUsing() {
		assert mc.player != null;

		if (using) {
			return;
		}

		usingTimer.reset();

		InvUtils.useItem(Hand.MAIN_HAND, mc.player.getYaw(), mc.player.getPitch());

		using = true;
	}

	private void stopUsing() {
		if (using) {
			using = false;

			usingTimer.reset();

			sendPacket(new PlayerActionC2SPacket(
					PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
					BlockPos.ORIGIN,
					Direction.DOWN
			));
		}
	}

	private static FindItemResult findMaxLevelBottle() {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		int maxLevel = -1;
		int maxSlot = -1;
		ItemStack maxStack = ItemStack.EMPTY;

		int size = mc.player.getInventory().size();

		for (int i = 0; i <= size; i++) {
			ItemStack stack = InvUtils.getStack(i);

			if (stack.getItem() == Items.OMINOUS_BOTTLE) {
				OminousBottleAmplifierComponent component = stack.getComponents().get(
						DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER
				);

				if (component != null) {
					int level = component.value();

					if (level == 4) {
						return new FindItemResult(stack, i);
					}

					if (level > maxLevel) {
						maxLevel = level;
						maxStack = stack;
						maxSlot = i;
					}
				}
			}
		}

		return new FindItemResult(maxStack, maxSlot);
	}
}
