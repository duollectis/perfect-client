package com.perfect.client.gui.widgets.list.itemPrice;

import com.perfect.client.core.functional.setting.impl.list.ItemPriceSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.ItemPrice;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.list.ItemPreviewWidget;
import org.joml.Matrix3x2fStack;

public class PricePickerWidget extends VerticalListWidget {

	private final ItemPriceSetting setting;

	private final ItemPreviewWidget preview = addChild(new ItemPreviewWidget());

	private final PriceIntWidget buyPrice = addChild(new PriceIntWidget(
			() -> "Цена покупки", "$", true, 9
	));

	private final PriceIntWidget sellPrice = addChild(new PriceIntWidget(
			() -> "Цена продажи", "$", true, 9
	));

	private final PriceIntWidget sellCount = addChild(new PriceIntWidget(
			() -> "Количество для продажи", "шт.", false, 2
	));

	private final Animation textOpacityAnimation = new Animation(Easing.IN_OUT_SINE, 300).finish(true);

	private final Animation pickedPlaceholderAnimation = new Animation(Easing.IN_OUT_SINE, 300).finish(true);

	private float pickedPlaceholderAnim;

	private ItemIdentifier pickedItem;

	public PricePickerWidget(ItemPriceSetting setting) {
		this.setting = setting;
		expandWidth();
	}

	@Override
	public void onPreTick() {
		if (pickedItem != null) {
			preview.setTextSupplier(() -> pickedItem.getName());
			preview.setItemStack(pickedItem.getDefaultStack());

			if (buyPrice.isChanged() || sellPrice.isChanged() || sellCount.isChanged()) {
				ItemPrice price = setting.computeIfAbsent(pickedItem);

				price.setBuyPrice(buyPrice.getValue());
				price.setSellPrice(sellPrice.getValue());
				price.setSellCount(sellCount.getValue());

				price.replaceInvalidValues();

				setupPickers(price);
			}
		}

		boolean visible = pickedPlaceholderAnim < 0.1f;
		boolean active = pickedItem != null;

		getChildren().forEach(child -> {
			child.setVisible(visible);
			child.setActive(active);
		});
	}

	public void setPickedItem(ItemIdentifier item) {
		if (!item.equals(pickedItem)) {
			preview.resetScroll();

			textOpacityAnimation.reset();
			pickedItem = item;

			ItemPrice price = setting.get(item);
			setupPickers(price);
		}
	}

	private void setupPickers(ItemPrice price) {
		buyPrice.setValue(price.getBuyPrice());
		sellPrice.setValue(price.getSellPrice());
		sellCount.setValue(price.getSellCount());

		buyPrice.setChanged(false);
		sellPrice.setChanged(false);
		sellCount.setChanged(false);
	}

	@Override
	public void onAnimationTick() {
		pickedPlaceholderAnim = pickedPlaceholderAnimation.changeDirectionAndGet(pickedItem == null);

		float opacity = 1.0f - pickedPlaceholderAnim;
		getChildren().forEach(child -> child.setOpacity(opacity));

		float textOpacity = textOpacityAnimation.get();

		preview.setTextOpacity(textOpacity);
		buyPrice.setTextOpacity(textOpacity);
		sellPrice.setTextOpacity(textOpacity);
		sellCount.setTextOpacity(textOpacity);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(1.5f);

		float priceWidth = 30.0f;
		float countWidth = 13.75f;

		buyPrice.setValueBackgroundWidth(priceWidth);
		sellPrice.setValueBackgroundWidth(priceWidth);
		sellCount.setValueBackgroundWidth(countWidth);

		super.onSizeUpdate(matrices);

		setRounding(1.5f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		float offset = 0.1f;

		if (pickedPlaceholderAnim > 0.0f) {
			Renderer2D.round()
			          .pos(drawX - offset, drawY - offset)
			          .size(width + offset * 2.0f, height + offset * 2.0f)
			          .rounding(rounding)
			          .thickness(0.5f)
			          .alpha(pickedPlaceholderAnim)
			          .outlineAlpha(pickedPlaceholderAnim)
			          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
			          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
			          .draw(matrices);
		}
	}
}
