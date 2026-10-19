package com.perfect.client.gui.widgets.settings.elements.enums;

import com.perfect.client.core.functional.setting.interfaces.settings.enums.IEnumSetting;
import com.perfect.client.gui.widgets.common.scrollable.TableWidget;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

public abstract class AbstractEnumWidget<T, E extends Enum<?>, K extends IEnumSetting<T, E>> extends SettingWidget<T, K> {

	public static final float LIST_Y = 5.5f;

	private final TableWidget table = addChild(new TableWidget().staticWidth());

	public AbstractEnumWidget(K setting) {
		super(setting);

		for (E value : setting.getValues()) {
			table.addChild(new EnumButtonWidget<>(value, setting, () -> handleClick(value)));
		}
	}

	@Override
	public float getBorderedWidth() {
		return 0.0f;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		table.setY(LIST_Y);
		table.setWidth(width);
		table.setIndent(1.0f);

		height = table.getHeight() + LIST_Y;
	}

	protected abstract void handleClick(E value);

}
