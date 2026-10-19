package com.perfect.client.gui.widgets.settings.elements.toggle;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.OpacityStack;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.widgets.bind.BindEditWidget;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class BindButtonWidget extends Widget {

	public static final float SIZE = 6.8f;

	private final AbstractBindListener bindListener;

	private final Animation visibleAnimation = new Animation(Easing.IN_OUT_DOUBLE, 300, false).finish();
	private final Animation highlightAnimation = new Animation(Easing.IN_OUT_DOUBLE, 300, false).finish();

	private float highlightAnim;
	private float visibleAnim;

	@Setter
	private boolean visible;

	public BindButtonWidget(AbstractBindListener bindListener) {
		this.bindListener = bindListener;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		setWidth(SIZE);
		setHeight(SIZE);
	}

	@Override
	public void onAnimationTick() {
		highlightAnimation.changeDirection(isButtonHighlighted());
		highlightAnim = highlightAnimation.get();

		visibleAnimation.changeDirection(isButtonVisible());
		visibleAnim = visibleAnimation.get();
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		if (visibleAnim <= 0) {
			return;
		}

		Color color = MathUtils.interpolateColor(
				Color.WHITE,
				Managers.COLOR.get(Managers.COLOR.MAIN),
				highlightAnim
		);

		OpacityStack.push(visibleAnim);

		Renderer2D.SDF_SHADER.drawCentered(
				matrices,
				Textures.KEYBOARD,
				drawX + SIZE / 2f,
				drawY + SIZE / 2f,
				SIZE * visibleAnim,
				0.4f,
				color
		);

		OpacityStack.pop();
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY)) {
			container.get().addWindow(BindEditWidget.createWindow(bindListener, mouseX, mouseY));
		}
	}

	private boolean isButtonHighlighted() {
		return button.isHovered();
	}

	private boolean isButtonVisible() {
		return bindListener.getBind().isSet() || visible;
	}

	public void setCenterX(float x) {
		setX(x - width / 2f);
	}

	public void setCenterY(float y) {
		setY(y - height / 2f);
	}

	@Override
	public float getAnimationMultiplier() {
		return visibleAnim;
	}
}
