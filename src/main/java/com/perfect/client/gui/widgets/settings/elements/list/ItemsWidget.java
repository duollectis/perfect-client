package com.perfect.client.gui.widgets.settings.elements.list;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.interfaces.settings.IListSetting;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.gui.widgets.list.ItemStackListItemWidget;
import com.perfect.client.gui.widgets.list.common.ListWidget;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.windows.ListWindowWidget;
import org.joml.Matrix3x2fStack;

import java.util.Set;

public class ItemsWidget extends SettingWidget<Set<ItemIdentifier>, IListSetting<ItemIdentifier>> {

	private static final float HEIGHT = 4.4f;

	public ItemsWidget(IListSetting<ItemIdentifier> setting) {
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
		ListWidget<ItemIdentifier> listWidget = new ListWidget<>(setting, ItemStackListItemWidget::new);
		container.get().addWindow(new ListWindowWidget(listWidget));
	}
}
