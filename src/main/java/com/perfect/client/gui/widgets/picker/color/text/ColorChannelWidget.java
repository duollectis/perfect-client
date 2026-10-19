package com.perfect.client.gui.widgets.picker.color.text;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.core.utils.text.filter.TextFilters;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.common.Widget;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorChannelWidget extends Widget {

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 3.5f);

	private final TextFieldWidget textField = addChild(
			new TextFieldWidget(this::set, TextFilters.POSITIVE_INT).font(font)
	);

	private final Supplier<Integer> valueGetter;
	private final Consumer<Integer> valueSetter;

	private String suffix = "";

	public ColorChannelWidget(Supplier<Integer> valueGetter, Consumer<Integer> valueSetter) {
		this.valueGetter = valueGetter;
		this.valueSetter = valueSetter;

		textField.setNoListenedText(() -> valueGetter.get() + suffix);
	}

	public ColorChannelWidget suffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	@Override
	public void onPreTick() {
		if (textField.isListening()) {
			container.get().disableEscape();
		}
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		String val = textField.isListening() ? textField.getString() : textField.getNoListenedVal();

		textField.setX(width / 2.0f - font.getTextWidth(val) / 2.0f);
		textField.setY(height / 2.0f);
		textField.setHeight(height);
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
