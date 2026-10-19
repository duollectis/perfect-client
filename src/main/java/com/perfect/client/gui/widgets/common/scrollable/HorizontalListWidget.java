package com.perfect.client.gui.widgets.common.scrollable;

import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

@Setter
public class HorizontalListWidget extends ScrollableWidget<HorizontalListWidget> {

	@Getter
	protected float horizontalOffset;

	@Getter
	private float indent;

	private boolean expandWidth = false;

	private boolean noWidthCalc = false;

	@SuppressWarnings("unchecked")
	public <T extends HorizontalListWidget> T expandWidth() {
		expandWidth = true;
		return (T) this;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		height = 0;
		float _width = 0;

		if (expandWidth) {
			int size = 0;

			for (Widget widget : getChildren()) {
				if (widget.shouldDraw() || !widget.isAnimationFinished()) {
					size++;
				}
			}

			if (size > 0) {
				float w = (width - horizontalOffset * 2 - indent * (size - 1)) / size;

				getChildren().forEach(widget -> {
					if (widget.shouldDraw() || !widget.isAnimationFinished()) {
						widget.width(w);
					}

					if (height < widget.getHeight()) {
						height = widget.getHeight();
					}
				});
			}
		}
		else {
			_width = horizontalOffset;

			for (Widget widget : getChildren()) {
				if (widget.shouldDraw() || !widget.isAnimationFinished()) {
					_width += widget.getWidth() + indent * widget.getAnimationMultiplier();

					if (height < widget.getHeight()) {
						height = widget.getHeight();
					}
				}
			}

			if (_width > 0.0f) {
				_width -= indent;
			}
		}

		if (!noWidthCalc) {
			width = _width;
		}

		scrollBound = Math.max(_width - boundOffset, 0.0f);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		float _width = horizontalOffset - getScroll();

		for (Widget widget : getChildren()) {
			if (widget.shouldDraw() || !widget.isAnimationFinished()) {
				widget.setX(_width);
				_width += widget.getWidth() + indent * widget.getAnimationMultiplier();
			}
		}
	}
}
