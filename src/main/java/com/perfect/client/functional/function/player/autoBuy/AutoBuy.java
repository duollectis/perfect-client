package com.perfect.client.functional.function.player.autoBuy;

import com.perfect.client.core.event.events.chat.MessageReceiveEvent;
import com.perfect.client.core.event.events.world.SoundEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.ItemPrice;
import com.perfect.client.core.utils.functions.delay.TickDelayUtils;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.core.utils.game.CommandUtils;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.core.utils.server.Servers;
import com.perfect.client.core.utils.server.auction.AuctionUtils;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.player.autoBuy.utils.SellItem;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.widgets.ClickGuiScreen;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.WindowWidget;
import com.perfect.client.gui.widgets.list.itemPrice.ItemPriceOpeningWidget;
import com.perfect.client.gui.widgets.overlay.toggle.OverlayToggleWidget;
import com.perfect.client.gui.widgets.windows.ListWindowWidget;
import lombok.Getter;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;

import java.util.Optional;

public class AutoBuy extends Function {

	private static final String AUCTION_IS_FULL_MESSAGE = "[☃] Освободите хранилище или уберите предметы с продажи";
	private static final String ITEM_WAS_SOLD_MESSAGE = "[☃] У Вас купили";

	private static final int PAY_SLOT = 10;
	private static final int PAY_ITEM_SLOT = 13;
	private static final int UPDATE_SLOT = 49;

	private final AutoBuyConfig config = new AutoBuyConfig(this);

	@Getter
	private final TickDelayUtils updateDelay = new TickDelayUtils(() -> config.getUpdateDelay().get());

	@Getter
	private final TickDelayUtils pickDelay = new TickDelayUtils(() -> config.getPickDelay().get());

	@Getter
	private final TickDelayUtils payDelay = new TickDelayUtils(() -> config.getPayDelay().get());

	@Getter
	private final TickDelayUtils sellDelay = new TickDelayUtils(() -> config.getSellDelay().get());

	private int anarchyIndex = 0;

	private final TimerUtils restartTimer = new TimerUtils();
	private final TimerUtils openAuctionTimer = new TimerUtils();
	private final TimerUtils changeAnarchyTimer = new TimerUtils();

	private final TimerUtils lagTimer = new TimerUtils();

	private boolean countLag;
	private int laggedTimes;
	private int notLaggedTimes;

	private boolean changingAnarchy;

	private boolean isSellBlocked;

	public AutoBuy() {
		super("auto-buy", "Auto Buy", Category.PLAYER);

		Managers.OVERLAY.addWindow(this::createWindow, this::shouldShowInterface);
		Managers.OVERLAY.addUpperOverlay(this::createButton, this::shouldShowInterface);
	}

	@Override
	public void onGameJoin() {
		countLag = true;
		laggedTimes = 0;
		notLaggedTimes = 0;
		restartTimer.reset();
		changingAnarchy = false;
		isSellBlocked = false;
	}

	@Override
	public void onMessageReceive(MessageReceiveEvent event) {
		String msg = StringUtils.textToString(event.getMessage());

		if (msg.equals(AUCTION_IS_FULL_MESSAGE)) {
			isSellBlocked = true;
		}
		else if (msg.startsWith(ITEM_WAS_SOLD_MESSAGE)) {
			isSellBlocked = false;
		}
	}

	@Override
	public void onSound(SoundEvent event) {
		if (event.isOf(SoundEvents.ENTITY_ENDER_EYE_LAUNCH)) {
			if (!countLag) {
				notLaggedTimes++;

				if (notLaggedTimes >= 2) {
					laggedTimes = 0;
				}
			}

			lagTimer.reset();
			countLag = false;
		}
	}

	private WindowWidget createWindow() {
		WindowWidget windowWidget = new ListWindowWidget(new ItemPriceOpeningWidget(config.getItems()));

		if (mc.currentScreen instanceof HandledScreen<?> screen) {
			windowWidget.setAppearX(() -> screen.x / 2.0f);
			windowWidget.setAppearY(() -> DrawConstants.getScreenHeight() / 2.0f);
		}

		return windowWidget;
	}

