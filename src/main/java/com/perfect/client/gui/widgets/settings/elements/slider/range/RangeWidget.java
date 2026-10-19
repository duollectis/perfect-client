package com.perfect.client.gui.widgets.settings.elements.slider.range;

import com.perfect.client.core.functional.setting.interfaces.settings.number.ISliderSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.value.DoubleAnimation;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.range.AbstractRange;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.settings.elements.slider.SliderWidget;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public abstract class RangeWidget<T extends Number, K extends AbstractRange<T>, F extends ISliderSetting<T, K>> extends SliderWidget<T, K, F> {

	private static final String separator = " - ";

	private final TextFieldWidget fromTextField = addChild(
			new TextFieldWidget(this::setFrom, getStringFilter()).font(font)
	);

	private final TextFieldWidget toTextField = addChild(
			new TextFieldWidget(this::setTo, getStringFilter()).font(font)
	);

	private final DoubleAnimation fromAnimation = new DoubleAnimation(0.2)
			.defined(false)
			.target(() -> getSliderValue(getFrom()));

	private final DoubleAnimation toAnimation = new DoubleAnimation(0.2)
			.defined(false)
			.target(() -> getSliderValue(getTo()));

	private float textCenterY;
	private float separatorX;

	private float textWidth;
	private float toWidth;
	private float separatorWidth;

	public RangeWidget(F setting) {
		super(setting);
	}

	@Override
	public void onOpen() {
		fromAnimation.defined(false);
		toAnimation.updateAndGet();
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		fromAnimation.updateAndGet();
		toAnimation.updateAndGet();

		fromTextField.color(Managers.COLOR.get(Managers.COLOR.MAIN));
		toTextField.color(Managers.COLOR.get(Managers.COLOR.MAIN));

		fromTextField.setNoListenedText(getValueString(getFrom()) + getSuffix());
		toTextField.setNoListenedText(getValueString(getTo()) + getSuffix());

		if (fromTextField.isListening() || toTextField.isListening()) {
			container.get().disableEscape();
		}
	}

	@Override
	public float getBorderedWidth() {
		return textWidth;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		String fromValue = fromTextField.isListening()
		                   ? fromTextField.getString()
		                   : fromTextField.getNoListenedVal();

		String toValue = toTextField.isListening()
		                 ? toTextField.getString()
		                 : toTextField.getNoListenedVal();

		float fromWidth = font.getTextWidth(fromValue);
		toWidth = font.getTextWidth(toValue);

		separatorWidth = font.getTextWidth(separator);

		textWidth = toWidth + separatorWidth + fromWidth;
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		separatorX = width - toWidth - separatorWidth;

		toTextField.setX(width - toWidth);
		fromTextField.setX(width - textWidth);

		textCenterY = TEXT_HEIGHT / 2.0f;

		fromTextField.setY(textCenterY);
		toTextField.setY(textCenterY);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		super.onDraw(matrices);

		float sliderWidthFrom = width * fromAnimation.getCurrent().floatValue();
		float sliderWidthTo = width * toAnimation.getCurrent().floatValue();

		if (sliderWidthFrom != sliderWidthTo) {
			float rangeWidth = sliderWidthTo - sliderWidthFrom;
			drawFilledSlider(matrices, drawX + sliderWidthFrom, rangeWidth);
		}

		drawPoint(matrices, drawX + sliderWidthFrom);
		drawPoint(matrices, drawX + sliderWidthTo);

		if (dragging.isDragging()) {
			updateSliderValue();
		}

		font.drawYCentered(
				matrices,
				separator,
				drawX + separatorX,
				drawY + textCenterY,
				Color.WHITE
		);
	}

	@Override
	public String getValueString() {
		String from = getValueString(setting.get().getFrom().doubleValue());
		String to = getValueString(setting.get().getTo().doubleValue());

		return from + " - " + to;
	}

	@Override
	public boolean isListening() {
		return isListening(fromTextField) || isListening(toTextField);
	}

	private boolean isListening(TextFieldWidget textField) {
		return textField.isListening() && !textField.enterOnMouse();
	}

	@Override
	public void startListening() {
		if (fromTextField.isHovered()) {
			fromTextField.startListening(getValueString(getFrom()));
		}
		else if (toTextField.isHovered()) {
			toTextField.startListening(getValueString(getTo()));
		}
	}

	private void updateSliderValue() {
		double value = MathUtils.interpolate(
				setting.getSliderMin().doubleValue(),
				setting.getSliderMax().doubleValue(),
				MathHelper.clamp((mouseX - drawX) / width, 0.0, 1.0)
		);

		Side side = getSide(value);

		if (side == Side.FROM) {
			setSliderFrom(value);
		}
		else {
			setSliderTo(value);
		}
	}

	private Side getSide(double value) {
		if (value < getFrom()) {
			return Side.FROM;
		}
		else if (value > getTo()) {
			return Side.TO;
		}

		double from = Math.clamp(getFrom(), getSliderMin(), getSliderMax());
		double to = Math.clamp(getTo(), getSliderMin(), getSliderMax());

		double fromDiff = Math.abs(from - value);
		double toDiff = Math.abs(to - value);

		if (fromDiff < toDiff) {
			return Side.FROM;
		}
		else {
			return Side.TO;
		}
	}

	private double getSliderValue(double val) {
		double min = getSliderMin();
		double max = getSliderMax();
		return (Math.clamp(val, min, max) - min) / (max - min);
	}

	private double getFrom() {
		return getDouble(setting.get().getFrom());
	}

	private double getTo() {
		return getDouble(setting.get().getTo());
	}

	private double getSliderMin() {
		return getDouble(setting.getSliderMin());
	}

	private double getSliderMax() {
		return getDouble(setting.getSliderMax());
	}

	private double getDouble(T val) {
		return val == null ? 0.0 : val.doubleValue();
	}

	protected abstract void setFrom(String value);
	protected abstract void setTo(String value);

	protected abstract void setSliderFrom(double val);
	protected abstract void setSliderTo(double val);

	protected abstract String getValueString(double value);

	private enum Side {
		FROM,
		TO
	}
}
