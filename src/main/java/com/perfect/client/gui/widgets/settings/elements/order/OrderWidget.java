package com.perfect.client.gui.widgets.settings.elements.order;

import com.perfect.client.core.functional.setting.impl.enums.EnumOrderSetting;
import com.perfect.client.core.utils.enums.EnumOrder;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

public class OrderWidget<T extends Enum<T>> extends SettingWidget<EnumOrder<T>, EnumOrderSetting<T>> {

	private static final float HEIGHT = 4.4f;

	private final OrderFieldWidget<T> field;

	public OrderWidget(EnumOrderSetting<T> setting) {
		super(setting);

		field = addChild(new OrderFieldWidget<>(setting));

		for (T value : setting.getValues()) {
			field.addChild(new OrderElementWidget<>(field, value, setting));
		}
	}

	@Override
	public float getBorderedWidth() {
		return 0.0f;
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);
		field.setY(HEIGHT);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		field.setWidth(width);
		height = HEIGHT + field.getHeight();
	}
}