	private Widget createButton() {
		OverlayToggleWidget widget = new OverlayToggleWidget();

		widget.setNameSupplier(this::getName);
		widget.setToggledSupplier(() -> config.getActive().get());

		widget.setToggleAction(() -> config.getActive().set(!config.getActive().get()));

		widget.finishAnimation(config.getActive().get());

		return widget;
	}

	private boolean shouldShowInterface() {
		return isEnabled() && config.getInterfaceInAuction().get() && isInAuction();
	}

	private boolean isInAuction() {
		return AuctionUtils.isAuctionScreen() || AuctionUtils.isPayScreen();
	}

	@Override
	public void onPreTick() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (!config.getActive().get()) {
			return;
		}

		if (!Servers.FUN_TIME.getServer().isOnAnarchy()) {
			return;
		}

		sellDelay.tick();
		payDelay.tick();
		updateDelay.tick();
		pickDelay.tick();

		if (!restartTimer.passedSeconds(config.getRestartDelay().get())) {
			return;
		}

		if (!countLag && lagTimer.passedMillis(config.getLagTime().get())) {
			notLaggedTimes = 0;
			laggedTimes++;
			lagTimer.reset();
			countLag = true;
		}

		if (laggedTimes >= 5 && changeAnarchyTimer.passedMillis(1500)) {
			mc.setScreen(null);
			changeAnarchy();
		}

		if (changingAnarchy) {
			return;
		}

		ScreenHandler handler = mc.player.currentScreenHandler;

		if (config.getSell().get() && handleSell(handler)) {
			return;
		}

