package com.perfect.client.gui.widgets.common.scrollable;

import com.perfect.client.core.event.events.input.mouse.MouseScrollEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Setter;

public class ScrollableWidget<T extends ScrollableWidget<T>> extends Widget {

	public static final float SCROLL_INTENSITY = 20f;

	@Setter
	protected float boundOffset;

	@Setter
	protected boolean scrollable;

	protected float scrollBound;

	protected float targetScrollOffset;

	private final FloatAnimation scrollAnimation = new FloatAnimation(0.15).target(() -> targetScrollOffset);

	protected boolean needScroll = true;

	public ScrollableWidget() {
		setInteractional(false);
	}

	@SuppressWarnings("unchecked")
	public T scrollable() {
		scrollable = true;
		return (T) this;
	}

	@Override
	public void onOpen() {
		scrollAnimation.defined(false);
	}

	@Override
	public void onPreDraw() {
		if (targetScrollOffset < 0) {
			targetScrollOffset = 0;
		}

		if (targetScrollOffset > scrollBound) {
			targetScrollOffset = scrollBound;
		}

		scrollAnimation.updateAndGet();
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (scrollable && isHovered() && canScroll()) {
			if (needScroll) {
				scroll(-(float) (event.getValue() * SCROLL_INTENSITY));
			}
			else {
				reset();
			}
		}
	}

	protected float getScroll() {
		return scrollAnimation.getCurrent();
	}

	protected void scroll(float value) {
		targetScrollOffset += value;
	}

	public void reset() {
		targetScrollOffset = 0;
	}

	protected boolean canScroll() {
		return !Managers.KEY_STATE.isLeftPressed();
	}
}
