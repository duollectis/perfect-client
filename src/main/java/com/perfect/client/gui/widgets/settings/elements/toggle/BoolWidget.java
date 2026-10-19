package com.perfect.client.gui.widgets.settings.elements.toggle;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.interfaces.settings.IBoolSetting;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

public class BoolWidget extends SettingWidget<Boolean, IBoolSetting> {

	private static final float HEIGHT = 4.4f;
	private static final float BIND_OFFSET = 4.1f;

	private final ToggleWidget toggle = addChild(new ToggleWidget(setting::get));

	private final BindButtonWidget bindButton;

	public BoolWidget(IBoolSetting setting) {
		super(setting);

		if (setting.getBindListener() == null) {
			bindButton = null;
		}
		else {
			bindButton = addChild(new BindButtonWidget(setting.getBindListener()));
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(HEIGHT);


	}

	@Override
	public void onAnimationTick() {
		super.onAnimationTick();

		if (bindButton != null) {
			bindButton.setVisible(isHovered());
		}
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		toggle.setX(width - toggle.getWidth());

		if (bindButton != null) {
			bindButton.setCenterX(width - toggle.getWidth() - BIND_OFFSET);
			bindButton.setCenterY(height / 2f);
		}
	}

	@Override
	protected void onUpdateHover() {
		super.onUpdateHover();
	}

	@Override
	public float getBorderedWidth() {
		return toggle.getWidth() + BindButtonWidget.SIZE * bindButton.getAnimationMultiplier();
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY) && (bindButton == null || !bindButton.isHovered())) {
			setting.set(!setting.get());
		}
	}
}
