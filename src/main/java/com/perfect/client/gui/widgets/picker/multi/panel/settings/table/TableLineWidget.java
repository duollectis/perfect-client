package com.perfect.client.gui.widgets.picker.multi.panel.settings.table;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.color.multiColor.MutableColor;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import com.perfect.client.gui.widgets.picker.multi.panel.settings.table.buttons.ColorTableButtons;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;

public class TableLineWidget extends HorizontalListWidget {

	private final ColorTableWidget table;
	private final ColorTableButtons buttons;

	public TableLineWidget(ISetting<MultiColor> setting, Consumer<MutableColor> selectColorConsumer) {
		table = addChild(new ColorTableWidget(setting, selectColorConsumer));
		buttons = addChild(new ColorTableButtons(setting, table::add, table::remove));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(1.0f);

		table.setHeight(height);
		buttons.setHeight(height);

		table.setWidth(width - buttons.getWidth() - getIndent());

		super.onSizeUpdate(matrices);
	}

	public void resetTable() {
		table.reset();
	}
}
