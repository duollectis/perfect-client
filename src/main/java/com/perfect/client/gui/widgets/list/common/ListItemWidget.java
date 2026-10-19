package com.perfect.client.gui.widgets.list.common;

import com.perfect.client.gui.widgets.common.button.ToggleButtonWidget;
import org.joml.Matrix3x2fStack;

public abstract class ListItemWidget extends ToggleButtonWidget {

	private final ListItem item;

	public ListItemWidget(ListItem item) {
		super(active -> {
			item.setEnabled(active);
			item.onToggle();
		});

		setRightClickAction(item::onPick);

		setButtonActive(item.isEnabled());

		this.item = item;
	}

	public String getName() {
		return item.getName();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		float size = 12.5f;

		setWidth(size);
		setHeight(size);
		setRounding(2.0f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		super.onDraw(matrices);
		drawContent(matrices);
	}

	protected abstract void drawContent(Matrix3x2fStack matrices);

}
