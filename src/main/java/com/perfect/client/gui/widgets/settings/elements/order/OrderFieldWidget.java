package com.perfect.client.gui.widgets.settings.elements.order;

import com.perfect.client.core.functional.setting.impl.enums.EnumOrderSetting;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.widgets.common.Widget;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.Comparator;

public class OrderFieldWidget<T extends Enum<T>> extends Widget {

	private static final float Y_OFFSET = 2.5f;

	private final EnumOrderSetting<T> setting;

	private final Dragging dragging = new Dragging();

	private OrderElementWidget<T> draggingElement = null;

	private float dragY;
	private float dragElementY;

	public OrderFieldWidget(EnumOrderSetting<T> setting) {
		this.setting = setting;
	}

	public void setCurrentDragging(OrderElementWidget<T> element, int button) {
		dragging.press(button);

		dragY = mouseY;
		dragElementY = element.getY();

		draggingElement = element;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onPreTick() {
		if (dragging.checkNeedUnPress()) {
			draggingElement = null;
		}

		getChildren().sort(Comparator.comparing(widget -> {
			if (widget instanceof OrderElementWidget<?> element) {
				return setting.getOrder((T) element.getValue());
			}
			return 0;
		}));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		float _height = Y_OFFSET;

		for (Widget widget : getChildren()) {
			if (widget instanceof OrderElementWidget<?> child) {
				if (child.shouldDraw() || !child.isAnimationFinished()) {
					child.setWidth(width);
					child.setTargetY(_height);
					_height += child.getHeight();
				}
			}
		}

		if (draggingElement != null) {
			draggingElement.setY(Math.clamp(
					dragElementY + (mouseY - dragY),
					Y_OFFSET,
					_height - draggingElement.getHeight()
			));

			setting.move(
					draggingElement.getValue(),
					getYIndex(draggingElement.getY() - Y_OFFSET, draggingElement.getHeight())
			);
		}

		setHeight(_height + Y_OFFSET);
		setRounding(2.25f);
	}

	private int getYIndex(float y, float lineHeight) {
		return (int) Math.floor((y + lineHeight / 2.0f) / lineHeight);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .color(new Color(13, 15, 27))
		          .draw(matrices);
	}
}
