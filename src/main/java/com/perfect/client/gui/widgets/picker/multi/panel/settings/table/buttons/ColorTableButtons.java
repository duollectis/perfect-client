package com.perfect.client.gui.widgets.picker.multi.panel.settings.table.buttons;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.color.multiColor.MutableColor;
import com.perfect.client.gui.widgets.common.TextureWidget;
import com.perfect.client.gui.widgets.common.button.ButtonWidget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;

public class ColorTableButtons extends VerticalListWidget {

	private final ISetting<MultiColor> setting;

	private final ButtonWidget addButton;
	private final ButtonWidget removeButton;


	public ColorTableButtons(
			ISetting<MultiColor> setting,
			Consumer<MutableColor> addConsumer,
			Runnable removeAction
	) {
		this.setting = setting;

		addButton = setupButton(addChild(new ButtonWidget(() -> add(addConsumer))));
		removeButton = setupButton(addChild(new ButtonWidget(removeAction)));

		addButton.add(new TextureWidget(() -> Textures.PLUS.getGlId()).size(2.8f));
		removeButton.add(new TextureWidget(() -> Textures.MINUS.getGlId()).size(2.8f));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(6.0f);

		addButton.setWidth(width);
		removeButton.setWidth(width);

		setIndent(1.0f);

		float buttonHeight = height / 2.0f - indent / 2.0f;

		addButton.setHeight(buttonHeight);
		removeButton.setHeight(buttonHeight);

		float rounding = 0.75f;

		addButton.setRounding(rounding);
		removeButton.setRounding(rounding);

		super.onSizeUpdate(matrices);
	}

	private ButtonWidget setupButton(ButtonWidget widget) {
		widget.setOutlineColorSupplier(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE));
		widget.backgroundColor(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND));
		widget.setOutlineSize(0.5f);
		return widget;
	}

	private void add(Consumer<MutableColor> addConsumer) {
		if (setting.get().getColorList().size() >= MultiColor.LIMIT) {
			return;
		}

		MutableColor color = new MutableColor();
		setting.get().getColorList().add(color);

		addConsumer.accept(color);
	}
}
