package com.perfect.client.gui.widgets.list.itemPrice;

import com.perfect.client.core.functional.setting.impl.list.ItemPriceSetting;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import com.perfect.client.gui.widgets.list.ItemStackListItemWidget;
import com.perfect.client.gui.widgets.list.common.ListWidget;
import org.joml.Matrix3x2fStack;

public class ItemPriceOpeningWidget extends HorizontalListWidget {

	private final PricePickerWidget picker;

	public ItemPriceOpeningWidget(ItemPriceSetting setting) {
		addChild(new ListWidget<>(setting, ItemStackListItemWidget::new, this::pickItem));
		picker = addChild(new PricePickerWidget(setting));
	}

	private void pickItem(ItemIdentifier item) {
		picker.setPickedItem(item);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(2.5f);
		picker.setWidth(70.0f);

		super.onSizeUpdate(matrices);

		picker.setHeight(height);
	}
}
