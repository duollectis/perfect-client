package com.perfect.client.gui.widgets.picker.multi.panel.settings;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.core.utils.text.filter.TextFilters;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.common.Widget;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorValueWidget extends Widget {

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 3.5f);

	private static final float BOX_WIDTH = 16.0f;
	private static final float LEFT_X_OFFSET = 2.5f;

	private final Supplier<String> nameSupplier;
	private final Supplier<Integer> valueGetter;
	private final Consumer<Integer> valueSetter;

	private final TextFieldWidget textField;

	public ColorValueWidget(
			Supplier<String> nameSupplier,
			Supplier<Integer> valueGetter,
			Consumer<Integer> valueSetter
	) {
		this.nameSupplier = nameSupplier;
		this.valueGetter = valueGetter;
		this.valueSetter = valueSetter;

		textField = addChild(new TextFieldWidget(this::set, TextFilters.POSITIVE_INT).font(font));
		textField.setNoListenedText(() -> valueGetter.get() + "%");
	}

	@Override
	public void onPreTick() {
		if (textField.isListening()) {
			container.get().disableEscape();
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setRounding(0.75f);
		setHeight(8.25f);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		String val = textField.isListening() ? textField.getString() : textField.getNoListenedVal();

		textField.setY(height / 2.0f);
		textField.setX(width - BOX_WIDTH / 2.0f - font.getTextWidth(val) / 2.0f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .draw(matrices);

		Renderer2D.round()
		          .pos(drawX + width - BOX_WIDTH, drawY + 0.25f, 0.2f, height - 0.5f)
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .draw(matrices);

		font.drawYCentered(matrices, nameSupplier.get(), drawX + LEFT_X_OFFSET, drawY + height / 2.0f, Color.WHITE);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!event.isPress()) {
			return;
		}

		if (!isHovered()) {
			textField.stopListening();
			return;
		}

		if (!textField.isListening() || textField.enterOnMouse()) {
			if (event.isRight() || event.isLeft()) {
				textField.startListening(Integer.toString(valueGetter.get()));
			}
		}
	}

	private void set(String str) {
		StringUtils.parseInt(str).ifPresent(valueSetter);
	}
}
