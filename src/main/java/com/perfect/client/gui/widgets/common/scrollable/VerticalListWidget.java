package com.perfect.client.gui.widgets.common.scrollable;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;

public class VerticalListWidget extends ScrollableWidget<VerticalListWidget> {

	private static final float SLIDER_WIDTH = 0.5f;
	private static final float SLIDER_INDENT = 0.65f;
	private static final float SLIDER_ROUND = SLIDER_WIDTH / 2.0f;

	private float _height;

	@Setter
	@Getter
	protected float topOffset;

	@Setter
	@Getter
	protected float indent;

	@Setter
	private boolean isPressScrolling;

	private boolean expandWidth;

	private final FloatAnimation sliderAlphaAnimation = new FloatAnimation(0.1).target(this::getSliderAlpha);

	public VerticalListWidget expandWidth() {
		expandWidth = true;
		return this;
	}

	@Override
	public void onReset() {
		isPressScrolling = false;
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		sliderAlphaAnimation.updateAndGet();
	}

	private float getSliderAlpha() {
		return isScrollSliderVisible() ? 0.5f : 0.0f;
	}

	private boolean isScrollSliderVisible() {
		if (!scrollable || !needScroll) {
			return false;
		}

		return isPressScrolling || (isHovered() && canScroll());
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		if (!expandWidth) {
			width = 0.0f;
		}

		height = topOffset;

		getChildren().forEach(widget -> {
			if (widget.shouldDraw() || !widget.isAnimationFinished()) {
				height += widget.getHeight() + indent * widget.getAnimationMultiplier();

				if (expandWidth) {
					widget.width(width);
				}
				else if (width < widget.getWidth()) {
					width = widget.getWidth();
				}
			}
		});

		if (height > 0.0f) {
			height -= indent;
		}

		needScroll = height > boundOffset;
		scrollBound = Math.max(height - boundOffset, 0.0f);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		_height = topOffset - getScroll();

		forEach(widget -> {
			widget.setY(_height);
			_height += widget.getHeight() + indent * widget.getAnimationMultiplier();
		});
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		if (sliderAlphaAnimation.getCurrent() > 0.0f) {
			float sliderHeight;
			float posY;

			if (scrollable && needScroll) {
				sliderHeight = boundOffset * (boundOffset / height);
				posY = (boundOffset - sliderHeight) * (getScroll() / scrollBound);
			}
			else {
				sliderHeight = boundOffset;
				posY = 0;
			}

			posY += topOffset;
			sliderHeight -= topOffset;

			ScissorStack.expand(matrices, 0.0f, 0.0f, 5.0f, 0.0f);

			Renderer2D.round()
			          .pos(drawX + width + SLIDER_INDENT, drawY + posY, SLIDER_WIDTH, sliderHeight)
			          .rounding(SLIDER_ROUND)
			          .alpha(sliderAlphaAnimation.getCurrent())
			          .color(Managers.COLOR.get(Managers.COLOR.MAIN))
			          .draw(matrices);

			ScissorStack.setLast(matrices);
		}
	}

	public void forEach(Consumer<Widget> widgetConsumer) {
		getChildren().forEach(widget -> {
			if (widget.shouldDraw() || !widget.isAnimationFinished()) {
				widgetConsumer.accept(widget);
			}
		});
	}
}
