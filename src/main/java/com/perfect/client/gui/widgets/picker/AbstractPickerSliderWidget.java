package com.perfect.client.gui.widgets.picker;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

public abstract class AbstractPickerSliderWidget extends Widget {

	@Getter
	@Setter
	private float value;

	protected final FloatAnimation valueAnimation = new FloatAnimation(0.2).target(() -> Math.clamp(value, 0f, 1f));

	@Getter
	@Setter
	private boolean changed;
	private final Dragging dragging = new Dragging();

	private final Animation appearAnimation = new Animation(Easing.IN_OUT_DOUBLE, 250);

	private float appearAnim;

	protected float centerY;

	@Override
	public void onClose() {
		dragging.unPress();
		UiUtils.releaseWidget(this);
	}

	@Override
	public void onPreTick() {
		if (dragging.checkNeedUnPress()) {
			UiUtils.releaseWidget(this);
		}
	}

	@Override
	public void onAnimationTick() {
		appearAnimation.changeDirection(shouldDraw());
		appearAnim = appearAnimation.get();

		opacity *= appearAnim;
	}

	@Override
	public float getAnimationMultiplier() {
		return appearAnim;
	}

	@Override
	public boolean isAnimationFinished() {
		return !(appearAnim > 0);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);
		centerY = drawY + height / 2f;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		height *= appearAnim;
	}

	@Override
	public void onPreDraw() {
		valueAnimation.updateAndGet();

		if (dragging.isDragging()) {
			float dragPos = mouseX - getDraggableX();
			float val = Math.clamp(dragPos / getDraggableWidth(), 0, 1);

			if (value != val) {
				changed = true;
				value = val;
			}
		}
	}

	protected abstract float getDraggableX();
	protected abstract float getDraggableWidth();

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (isHovered() && event.isPress()) {
			dragging.press(event.getValue());
			UiUtils.setDraggingWidget(this);
		}
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		draw(matrices);
	}

	protected abstract void draw(Matrix3x2fStack matrices);
}
