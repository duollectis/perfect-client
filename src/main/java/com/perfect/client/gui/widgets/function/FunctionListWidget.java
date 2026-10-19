package com.perfect.client.gui.widgets.function;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.RenderPackets;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public class FunctionListWidget extends VerticalListWidget {

	private final Dragging dragging = new Dragging();
	private float draggingY;

	public FunctionListWidget(Category category) {
		scrollable();
		Managers.FUNCTION.getGroup(category).forEach(function -> addChild(new FunctionWidget(function)));
	}

	@Override
	public void onPreDraw() {
		dragging.checkNeedUnPress();

		if (dragging.isDragging()) {
			setPressScrolling(true);
			scroll(getScrollOffset());
			draggingY = mouseY;
		}

		super.onPreDraw();
	}

	@Override
	public void onPostChildrenDraw(Matrix3x2fStack matrices) {
		ScissorStack.pushScissor(matrices, drawX, drawY, width, height);
		Fonts.render(MONTSERRAT_MEDIUM, RenderPackets.BOTTOM_FONT_LAYER);
		ScissorStack.popScissor(matrices);
	}

	private float getScrollOffset() {
		return draggingY - mouseY;
	}

	private boolean canHover() {
		for (Widget widget : getChildren()) {
			if (isWidgetBlockHover(widget)) {
				return false;
			}
		}
		return true;
	}

	private boolean isWidgetBlockHover(Widget widget) {
		return widget instanceof FunctionWidget functionWidget && functionWidget.isSettingsHovered();
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (event.isPress()) {
			if (dragging.isDragging()) {
				dragging.unPress();
			}
			else if (isHovered() && canHover()) {
				dragging.press(event.getValue());
				draggingY = mouseY;
			}
		}
	}
}
