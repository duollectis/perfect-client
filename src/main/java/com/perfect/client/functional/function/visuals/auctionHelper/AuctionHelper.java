package com.perfect.client.functional.function.visuals.auctionHelper;

import com.perfect.client.core.event.events.render.RenderHandledScreenEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.server.auction.AuctionUtils;
import com.perfect.client.functional.function.visuals.auctionHelper.utils.AuctionHelperUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.awt.*;

public class AuctionHelper extends Function {

	private final AuctionHelperConfig config = new AuctionHelperConfig(this);

	private final AuctionHelperUtils helper = new AuctionHelperUtils(this, config);

	private PriceSlot slotWithMinimumPrice;
	private PriceSlot slotWithBestPrice;

	public AuctionHelper() {
		super("auction-helper", "Auction Helper", Category.VISUALS);
	}

	@Override
	public void onRenderSlot(RenderHandledScreenEvent event) {
		clearSlots();

		ScreenHandler handler = event.getHandler();

		if (AuctionUtils.isAuctionScreen()) {
			for (int i = 0; i < handler.slots.size() - 36; ++i) {
				Slot slot = handler.slots.get(i);
				ItemStack stack = slot.getStack();

				if (stack.isEmpty() || !helper.isValidItem(stack)) {
					continue;
				}

				AuctionUtils.getPrice(stack).ifPresent(price -> {
					if (slotWithMinimumPrice == null || slotWithMinimumPrice.price > price) {
						slotWithMinimumPrice = new PriceSlot(slot, price);
					}

					int bestPrice = price / stack.getCount();
					if (slotWithBestPrice == null || slotWithBestPrice.price > bestPrice) {
						slotWithBestPrice = new PriceSlot(slot, bestPrice);
					}
				});
			}

			if (slotWithMinimumPrice != null && slotWithBestPrice != null
					&& slotWithMinimumPrice.slot == slotWithBestPrice.slot) {
				slotWithBestPrice = null;
			}
		}

		drawPriceSlotIfPresent(event, slotWithMinimumPrice, config.getMinItemPriceColor().getColor());

		drawPriceSlotIfPresent(event, slotWithBestPrice, config.getBestItemPriceColor().getColor());
	}

	private void clearSlots() {
		slotWithMinimumPrice = null;
		slotWithBestPrice = null;
	}

	private void drawPriceSlotIfPresent(
			RenderHandledScreenEvent event, PriceSlot priceSlot, Color color
	) {
		if (priceSlot != null) {
			Slot slot = priceSlot.slot;

			int containerX = event.getContainerX();
			int containerY = event.getContainerY();

			event.getContext().fill(
					containerX + slot.x,
					containerY + slot.y,
					containerX + slot.x + 16,
					containerY + slot.y + 16,
					ColorUtils.getRgba(color)
			);
		}
	}

	public record PriceSlot(
			Slot slot,
			int price
	) {}
}
