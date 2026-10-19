package com.perfect.client.gui.widgets.settings;

import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.widgets.common.BorderedTextWidget;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.settings.elements.toggle.BoolWidget;
import com.perfect.client.gui.widgets.settings.elements.toggle.ToggleWidget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

public class SettingHolderWidget extends Widget implements IVisible {

	private final Animation hoveredAnimation = new Animation(Easing.IN_OUT_DOUBLE, 300, false).finish();
	private final Animation settingHideAnimation = new Animation(Easing.IN_OUT_DOUBLE, 250, true).finish();
	private final Animation widgetHideAnimation = new Animation(Easing.IN_OUT_DOUBLE, 250, true).finish();

	@Getter
	private final SettingWidget<?, ?> holdableWidget;

	private final BoolWidget toggleWidget;

	private final BorderedTextWidget text;

	@Setter
	@Getter
	private SettingGroup group;

	protected float hideAnim;
	protected float hoveredAnim;

	protected Color nameColor = Color.BLUE;

	public SettingHolderWidget(SettingWidget<?, ?> holdableWidget, Supplier<String> nameSupplier) {
		this.holdableWidget = addChild(holdableWidget);

		if (holdableWidget.setting.getToggleable() != null) {
			toggleWidget = addChild(new BoolWidget(holdableWidget.setting.getToggleable()));
		}
		else {
			toggleWidget = null;
		}

		text = addChild(new BorderedTextWidget(nameSupplier));
		text.setScrollCondition(() -> isHovered() || holdableWidget.isHovered());
	}

	@Override
	public String onGetDescription() {
		return holdableWidget.getSettingDescription();
	}

	@Override
	public void onOpen() {
		hoveredAnimation.finish();
		settingHideAnimation.finish();
		settingHideAnimation.finish();
	}

	@Override
	public void onPreDraw() {
		nameColor = MathUtils.interpolateColor(Color.WHITE, Managers.COLOR.get(Managers.COLOR.MAIN), hoveredAnim);
		holdableWidget.setCanHover(toggleWidget == null || !toggleWidget.isHovered());

		text.setColorSupplier(() -> nameColor);
	}

	@Override
	public void onAnimationTick() {
		hoveredAnimation.changeDirection(isHovered());
		hoveredAnim = hoveredAnimation.get();

		settingHideAnimation.changeDirection(shouldDraw());
		hideAnim = settingHideAnimation.get();

		widgetHideAnimation.changeDirection(toggleWidget == null || toggleWidget.setting.get());

		if (settingHideAnimation.isFinished(true)) {
			holdableWidget.setVisible(shouldDraw());
			holdableWidget.setActive(shouldHandleEvents());
		}

		if (toggleWidget != null && !toggleWidget.setting.get()) {
			holdableWidget.setActive(false);
		}

		if (widgetHideAnimation.isFinished(false)) {
			holdableWidget.setVisible(false);
		}

		opacity *= hideAnim;
		holdableWidget.setOpacity(opacity * widgetHideAnimation.get());
	}

	@Override
	public void onReset() {
		height = 0.0f;
		holdableWidget.setHeight(0.0f);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		height = holdableWidget.getHeight();

		if (toggleWidget != null) {
			height = Math.max(height, toggleWidget.getHeight());
		}

		height *= hideAnim;

		holdableWidget.setWidth(width);

		if (toggleWidget != null) {
			toggleWidget.setWidth(ToggleWidget.WIDTH);
		}

		float borderOffset = 1.5f;
		float borderEnd = width - holdableWidget.getBorderedWidth() - borderOffset;

		text.setWidth(borderEnd);
		text.setHeight(SettingWidget.TEXT_HEIGHT);

		float textX = 0.0f;

		if (toggleWidget != null) {
			float toggleWidgetOffset = 2.0f;
			textX += toggleWidget.getWidth() + toggleWidgetOffset;
		}

		text.setTextX(textX);
	}

	@Override
	public boolean isAnimationFinished() {
		return settingHideAnimation.isFinished(false);
	}

	@Override
	public float getAnimationMultiplier() {
		return hideAnim;
	}

	@Override
	public boolean isVisible() {
		return holdableWidget.getSetting().isVisible();
	}
}
