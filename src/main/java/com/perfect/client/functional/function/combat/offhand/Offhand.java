package com.perfect.client.functional.function.combat.offhand;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.event.events.block.InteractBlockEvent;
import com.perfect.client.core.event.events.entity.EntityEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.collection.UpdatingCollection;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.combat.offhand.helpers.InventoryHelper;
import com.perfect.client.functional.function.combat.offhand.helpers.SafetyHelper;
import lombok.Getter;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.Map;

import static com.perfect.client.manager.system.FunctionManager.suicide;


public class Offhand extends Function {

	@Getter
	private final OffhandConfig config = new OffhandConfig(this);

	@Getter
	private final SafetyHelper safetyHelper = new SafetyHelper(this, config);

	@Getter
	private final InventoryHelper inventoryHelper = new InventoryHelper(this, config);

	public final TimerUtils actionTimer = new TimerUtils();
	public final TimerUtils popTimer = new TimerUtils();
	public final UpdatingCollection<Entity> lastCrystals = new UpdatingCollection<>();

	private boolean working = false;

	private final Map<Item, Integer> prevSlots = new HashMap<>();

	public Offhand() {
		super("offhand", "Offhand", Category.COMBAT, "Auto Totem");
	}

	@Override
	public void onEnable() {
		working = false;
	}

	@Override
	public void onDisable() {
		lastCrystals.clear();
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (mc.player == null) {
			return;
		}

		if (event.getPacket() instanceof EntityStatusS2CPacket packet) {
			onEntityStatusPacket(packet);
		}
	}

	private void onEntityStatusPacket(EntityStatusS2CPacket packet) {
		if (mc.player == null) {
			return;
		}

		if (PacketUtils.isTotemPop(packet) && mc.player.equals(packet.getEntity(mc.world))) {
			popTimer.reset();
		}
	}

	@Override
	public void onPostTick() {
		lastCrystals.update(100);
		action();
	}

//	@Override
//	public void onClickSlot() {
//		action();
//	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		if (event.isCombatBreak() && event.isInRange(10d)) {
			action();
		}
	}

	@Override
	public void onEntitySpawn(EntityEvent event) {
		if (isDangerousEntity(event.getEntity())) {
			action();
		}
	}

//	@Override
//	public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateEvent event) {
//		if (event.getSlot() == 45) {
//			action();
//		}
//	}

	@Override
	public void onEntityRemove(EntityEvent event) {
		if (isDangerousEntity(event.getEntity())) {
			lastCrystals.put(event.getEntity());
		}
	}

	@Override
	public void onInteractBlock(InteractBlockEvent event) {
		if (mc.player == null) {
			return;
		}

		if (event.getHand() == Hand.OFF_HAND
				&& config.isGAppleBindPressed()
				&& mc.player.getOffHandStack().isOf(Items.END_CRYSTAL)
		) {
			event.cancel();
		}
	}

	private void action() {
		if (!working) {
			working = true;
			actionIternal();
			working = false;
		}
	}

	
	private void actionIternal() {
		if (mc.player == null
				|| mc.world == null
				|| mc.interactionManager == null
				|| mc.player.currentScreenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler
		) {
			return;
		}

		if (suicide.isEnabled() && !mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
			return;
		}

		if (actionTimer.passedMillis(config.getDelay().get())) {
			Item item = inventoryHelper.getItem();

			if (mc.player.getOffHandStack().isOf(item)) {
				return;
			}

			FindItemResult fir = InvUtils.findFromEnd(stack -> inventoryHelper.isGoodStack(stack, item));
			ItemStack cursorStack = mc.player.currentScreenHandler.getCursorStack();

			if (!fir.found() && !cursorStack.isOf(item)) {
				return;
			}

			boolean canClickOffhand = mc.player.currentScreenHandler instanceof PlayerScreenHandler;

			actionTimer.reset();

			if (cursorStack.isOf(item) && canClickOffhand) {
				Integer prevSlot = prevSlots.remove(mc.player.getOffHandStack().getItem());

				InvUtils.clickSlot(45, 0, SlotActionType.PICKUP);

				if (prevSlot != null) {
					InvUtils.clickSlot(SlotUtils.getSlotID(prevSlot), 0, SlotActionType.PICKUP);
				}
			}
			else {
				int itemSlot = fir.slot();

				Integer prevSlot = prevSlots.remove(mc.player.getOffHandStack().getItem());

				if (prevSlot != null) {
					InvUtils.clickSlot(SlotUtils.getSlotID(prevSlot), 40, SlotActionType.SWAP);
				}

				prevSlots.put(item, itemSlot);

				InvUtils.clickSlot(SlotUtils.getSlotID(itemSlot), 40, SlotActionType.SWAP);
			}
		}
	}

	public boolean isDangerousEntity(Entity entity) {
		return entity instanceof EndCrystalEntity
				|| entity instanceof TntEntity
				|| entity instanceof CreeperEntity
				|| entity instanceof TntMinecartEntity;
	}

	@Getter
	public enum OffhandItem {
		TOTEM(Items.TOTEM_OF_UNDYING),
		CRYSTAL(Items.END_CRYSTAL),
		GAPPLE(Items.GOLDEN_APPLE),
		EGAPPLE(Items.ENCHANTED_GOLDEN_APPLE);

		private final Item item;

		OffhandItem(Item item) {
			this.item = item;
		}
	}
}