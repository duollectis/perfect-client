package com.perfect.client.gui.widgets.picker.multi;

import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.IMutableColor;
import com.perfect.client.gui.widgets.picker.AbstractColorPickerWidget;
import com.perfect.client.gui.widgets.picker.color.ColorFieldWidget;
import com.perfect.client.gui.widgets.picker.color.PickerSliderWidget;
import com.perfect.client.gui.widgets.picker.color.text.ColorTextFidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class RGBColorPickerWidget extends AbstractColorPickerWidget {

	private final ColorFieldWidget field = addChild(new ColorFieldWidget());

	private final PickerSliderWidget hueSlider;

	public RGBColorPickerWidget(IMutableColor mutableColor) {
		super(mutableColor);

		hueSlider = addChild(new PickerSliderWidget(true));

		addChild(new ColorTextFidget(
				this::getSettingColor,
				this::apply,
				ColorTextFidget.Channels.RGB
		));

		setMutableColor(mutableColor);
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		if (field.isChanged() || hueSlider.isChanged()) {
			field.setChanged(false);
			hueSlider.setChanged(false);

			Color color = Color.getHSBColor(hueSlider.getValue(), field.getSaturation(), field.getBrightness());
			setSettingColor(color);

			field.setColor(Color.getHSBColor(hueSlider.getValue(), 1.0f, 1.0f));
			field.setPointColor(color);
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		field.setHeight(FIELD_HEIGHT);

		setIndent(VERTICAL_INDENT);
		getChildren().forEach(child -> child.setWidth(width));

		hueSlider.setHeight(SLIDER_LINE_HEIGHT);

		super.onSizeUpdate(matrices);

		hueSlider.setWidth(width);
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
	}
}
