package com.perfect.client.functional.function.player.middleClick;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;


public class MiddleClick extends Function {

	private final MiddleClickConfig config = new MiddleClickConfig(this);

	public MiddleClick() {
		super("middle-click", "Middle Click", Category.PLAYER);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (mc.player == null || mc.interactionManager == null)
			return;
		if (mc.currentScreen != null)
			return;
		if (event.getValue() != GLFW.GLFW_MOUSE_BUTTON_MIDDLE || !event.isPress())
			return;

		switch (config.getMode().get()) {
			case FRIEND -> {
				if (mc.crosshairTarget instanceof EntityHitResult hitResult
						&& hitResult.getEntity() instanceof PlayerEntity player) {
					String name = player.getName().getString();

					if (Managers.FRIEND.isFriend(player)) {
						if (Managers.FRIEND.remove(player))
							ChatUtils.sendMsg(
									player.hashCode(),
									Formatting.WHITE + name + Formatting.GRAY + " удален из друзей"
							);
					}
					else {
						if (Managers.FRIEND.add(name))
							ChatUtils.sendMsg(
									player.hashCode(),
									Formatting.WHITE + name + Formatting.GRAY + " добавлен в друзья"
							);
					}
				}
			}
			case PEARL -> useItem(Items.ENDER_PEARL);
			case FIREWORK -> useItem(Items.FIREWORK_ROCKET);
		}
	}

	private void useItem(Item item) {
		if (mc.player == null) {
			return;
		}

		FindItemResult fir = InvUtils.find(item);

		if (!fir.found()) {
			return;
		}

		boolean swap = fir.slot() != mc.player.getInventory().getSelectedSlot();
		Hand hand = fir.slot() == SlotUtils.OFFHAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
		int prevSlot = mc.player.getInventory().getSelectedSlot();

		if (swap) {
			if (fir.slot() <= 8) {
				InvUtils.setSlot(fir.slot(), false);
			}
			else {
				InvUtils.clickSlot(
						SlotUtils.getSlotID(fir.slot()),
						mc.player.getInventory().getSelectedSlot(),
						SlotActionType.SWAP
				);
			}
		}

		InvUtils.useItem(hand, mc.player.getYaw(), mc.player.getPitch());

		if (swap) {
			if (fir.slot() <= 8) {
				InvUtils.setSlot(prevSlot, false);
			}
			else {
				InvUtils.clickSlot(
						SlotUtils.getSlotID(fir.slot()),
						mc.player.getInventory().getSelectedSlot(),
						SlotActionType.SWAP
				);
			}
		}
	}

	public enum Mode {
		PEARL,
		FIREWORK,
		FRIEND
	}
}
