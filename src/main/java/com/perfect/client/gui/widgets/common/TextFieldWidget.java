package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.event.events.input.keyboard.CharEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.event.events.input.mouse.MouseMoveEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.render.renderer.OpacityStack;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.core.utils.text.NameUtils;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.core.utils.text.filter.TextFieldState;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.utils.UiUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public class TextFieldWidget extends Widget {

	private static final int DEFAULT_CHAR_LIMIT = 50;

	private static final float HOVER_EXPAND = 3f;

	private static final float CURSOR_X_SIZE = 0.5f;
	private static final float CURSOR_ROUNDING = 0.8f;
	private static final float MAX_CURSOR_ALPHA = 1f;
	private static final float MIN_CURSOR_ALPHA = 0.2f;

	private static final int BLINK_DELAY = 450;

	private static final SizedFont DEFAULT_FONT = new SizedFont(() -> MONTSERRAT_MEDIUM, 4f);

	private final Animation blinkAnimation = new Animation(Easing.IN_OUT_SINE, 150).finish();
	private final Animation opacityAnimation = new Animation(Easing.LINEAR, 100).finish();

	private final StringBuilder stringBuilder = new StringBuilder();

	private final TimerUtils blinkTimer = new TimerUtils();

	private final Dragging dragging = new Dragging();

	@Getter
	private SizedFont font = DEFAULT_FONT;

	private Supplier<String> noListenedTextSupplier = () -> "";

	@Setter
	private ITextFilter filter;

	@Setter
	private Consumer<String> enterAction;

	@Setter
	private Runnable updateAction;

	@Getter
	private boolean listening;
	private boolean indicator;

	private int cursorStart;
	private int cursorEnd;

	private float cursorBlinkAnim;
	private float cursorOpacity;

	private final FloatAnimation cursorStartRenderX = new FloatAnimation(0.2).defined(false);
	private final FloatAnimation cursorEndRenderX = new FloatAnimation(0.2).defined(false);

	private boolean keyImpl = false;

	private int charLimit = DEFAULT_CHAR_LIMIT;

	public TextFieldWidget() {
		this(null, null);
	}

	public TextFieldWidget(ITextFilter filter) {
		this(null, filter);
	}

	public TextFieldWidget(Consumer<String> enterAction, ITextFilter filter) {
		this.enterAction = enterAction;
		this.filter = filter;
	}

	public TextFieldWidget font(SizedFont font) {
		this.font = font;
		return this;
	}

	public TextFieldWidget keyImpl() {
		keyImpl = true;
		return this;
	}

	public TextFieldWidget limit(int limit) {
		this.charLimit = limit;
		return this;
	}

	public void setNoListenedText(String text) {
		noListenedTextSupplier = () -> text;
	}

	public void setNoListenedText(Supplier<String> textSupplier) {
		noListenedTextSupplier = textSupplier;
	}

	public void stopListening() {
		listening = false;
		UiUtils.setListeningWidget(null);
	}

	public void startListening() {
		startListening(noListenedTextSupplier.get());
	}

	public void startListening(String string) {
		if (!listening) {
			clear();
			UiUtils.startListening(this);

			stringBuilder.append(string);
			listening = true;

			resetIndicator();

			opacityAnimation.setDirection(false);
			opacityAnimation.finish();

			cursorStart = 0;
			cursorEnd = stringBuilder.length();

			cursorStartRenderX.defined(false);
			cursorEndRenderX.defined(false);
		}
	}

	@Override
	public void onOpen() {
		blinkAnimation.finish();
		opacityAnimation.finish();
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!event.isPress() || !listening) {
			return;
		}

		int index = getCursorIndex(getString(), mouseX - drawX);

		if (!event.hasModifier(InputEvent.Modifier.SHIFT)) {
			cursorStart = index;
		}

		cursorEnd = index;
		dragging.press(event.getValue());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging.isDragging()) {
			cursorEnd = getCursorIndex(getString(), mouseX - drawX);
		}
	}

	@Override
	public void onChar(CharEvent event) {
		if (!keyImpl) {
			onChar(event.getCharacter());
		}
	}

	public void onChar(char character) {
		if (!listening) {
			return;
		}

		if (filter == null || filter.test(getCurrentFieldState(), character)) {
			if (cursorStart != cursorEnd) {
				deleteSelection();
			}

			if (stringBuilder.length() >= charLimit) {
				return;
			}

			stringBuilder.insert(cursorStart, character);

			setCursorStart(cursorStart + 1);
			setCursorEnd(cursorStart);

			onUpdate();
		}
	}

	@Override
	public void onKey(KeyEvent event) {
		if (event.isRelease()) {
			return;
		}

		if (!listening) {
			return;
		}

		resetIndicator();

		boolean hotKeyPressed = pressHotKey(event);

		if (keyImpl && !hotKeyPressed) {
			String keyName = NameUtils.getKeyName(event.getValue());

			if (keyName.length() == 1) {
				if (Managers.KEY_STATE.isKeyboardPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ||
						Managers.KEY_STATE.isKeyboardPressed(GLFW.GLFW_KEY_LEFT_SHIFT)
				) {
					keyName = keyName.toUpperCase();
				}
				else {
					keyName = keyName.toLowerCase();
				}

				char c = keyName.charAt(0);
				onChar(c);
			}
		}
	}

	private boolean pressHotKey(KeyEvent event) {
		switch (event.getValue()) {
			case GLFW.GLFW_KEY_BACKSPACE -> {
				erase(false);
				return true;
			}
			case GLFW.GLFW_KEY_DELETE -> {
				erase(true);
				return true;
			}
			case GLFW.GLFW_KEY_ESCAPE -> {
				enter();
				return true;
			}
			case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
				if (enterAction != null) {
					enter();
				}
				else {
					stopListening();
				}
				return true;
			}
			case GLFW.GLFW_KEY_A -> {
				if (event.hasModifier(InputEvent.Modifier.CONTROL)) {
					selectAll();
					return true;
				}
			}
			case GLFW.GLFW_KEY_C -> {
				if (event.hasModifier(InputEvent.Modifier.CONTROL)) {
					if (cursorStart != cursorEnd) {
						copyToClipboard();
					}

					return true;
				}
			}
			case GLFW.GLFW_KEY_X -> {
				if (event.hasModifier(InputEvent.Modifier.CONTROL)) {
					if (cursorStart != cursorEnd) {
						copyToClipboard();
						erase(false);
					}

					return true;
				}
			}
			case GLFW.GLFW_KEY_V -> {
				if (event.hasModifier(InputEvent.Modifier.CONTROL)) {
					pasteFromClipboard();
					return true;
				}
			}
			case GLFW.GLFW_KEY_LEFT -> {
				if (event.hasModifier(InputEvent.Modifier.SHIFT)) {
					setCursorEnd(cursorEnd - 1);
					return true;
				}
				else if (cursorStart == cursorEnd) {
					setCursorStart(cursorStart - 1);
					setCursorEnd(cursorStart);
					return true;
				}
				else if (cursorStart > cursorEnd) {
					setCursorStart(cursorEnd);
					return true;
				}
				else {
					setCursorEnd(cursorStart);
					return true;
				}
			}
			case GLFW.GLFW_KEY_RIGHT -> {
				if (event.hasModifier(InputEvent.Modifier.SHIFT)) {
					setCursorEnd(cursorEnd + 1);
					return true;
				}
				else if (cursorStart == cursorEnd) {
					setCursorStart(cursorStart + 1);
					setCursorEnd(cursorStart);
					return true;
				}
				else if (cursorStart < cursorEnd) {
					setCursorStart(cursorEnd);
					return true;
				}
				else {
					setCursorEnd(cursorStart);
					return true;
				}
			}
		}

		return false;
	}

	public boolean isValidText(String text) {
		if (filter == null) {
			return true;
		}
		StringBuilder newStringBuilder = new StringBuilder(stringBuilder);
		String newString = newStringBuilder.delete(getCursorStart(), getCursorEnd()).toString();

		for (char character : text.toCharArray()) {
			if (!filter.test(getCurrentFieldState(newString), character)) {
				return false;
			}
			newString += character;
		}
		return true;
	}

	@Override
	public void onPreTick() {
		dragging.checkNeedUnPress();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		String s;
		if (listening) {
			s = getString();
		}
		else {
			s = getNoListenedVal();
		}

		width = font.getTextWidth(s);
		height = font.getFontHeight();
	}

	public String getNoListenedVal() {
		return noListenedTextSupplier.get();
	}

	@Override
	public void onPreDraw() {
		if (listening) {
			container.get().disableEscape();

			float cursorStartX = font.getTextWidth(stringBuilder.substring(0, getCursorStart()));
			float cursorEndX = font.getTextWidth(stringBuilder.substring(0, getCursorEnd()));

			cursorStartRenderX.updateAndGet(cursorStartX);
			cursorEndRenderX.updateAndGet(cursorEndX);

			if (cursorStart == cursorEnd && blinkTimer.passedMillis(BLINK_DELAY)) {
				blinkTimer.reset();
				indicator = !indicator;
			}
		}
	}

	@Override
	public void onAnimationTick() {
		cursorBlinkAnim = blinkAnimation.changeDirectionAndGet(indicator);

		float opacityAnim = opacityAnimation.changeDirectionAndGet(isCursorSingle());
		cursorOpacity = MathUtils.interpolateFloat(MIN_CURSOR_ALPHA, MAX_CURSOR_ALPHA, opacityAnim);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		String noListenedVal = noListenedTextSupplier.get();

		String val = (listening || noListenedVal.isEmpty()) ? getString() : noListenedVal;
		font.drawYCentered(matrices, val, drawX, drawY, color);

		ScissorStack.expand(matrices, 1);

		if (listening) {
			float selX = drawX + cursorStartRenderX.getCurrent();
			float selWidth = cursorEndRenderX.getCurrent() - cursorStartRenderX.getCurrent();

			float height = font.getFontHeight();

			float size = CURSOR_X_SIZE / 2f;
			float maxRounding = size + selWidth / 2f;

			float rounding = Math.min(CURSOR_ROUNDING, maxRounding);

			boolean blinking = isCursorSingle();

			if (blinking) {
				OpacityStack.push(cursorBlinkAnim);
			}

			Renderer2D.round()
			          .pos(selX - size, drawY - height / 2, selWidth + size * 2f, height)
			          .rounding(rounding)
			          .softness(0.1f)
			          .alpha(cursorOpacity)
			          .color(Color.WHITE)
			          .draw(matrices);

			if (blinking) {
				OpacityStack.pop();
			}
		}

		ScissorStack.setLast(matrices);
	}

	@Override
	protected void onUpdateHover() {
		float hoverX = drawX - HOVER_EXPAND;
		float hoverWidth = width + HOVER_EXPAND * 2f;
		float hoverY = drawY - height / 2f;

		button.setHovered(UiUtils.isHovered(mouseX, mouseY, hoverX, hoverY, hoverWidth, height));
	}

	public boolean isCursorSingle() {
		return cursorStart == cursorEnd;
	}

	public void resetIndicator() {
		blinkTimer.reset();
		indicator = true;
	}

	public boolean enterOnMouse() {
		if (isHovered()) {
			return false;
		}
		else {
			enter();
			return true;
		}
	}

	public void enter() {
		if (!listening) {
			return;
		}

		if (enterAction != null) {
			enterAction.accept(getString());
		}

		stopListening();
		clear();
	}

	public void clear() {
		cursorStart = 0;
		cursorEnd = 0;

		if (!stringBuilder.isEmpty()) {
			stringBuilder.delete(0, stringBuilder.length());
			onUpdate();
		}
	}

	public void erase(boolean right) {
		if (stringBuilder.isEmpty()) {
			return;
		}

		if (cursorStart == cursorEnd) {
			if (right) {
				if (cursorStart < stringBuilder.length()) {
					stringBuilder.deleteCharAt(cursorStart);
				}
			}
			else if (cursorStart > 0) {
				setCursorStart(cursorStart - 1);
				stringBuilder.deleteCharAt(cursorStart);
			}

			setCursorEnd(cursorStart);
		}
		else {
			deleteSelection();
		}

		onUpdate();
	}

	public void copyToClipboard() {
		String substring = stringBuilder.substring(getCursorStart(), getCursorEnd());
		mc.keyboard.setClipboard(substring);
	}

	public void pasteFromClipboard() {
		String text = mc.keyboard.getClipboard();

		if (isValidText(text)) {
			for (char character : text.toCharArray()) {
				onChar(character);
			}
		}
	}

	public void deleteSelection() {
		stringBuilder.delete(getCursorStart(), getCursorEnd());

		if (cursorStart < cursorEnd) {
			setCursorEnd(cursorStart);
		}
		else {
			setCursorStart(cursorEnd);
		}

		onUpdate();
	}

	private int getCursorStart() {
		return Math.min(cursorStart, cursorEnd);
	}

	private int getCursorEnd() {
		return Math.max(cursorStart, cursorEnd);
	}

	private void setCursorStart(int index) {
		cursorStart = index;

		if (cursorStart < 0) {
			cursorStart = 0;
		}

		if (cursorStart > stringBuilder.length()) {
			cursorStart = stringBuilder.length();
		}
	}

	private void setCursorEnd(int index) {
		cursorEnd = index;

		if (cursorEnd < 0) {
			cursorEnd = 0;
		}

		if (cursorEnd > stringBuilder.length()) {
			cursorEnd = stringBuilder.length();
		}
	}

	private int getCursorIndex(String text, float mouseX) {
		float accumulatedWidth = 0;
		for (int i = 0; i < text.length(); i++) {

			float charWidth = font.getCharWidth(text.charAt(i));
			if (mouseX <= accumulatedWidth + charWidth / 2) {
				return i;
			}

			accumulatedWidth += charWidth;
		}
		return text.length();
	}

	public void selectAll() {
		cursorStart = 0;
		cursorEnd = stringBuilder.length();
	}

	public String getString() {
		return stringBuilder.toString();
	}

	public void onUpdate() {
		if (updateAction != null) {
			updateAction.run();
		}
	}

	private TextFieldState getCurrentFieldState() {
		return getCurrentFieldState(getString());
	}

	private TextFieldState getCurrentFieldState(String string) {
		return new TextFieldState(getCursorStart(), getCursorEnd(), string);
	}
}