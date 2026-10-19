package com.perfect.client.gui.widgets.settings.elements.list;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.impl.list.ItemPriceSetting;
import com.perfect.client.core.utils.ItemPrice;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.list.itemPrice.ItemPriceOpeningWidget;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.windows.ListWindowWidget;
import org.joml.Matrix3x2fStack;

import java.util.Map;

public class ItemPriceWidget extends SettingWidget<Map<ItemIdentifier, ItemPrice>, ItemPriceSetting> {

	private static final float HEIGHT = 4.4f;

	public ItemPriceWidget(ItemPriceSetting setting) {
		super(setting);
	}

	@Override
	public float getBorderedWidth() {
		return 0.0f;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		height = HEIGHT;
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY)) {
			openWindow();
		}
	}

	private void openWindow() {
		Widget widget = new ItemPriceOpeningWidget(setting);
		container.get().addWindow(new ListWindowWidget(widget));
	}
}
