package com.perfect.client.gui.widgets.list.itemPrice;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

public class PriceIntWidget extends Widget {

	private static final float X_OFFSET = 3.5f;

	private static final float VALUE_BACKGROUND_HEIGHT = 6.25f;
	private static final float VALUE_BACKGROUND_ROUNDING = 0.75f;

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 3.5f);

	private final TextFieldWidget textField = addChild(
			new TextFieldWidget(this::parseValue, (state, c) -> Character.isDigit(c)).font(font)
	);

	private final Supplier<String> nameSupplier;
	private final String suffix;
	private final boolean separators;

	@Getter
	@Setter
	private boolean changed;

	@Setter
	@Getter
	private int value;

	@Setter
	private float valueBackgroundWidth;

	private float valueBackgroundX;
	private float valueBackgroundY;

	@Setter
	private float textOpacity = 1.0f;

	public PriceIntWidget(Supplier<String> nameSupplier, String suffix, boolean separators, int limit) {
		this.nameSupplier = nameSupplier;
		this.suffix = suffix;
		this.separators = separators;

		textField.limit(limit);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setRounding(2.0f);
		setHeight(12.5f);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		valueBackgroundX = width - X_OFFSET - valueBackgroundWidth;
		valueBackgroundY = height / 2.0f - VALUE_BACKGROUND_HEIGHT / 2.0f;

		String value = textField.isListening() ? textField.getString() : textField.getNoListenedVal();

		float valueTextX = valueBackgroundX + valueBackgroundWidth / 2.0f - font.getTextWidth(value) / 2.0f;

		textField.setY(height / 2.0f);
		textField.setX(valueTextX);
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		textField.setOpacity(textOpacity);
		textField.color(Managers.COLOR.get(Managers.COLOR.MAIN));
		textField.setNoListenedText(getValueString() + " " + suffix);

		if (textField.isListening()) {
			container.get().disableEscape();
		}
	}

	private String getValueString() {
		String str = Integer.toString(value);

		if (separators) {
			return StringUtils.separateNumber(str, ",");
		}
		else {
			return str;
		}
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

		font.drawYCentered(matrices, nameSupplier.get(), drawX + X_OFFSET, drawY + height / 2.0f, Color.WHITE);

		Renderer2D.round()
		          .pos(drawX + valueBackgroundX, drawY + valueBackgroundY)
		          .size(valueBackgroundWidth, VALUE_BACKGROUND_HEIGHT)
		          .rounding(VALUE_BACKGROUND_ROUNDING)
		          .color(Managers.COLOR.get(Managers.COLOR.ITEM_PRICE_INT_BACKGROUND))
		          .draw(matrices);
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
				textField.startListening(Integer.toString(value));
			}
		}
	}

	private void parseValue(String val) {
		int parsed = getValueOf(val);

		if (value != parsed) {
			changed = true;
		}

		value = parsed;
	}

	private int getValueOf(String val) {
		if (val.isEmpty()) {
			return 0;
		}
		return StringUtils.parseInt(val).orElse(value);
	}
}
