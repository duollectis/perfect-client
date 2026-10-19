package com.perfect.client.gui.widgets.settings.elements.slider;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.interfaces.settings.number.ISliderSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public abstract class SliderWidget<T extends Number, K, F extends ISliderSetting<T, K>> extends SettingWidget<K, F> {

	private static final float SLIDER_Y_OFFSET = 6.0f;
	private static final float SLIDER_HEIGHT = 1.3f;
	private static final float SLIDER_ROUNDING = SLIDER_HEIGHT / 2.0f;

	private static final float POINT_SIZE = 2.4f;
	private static final float POINT_ROUNDING = POINT_SIZE / 2.0f;

	private static final float HEIGHT = 9.0f;

	protected final static SizedFont font = new SizedFont(() -> MONTSERRAT_MEDIUM, 3.5f);

	protected final Dragging dragging = new Dragging();

	private float sliderY;

	public SliderWidget(F setting) {
		super(setting);
	}

	@Override
	public void onClose() {
		dragging.unPress();
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		if (dragging.checkNeedUnPress()) {
			UiUtils.releaseWidget(this);
		}
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);
		sliderY = drawY + SLIDER_Y_OFFSET;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(HEIGHT);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, sliderY, width, SLIDER_HEIGHT)
		          .rounding(SLIDER_ROUNDING)
		          .color(Managers.COLOR.get(Managers.COLOR.SETTING))
		          .draw(matrices);
	}

	protected void drawFilledSlider(Matrix3x2fStack matrices, float x, float width) {
		Renderer2D.round()
		          .pos(x, sliderY, width, SLIDER_HEIGHT)
		          .rounding(SLIDER_ROUNDING)
		          .color(Managers.COLOR.get(Managers.COLOR.MAIN))
		          .draw(matrices);
	}

	protected void drawPoint(Matrix3x2fStack matrices, float x) {
		float pointX = x - POINT_ROUNDING;
		float pointY = sliderY + SLIDER_HEIGHT / 2.0f - POINT_SIZE / 2.0f;

		ScissorStack.expand(matrices, 3.0f, 0.0f, 3.0f, 0.0f);

		Renderer2D.round()
		          .pos(pointX, pointY, POINT_SIZE, POINT_SIZE)
		          .rounding(POINT_ROUNDING)
		          .color(Managers.COLOR.get(Managers.COLOR.MAIN))
		          .draw(matrices);

		ScissorStack.setLast(matrices);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (event.isNotPress()) {
			return;
		}

		if (isListening()) {
			return;
		}

		if (!isHovered()) {
			return;
		}

		if (event.isLeft()) {
			UiUtils.setDraggingWidget(this);
			dragging.press(event.getValue());
		}
		else if (event.isRight()) {
			dragging.unPress();

			UiUtils.releaseWidget(this);
			startListening();
		}
	}

	@Override
	protected void onUpdateHover() {
		super.onUpdateHover();
		button.setHovered(isHovered() || dragging.isDragging());
	}

	protected abstract boolean isListening();
	protected abstract void startListening();

	protected String getSuffix() {
		return setting.getSuffix();
	}

	protected abstract ITextFilter getStringFilter();

	protected abstract String getValueString();
}
