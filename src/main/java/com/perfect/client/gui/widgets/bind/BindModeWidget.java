package com.perfect.client.gui.widgets.bind;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.utils.Rounding;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.manager.system.ColorManager;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Consumer;

public class BindModeWidget extends Widget {

	private static final ColorManager.ColorPreset enabledBackgroundPreset = new ColorManager.ColorPreset(0.53f, 0.44f);

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 3.5f);

	private final Animation enabledAnimation = new Animation(Easing.IN_OUT_SINE, 400);

	private final Bind.Mode mode;

	private final Consumer<Bind.Mode> enableAction;

	public BindModeWidget(
			Bind.Mode mode,
			Consumer<Bind.Mode> enableAction,
			Rounding rounding
	) {
		this.mode = mode;
		this.rounding = rounding;
		this.enableAction = enableAction;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		float enabledAnim = enabledAnimation.get();

		Color outlineColor = MathUtils.interpolateColor(
				Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE),
				Managers.COLOR.get(Managers.COLOR.MAIN),
				enabledAnim
		);

		Color backgroundColor = MathUtils.interpolateColor(
				Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND),
				Managers.COLOR.get(enabledBackgroundPreset),
				enabledAnim
		);

		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .outlineColor(outlineColor)
		          .color(backgroundColor)
		          .draw(matrices);

		font.drawCentered(matrices, mode.getName(), drawX + width / 2.0f, drawY + height / 2.0f, Color.WHITE);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (event.isLeftOrRightPress() && isHovered()) {
			enableAction.accept(mode);
		}
	}

	public void setEnabled(boolean enabled) {
		enabledAnimation.changeDirection(enabled);
	}

	public void finishAnimation() {
		enabledAnimation.finish();
	}
}
