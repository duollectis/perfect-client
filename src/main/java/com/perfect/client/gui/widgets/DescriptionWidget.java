package com.perfect.client.gui.widgets;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.OpacityStack;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.gui.widgets.common.Widget;
import org.joml.Matrix3x2fStack;

import java.awt.*;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;
import static com.perfect.client.manager.system.FunctionManager.testFunction;

public class DescriptionWidget extends Widget {

	private static final float X_DEPTH = 3.5f;

	private static final SizedFont font = new SizedFont(() -> MONTSERRAT_MEDIUM, 3.6f);

	private final Animation visibleAnimation = new Animation(Easing.IN_OUT_SINE, 150);
	private final Animation opacityAnimation = new Animation(Easing.IN_OUT_SINE, 150);

	private final FloatAnimation widthAnimation = new FloatAnimation(0.3).target(() -> width);

	private float drawWidth;

	private String prevDrawText;
	private String prevText;
	private String text = null;

	private float visibleAnim;
	private float opacityAnim;

	public void setText(String text) {
		if (!isEmpty(this.text)) {
			prevText = this.text;
		}
		this.text = text;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(9.4f);
		setRounding(2.0f);

		if (!isEmpty(text)) {
			width = font.getTextWidth(text) + X_DEPTH * 2.0f;
		}
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		drawX = drawX - drawWidth / 2.0f;
	}

	@Override
	public void onAnimationTick() {
		drawWidth = widthAnimation.updateAndGet();

		if (!isEmpty(text) && !text.equals(prevDrawText)) {
			opacityAnimation.reset();
			prevDrawText = text;
		}

		visibleAnimation.setTime(testFunction.time.get());

		visibleAnim = visibleAnimation.changeDirectionAndGet(!isEmpty(text));
		opacityAnim = opacityAnimation.get();
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		String text = getText();

		if (text != null && visibleAnim > 0.0f) {
			OpacityStack.push(visibleAnim);

			Renderer2D.blurredRound()
			          .pos(drawX, drawY, drawWidth, height)
			          .rounding(rounding)
			          .thickness(0.6f)
			          .blur(2.0f)
			          .alpha(0.9f)
			          .outlineColor(Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE))
			          .color(Managers.COLOR.get(Managers.COLOR.BACKGROUND))
			          .draw(matrices);

			float centerX = drawX + drawWidth / 2.0f;
			float centerY = drawY + height / 2.0f;

			OpacityStack.push(opacityAnim);

			ScissorStack.pushScissor(matrices, drawX, drawY, drawWidth, height);

			font.drawCentered(matrices, text, centerX, centerY, Color.WHITE);

			ScissorStack.popScissor(matrices);

			OpacityStack.pop();

			OpacityStack.pop();
		}
	}

	private String getText() {
		if (isEmpty(text)) {
			return prevText;
		}
		return text;
	}

	private boolean isEmpty(String text) {
		return text == null || text.isBlank();
	}
}
