package com.perfect.client.gui.widgets.settings.elements.slider.number;

import com.perfect.client.core.functional.setting.interfaces.settings.number.INumberSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.value.DoubleAnimation;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.settings.elements.slider.SliderWidget;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

public abstract class NumberSliderWidget<T extends Number> extends SliderWidget<T, T, INumberSetting<T>> {

	private final DoubleAnimation valueAnimation = new DoubleAnimation(0.2)
			.defined(false)
			.target(this::getSliderValue);

	private final TextFieldWidget textField = addChild(
			new TextFieldWidget(this::setValue, getStringFilter()).font(font)
	);

	private float textWidth;

	public NumberSliderWidget(INumberSetting<T> setting) {
		super(setting);
		dragging.setOnSwitched(setting::setWidgetDragging);
	}

	@Override
	public void onOpen() {
		valueAnimation.defined(false);
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		valueAnimation.updateAndGet();

		textField.color(Managers.COLOR.get(Managers.COLOR.MAIN));
		textField.setNoListenedText(getValueString() + getSuffix());

		if (textField.isListening()) {
			container.get().disableEscape();
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		String value = textField.isListening() ? textField.getString() : textField.getNoListenedVal();
		textWidth = font.getTextWidth(value);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		textField.setX(width - textWidth);
		textField.setY(TEXT_HEIGHT / 2f);
	}

	@Override
	public float getBorderedWidth() {
		return textWidth;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		super.onDraw(matrices);

		float sliderWidth = width * valueAnimation.getCurrent().floatValue();

		if (sliderWidth > 0) {
			drawFilledSlider(matrices, drawX, sliderWidth);
		}

		drawPoint(matrices, drawX + sliderWidth);

		if (dragging.isDragging()) {
			updateSliderValue();
		}
	}

	@Override
	public String getValueString() {
		return getValueString(setting.get().doubleValue());
	}

	@Override
	public boolean isListening() {
		return textField.isListening() && !textField.enterOnMouse();
	}

	@Override
	public void startListening() {
		textField.startListening(setting.get().toString());
	}

	private double getSliderValue() {
		double min = setting.getSliderMin().doubleValue();
		double max = setting.getSliderMax().doubleValue();

		return (Math.clamp(getValue(), min, max) - min) / (max - min);
	}

	private void updateSliderValue() {
		double value = MathUtils.interpolate(
				setting.getSliderMin().doubleValue(),
				setting.getSliderMax().doubleValue(),
				MathHelper.clamp((mouseX - drawX) / width, 0.0, 1.0)
		);

		setSliderValue(value);
	}

	protected double getValue() {
		T val = setting.get();
		return val == null ? 0 : val.doubleValue();
	}

	protected abstract void setSliderValue(double value);

	protected abstract void setValue(String value);

	protected abstract ITextFilter getStringFilter();

	protected abstract String getValueString(double value);
}
