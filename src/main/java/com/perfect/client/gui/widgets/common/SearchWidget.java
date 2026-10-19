package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.OpacityStack;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.core.utils.text.filter.TextFilters;
import com.perfect.client.gui.utils.UiUtils;
import lombok.Setter;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Setter
public class SearchWidget extends Widget {

	private final TextFieldWidget textField = addChild(new TextFieldWidget(TextFilters.LETTERS));

	private final FloatAnimation listeningAnimation = new FloatAnimation(0.1)
			.target(() -> isListening() || !isSearching() ? 1.0f : 0.0f);

	private final FloatAnimation activeAnimation = new FloatAnimation(0.1)
			.target(() -> isSearching() ? 1.0f : 0.0f);

	private boolean hidden;

	private Supplier<Color> outlineColor = () -> Managers.COLOR.get(Managers.COLOR.SETTING_BACKGROUND);
	private Supplier<Color> backgroundColor = () -> Managers.COLOR.get(Managers.COLOR.SETTING_BACKGROUND);

	private Supplier<String> noListenedText = () -> StringUtils.EMPTY;

	public void setEnterAction(Consumer<String> enterAction) {
		textField.setEnterAction(enterAction);
	}

	public void setTextFilter(ITextFilter filter) {
		textField.setFilter(filter);
	}

	public void clear() {
		textField.stopListening();
		textField.clear();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		textField.width(getWidth());
		textField.height(getHeight());
	}

	@Override
	public void onOpen() {
		activeAnimation.defined(false);
		listeningAnimation.defined(false);
	}

	@Override
	public void onPreTick() {
		if (isSearching()) {
			textField.setNoListenedText(textField.getString());
		}
		else {
			textField.setNoListenedText(noListenedText);
		}
	}

	@Override
	public void onAnimationTick() {
		if (hidden) {
			activeAnimation.updateAndGet();
		}
		else {
			activeAnimation.set(1.0f);
		}

		listeningAnimation.updateAndGet();
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		textField.setX(height);
		textField.setY(height / 2.0f);

		setY(getY() + height * (1.0f - activeAnimation.getCurrent()));

		super.onPositionUpdate(matrices);
	}

	@Override
	public void onReset() {
		setX(0);
		setY(0);
	}

	@Override
	public void onPreDraw() {
		textField.color(MathUtils.interpolateColor(
				new Color(0.6f, 0.6f, 0.6f),
				Color.WHITE,
				listeningAnimation.getCurrent()
		));
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		if (hidden && activeAnimation.getCurrent() == 0.0f) {
			return;
		}

		OpacityStack.push(activeAnimation.getCurrent());

		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .outlineColor(outlineColor.get())
		          .color(backgroundColor.get())
		          .draw(matrices);

		Renderer2D.SDF_SHADER.drawCentered(
				matrices,
				Textures.MAGNIFIER,
				drawX + height / 2.0f,
				drawY + height / 2.0f,
				5.1f,
				0.5f,
				Color.WHITE
		);

		OpacityStack.pop();
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!textField.isListening() && button.isClick(event, mouseX, mouseY)) {
			toggle();
		}
		else if (event.isPress() && textField.isListening() && !isHovered()) {
			textField.stopListening();
		}
	}

	@Override
	public void onKey(KeyEvent event) {
		if (event.isPress() && event.getValue() == GLFW.GLFW_KEY_ESCAPE) {
			clear();
		}
	}

	public boolean isSearching() {
		return !textField.getString().isEmpty() || textField.isListening();
	}

	public boolean isListening() {
		return textField.isListening();
	}

	public void toggle() {
		if (textField.isListening()) {
			textField.enter();
		}
		else {
			UiUtils.setDraggingWidget(null);
			textField.startListening(textField.getString());
		}
	}

	public String getText() {
		return textField.getString();
	}

	public void onUpdate(Runnable action) {
		textField.setUpdateAction(action);
	}
}
