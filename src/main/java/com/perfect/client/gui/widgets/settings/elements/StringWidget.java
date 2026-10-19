package com.perfect.client.gui.widgets.settings.elements;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.interfaces.settings.IStringSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

public class StringWidget extends SettingWidget<String, IStringSetting> {

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 3.5f);

	private static final float TEXT_OFFSET = 1.6f;

	private static final float BOX_Y_OFFSET = 5.4f;
	private static final float BOX_HEIGHT = 6.6f;

	private final TextFieldWidget textField;

	public StringWidget(IStringSetting setting) {
		super(setting);

		textField = addChild(new TextFieldWidget(setting::set, setting.getFilter()).font(font));
	}

	@Override
	public float getBorderedWidth() {
		return 0.0f;
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		if (textField.isListening()) {
			container.get().disableEscape();
		}

		textField.setNoListenedText(setting.get());
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setRounding(1.5f);
		height = BOX_Y_OFFSET + BOX_HEIGHT;
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		textField.setX(TEXT_OFFSET);
		textField.setY(BOX_Y_OFFSET + BOX_HEIGHT / 2f);

		textField.setWidth(width);

		textField.color(Managers.COLOR.get(Managers.COLOR.MAIN));
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		float boxY = drawY + BOX_Y_OFFSET;

		Renderer2D.round()
		          .pos(drawX, boxY, width, BOX_HEIGHT)
		          .rounding(rounding)
		          .color(Managers.COLOR.get(Managers.COLOR.SETTING))
		          .draw(matrices);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (event.isNotPress()) {
			return;
		}

		if (textField.isListening()) {
			textField.enterOnMouse();
			return;
		}

		if (!isHovered()) {
			return;
		}

		//GuiUtil.setDraggingSlider(null);
		textField.startListening(setting.get());
	}
}
