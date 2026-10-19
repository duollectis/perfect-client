package com.perfect.client.gui.widgets.settings;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class SettingsGroupWidget extends VerticalListWidget {

	private static final float SEPARATOR_X_OFFSET = 20.0f;
	private static final float SEPARATOR_WIDTH = SettingsListWidget.WIDTH - SEPARATOR_X_OFFSET * 2.0f;

	private static final float SEPARATOR_Y_OFFSET = 7.0f;
	private static final float SEPARATOR_HEIGHT = 0.8f;

	private static final float SEPARATOR_ROUNDING = SEPARATOR_HEIGHT / 2.0f;

	public static final float Y_INDENT = 3.0f;

	@Getter
	private final SettingGroup group;

	private final Animation animation = new Animation(
			Easing.IN_OUT_DOUBLE,
			250
	).finish();

	private float anim;

	@Setter
	private boolean separate = true;

	public SettingsGroupWidget(SettingGroup group) {
		this.group = group;

		group.forEach(baseSetting -> {
			if (baseSetting instanceof Setting<?> setting) {
				SettingWidget<?, ?> widget = setting.createWidget();

				if (widget != null) {
					addChild(new SettingHolderWidget(widget, setting::getName));
				}
			}
			else {
				addChild(baseSetting.createWidget());
			}
		});

		color = new Color(
				MathUtils.random(100, 255),
				MathUtils.random(100, 255),
				MathUtils.random(100, 255)
		);
	}

	@Override
	public void onOpen() {
		animation.finish();
	}

	@Override
	public void onPreTick() {
		setIndent(Y_INDENT);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		getChildren().forEach(widget -> widget.setWidth(SettingWidget.WIDTH));

		super.onSizeUpdate(matrices);

		if (shouldDrawSeparator()) {
			height += (SEPARATOR_Y_OFFSET + Y_INDENT) * anim;
		}
	}

	@Override
	public void onAnimationTick() {
		animation.changeDirection(shouldDraw() && separate);
		anim = animation.get();

		for (Widget widget : getChildren()) {
			if (widget instanceof IVisible iVisible) {
				boolean visible = iVisible.isVisible();
				widget.setActive(visible);
				widget.setVisible(visible);
			}
			else {
				widget.setActive(true);
				widget.setVisible(true);
			}
		}
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		super.onDraw(matrices);

		if (shouldDrawSeparator()) {
			Renderer2D.round()
			          .pos(drawX + SEPARATOR_X_OFFSET, drawY + height - 4.8f)
			          .size(SEPARATOR_WIDTH, SEPARATOR_HEIGHT)
			          .rounding(SEPARATOR_ROUNDING)
			          .alpha(anim)
			          .color(Managers.COLOR.get(Managers.COLOR.SETTING))
			          .draw(matrices);
		}
	}

	@Override
	public boolean isAnimationFinished() {
		for (Widget widget : getChildren()) {
			if (!widget.isAnimationFinished()) {
				return false;
			}
		}

		return animation.isFinished(false);
	}

	@Override
	public void setAnimationDirection(boolean direction) {
		animation.changeDirection(direction);
	}

	private boolean shouldDrawSeparator() {
		return separate || !animation.isFinished(false);
	}
}
