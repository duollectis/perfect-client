package com.perfect.client.gui.widgets.picker.color;

import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.IMutableColor;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.picker.AbstractColorPickerWidget;
import com.perfect.client.gui.widgets.picker.color.text.ColorTextFidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class RGBAColorPickerWidget extends AbstractColorPickerWidget {

	private final ColorFieldWidget field = addChild(new ColorFieldWidget());

	private final VerticalListWidget sliderList;

	private final PickerSliderWidget hueSlider;
	private final PickerSliderWidget alphaSlider;

	public RGBAColorPickerWidget(IMutableColor mutableColor) {
		super(mutableColor);

		sliderList = addChild(new VerticalListWidget());

		hueSlider = sliderList.addChild(new PickerSliderWidget(true));
		alphaSlider = sliderList.addChild(new PickerSliderWidget(false));

		addChild(new ColorTextFidget(
				this::getSettingColor,
				this::apply,
				ColorTextFidget.Channels.RGBA
		));

		sliderList.expandWidth();

		setMutableColor(mutableColor);
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		if (field.isChanged() || hueSlider.isChanged() || alphaSlider.isChanged()) {
			field.setChanged(false);
			hueSlider.setChanged(false);
			alphaSlider.setChanged(false);

			Color color = Color.getHSBColor(hueSlider.getValue(), field.getSaturation(), field.getBrightness());
			setSettingColor(ColorUtils.applyAlpha(color, alphaSlider.getValue()));

			field.setColor(Color.getHSBColor(hueSlider.getValue(), 1.0f, 1.0f));
			field.setPointColor(color);

			alphaSlider.setColor(color);
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		field.setHeight(FIELD_HEIGHT);
		setIndent(VERTICAL_INDENT);

		sliderList.getChildren().forEach(widget -> widget.setHeight(SLIDER_LINE_HEIGHT));

		super.onSizeUpdate(matrices);

		setWidth(WIDTH);

		getChildren().forEach(child -> child.setWidth(width));
	}

	@Override
	protected void apply(Color color) {
		setSettingColor(color);

		float[] hsb = ColorUtils.toHsb(color);

		field.setPointColor(ColorUtils.applyAlpha(color, 1.0f));
		field.setColor(Color.getHSBColor(hsb[0], 1.0f, 1.0f));
		field.setSaturation(hsb[1]);
		field.setBrightness(hsb[2]);

		hueSlider.setValue(hsb[0]);
		alphaSlider.setValue(color.getAlpha() / 255.0f);

		alphaSlider.setColor(color);
	}
}
