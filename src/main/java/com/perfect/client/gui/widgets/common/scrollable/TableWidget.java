package com.perfect.client.gui.widgets.common.scrollable;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;

@Getter
@Setter
public class TableWidget extends ScrollableWidget<TableWidget> {

	private final Dragging scrollDragging = new Dragging();

	private boolean scrollHovered;

	private float scrollSliderY;
	private float targetScrollSliderHeight;

	private final FloatAnimation scrollSliderHeightAnimation = new FloatAnimation(0.15)
			.target(() -> targetScrollSliderHeight);

	private float scrollHitboxWidth;
	private float scrollSliderWidth;

	private float indent;

	private int lineLength;
	private boolean staticWidth;

	public TableWidget staticWidth() {
		staticWidth = true;
		return this;
	}

	public TableWidget lineLength(int lineLength) {
		this.lineLength = lineLength;
		return this;
	}

	@Override
	public void onClose() {
		scrollDragging.unPress();
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		scrollDragging.checkNeedUnPress();

		if (scrollDragging.isDragging()) {
			float y = mouseY - drawY;
			targetScrollOffset =
					((y - targetScrollSliderHeight / 2.0f) / (boundOffset - targetScrollSliderHeight)) * scrollBound;
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		if (!staticWidth) {
			float _width = 0.0f;
			float _height = 0.0f;

			float lineWidth = 0.0f;
			float lineHeight = 0.0f;

			int inLine = 0;

			float scroll = getScroll();

			for (Widget widget : getChildren()) {
				if (widget.shouldDraw() || !widget.isAnimationFinished()) {
					widget.setX(lineWidth);
					widget.setY(_height - scroll);

					float animatedIndent = indent * widget.getAnimationMultiplier();

					lineWidth += widget.getWidth() + animatedIndent;
					lineHeight = Math.max(widget.getHeight() + animatedIndent, lineHeight);

					inLine++;

					if (inLine == lineLength) {
						if (lineWidth > 0.0f) {
							lineWidth -= indent;
						}

						_width = Math.max(lineWidth, _width);
						_height += lineHeight;

						lineWidth = 0.0f;
						lineHeight = 0.0f;

						inLine = 0;
					}
				}
			}

			if (inLine != 0) {
				if (lineWidth > 0.0f) {
					lineWidth -= indent;
				}

				_width = Math.max(lineWidth, _width);
				_height += lineHeight;
			}

			if (_width > 0.0f) {
				_width += scrollHitboxWidth;
			}

			if (_height > 0.0f) {
				_height -= indent;
			}

			width = _width;
			height = _height;
		}
		else {
			float tableWidth = width;

			if (scrollable) {
				tableWidth -= scrollHitboxWidth;
			}

			float _height = 0.0f;

			float lineWidth = 0.0f;
			float lineHeight = 0.0f;

			float scroll = getScroll();

			for (Widget widget : getChildren()) {
				if (widget.shouldDraw() || !widget.isAnimationFinished()) {
					float animatedIndent = indent * widget.getAnimationMultiplier();

					if (lineWidth + widget.getWidth() > tableWidth && widget.getWidth() <= tableWidth) {
						_height += lineHeight;

						lineWidth = 0.0f;
						lineHeight = 0.0f;
					}

					widget.setX(lineWidth);
					widget.setY(_height - scroll);

					lineWidth += widget.getWidth() + animatedIndent;
					lineHeight = Math.max(widget.getHeight() + animatedIndent, lineHeight);
				}
			}

			_height += lineHeight;

			if (_height > 0.0f) {
				_height -= indent;
			}

			height = _height;
		}

		needScroll = height > boundOffset;
		scrollBound = Math.max(height - boundOffset, 0);

		targetScrollSliderHeight = boundOffset * (boundOffset / height);
		scrollSliderY = (boundOffset - targetScrollSliderHeight) * (getScroll() / scrollBound);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		if (scrollable && needScroll) {
			float scrollX = drawX + width - scrollSliderWidth;
			float scrollRounding = scrollSliderWidth / 2.0f;

			Renderer2D.round()
			          .pos(scrollX, drawY, scrollSliderWidth, height)
			          .rounding(scrollRounding)
			          .color(new Color(20, 21, 45))
			          .draw(matrices);

			Renderer2D.round()
			          .pos(scrollX, drawY + scrollSliderY)
			          .size(scrollSliderWidth, scrollSliderHeightAnimation.updateAndGet())
			          .rounding(scrollRounding)
			          .color(Managers.COLOR.get(Managers.COLOR.MAIN))
			          .draw(matrices);
		}
	}

	@Override
	public void onUpdateHover() {
		super.onUpdateHover();

		scrollHovered = scrollable && UiUtils.isHovered(
				mouseX,
				mouseY,
				drawX + width - scrollHitboxWidth,
				drawY,
				scrollHitboxWidth,
				height
		);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!event.isPress()) {
			return;
		}

		if (event.isLeft() && isHovered() && scrollHovered) {
			scrollDragging.press(event.getValue());
		}
		else {
			scrollDragging.unPress();
		}
	}
}