		if (AuctionUtils.isAuctionScreen()) {
			handleAuctionScreen(handler);
		}
		else if (AuctionUtils.isPayScreen()) {
			handlerPayScreen(handler);
		}
		else {
			updateDelay.resetTimer();
			pickDelay.resetTimer();
			payDelay.resetTimer();
			lagTimer.reset();

			if (!(mc.currentScreen instanceof ClickGuiScreen) && openAuctionTimer.passedMillis(600)) {
				CommandUtils.sendCommand("ah");
				openAuctionTimer.reset();
			}
		}
	}

	private void changeAnarchy() {
		int anarchy = getNextAnarchy();
		CommandUtils.sendCommand("an%s".formatted(anarchy));

		changeAnarchyTimer.reset();
		changingAnarchy = true;
	}

	private int getNextAnarchy() {
		int current = Servers.FUN_TIME.getServer().getAnarchy();

		incrementAnarchyIndex();

		int next = getAnarchyByIndex();

		if (next == current) {
			incrementAnarchyIndex();
			return getAnarchyByIndex();
		}
		else {
			return next;
		}
	}

	private int getAnarchyByIndex() {
		return config.getAnarchy().get().get(anarchyIndex).getValue();
	}

	private void incrementAnarchyIndex() {
		anarchyIndex = (anarchyIndex + 1) % config.getAnarchy().get().size();
	}

	private boolean handleSell(ScreenHandler handler) {
		if (mc.player == null) {
			return false;
		}
		if (isSellBlocked) {
			return false;
		}

		if (!sellDelay.isPassed()) {
			return true;
		}

		SellItem item = getSellItem(handler);

		if (item == null) {
			return false;
		}

		if (mc.currentScreen != null || Managers.NETWORK.isInScreenHandler()) {
			mc.player.closeHandledScreen();
			sellDelay.resetTimer();
			return true;
		}

		if (SlotUtils.getSlotID(Managers.NETWORK.getSlot()) != item.slot()) {
			sellDelay.resetTimer();
			InvUtils.clickSlot(item.slot(), Managers.NETWORK.getSlot(), SlotActionType.SWAP);
			return true;
		}

		CommandUtils.sendCommand("ah sell %s".formatted(item.price().getSellPrice()));
		sellDelay.resetTimer();
		return true;
	}

	private SellItem getSellItem(ScreenHandler handler) {
		if (mc.player == null) {
			return null;
		}

		int start = SlotUtils.getSlotID(SlotUtils.MAIN_START);
		int end = start + 4 * 9;

		SellItem holdingItem = getSellItem(
				mc.player.getMainHandStack(),
				SlotUtils.getSlotID(mc.player.getInventory().getSelectedSlot())
		);

		if (holdingItem != null) {
			return holdingItem;
		}

		for (int slot = start; slot < end; slot++) {
			ItemStack stack = handler.getSlot(slot).getStack();

			SellItem sellItem = getSellItem(stack, slot);

			if (sellItem != null) {
				return sellItem;
			}
		}
		return null;
	}

	private SellItem getSellItem(ItemStack stack, int slot) {
		if (stack.isEmpty()) {
			return null;
		}

		ItemIdentifier itemIdentifier = Managers.ITEM.identify(stack);
		ItemPrice itemPrice = config.getItems().get(itemIdentifier);

		if (itemPrice.isEnabled()
				&& stack.getCount() == itemPrice.getSellCount()
				&& itemPrice.getSellPrice() >= itemPrice.getBuyPrice()) {
			return new SellItem(stack, itemPrice, slot);
		}

		return null;
	}

	private void handlerPayScreen(ScreenHandler handler) {
		ItemStack stack = handler.getSlot(PAY_ITEM_SLOT).getStack();

		getPrice(stack).ifPresent(price -> {
			if (price != 0 && isSuitableStack(stack, price) && payDelay.isPassed()) {
				buyItem();
				sellDelay.resetTimer();
				payDelay.resetTimer();
				pickDelay.resetTimer();
				updateDelay.resetTimer();
			}
		});
	}

	private void handleAuctionScreen(ScreenHandler handler) {
		int buySlot = getBuySlot(handler);

		if (buySlot != -1) {
			if (pickDelay.isPassed()) {
				pickItem(buySlot);

				pickDelay.resetTimer();
				payDelay.resetTimer();
			}
		}
		else if (updateDelay.isPassed()) {
			updateAuction();

			updateDelay.resetTimer();
			pickDelay.resetTimer();
		}
	}

	private int getBuySlot(ScreenHandler handler) {
		int bestSlot = -1;
		int bestBenefit = 0;

		for (int slot = 0; slot < SlotUtils.getSlotID(SlotUtils.MAIN_START); slot++) {
			ItemStack stack = handler.getSlot(slot).getStack();

			if (stack.isEmpty()) {
				continue;
			}

			Optional<Integer> priceOptional = getPrice(stack);

			if (priceOptional.isEmpty()) {
				continue;
			}

			int price = priceOptional.get();

			ItemIdentifier itemIdentifier = Managers.ITEM.identify(stack);
			ItemPrice itemPrice = config.getItems().get(itemIdentifier);

			if (isSuitableStack(itemPrice, price)) {
				int benefit = itemPrice.getSellPrice() - price;

				if (bestSlot == -1 || benefit > bestBenefit) {
					bestSlot = slot;
					bestBenefit = benefit;
				}
			}
		}

		return bestSlot;
	}

	private Optional<Integer> getPrice(ItemStack stack) {
		if (stack.isEmpty() || stack.getCount() == 0) {
			return Optional.empty();
		}
		return AuctionUtils.getPrice(stack).map(price -> price / stack.getCount());
	}

	private boolean isSuitableStack(ItemStack stack, int price) {
		ItemIdentifier itemIdentifier = Managers.ITEM.identify(stack);
		ItemPrice itemPrice = config.getItems().get(itemIdentifier);

		return isSuitableStack(itemPrice, price);
	}

	private boolean isSuitableStack(ItemPrice itemPrice, int price) {
		if (price >= Servers.FUN_TIME.getServer().getBalance()) {
			return false;
		}
		return itemPrice.isEnabled() && price <= itemPrice.getBuyPrice();
	}

	private void updateAuction() {
		pickItem(UPDATE_SLOT);
	}

	private void buyItem() {
		pickItem(PAY_SLOT);
	}

	private void pickItem(int slotId) {
		InvUtils.clickSlotSilent(slotId, 0, SlotActionType.PICKUP);
	}
}
