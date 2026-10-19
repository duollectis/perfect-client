package com.perfect.client.gui.widgets.function;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.RenderPackets;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.renderer.OpacityStack;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.widget.Button;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.bind.BindEditWidget;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.settings.SettingsListWidget;
import lombok.Getter;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;


public class FunctionWidget extends Widget {

	public static final float WIDTH = CategoryWidget.WIDTH - CategoryWidget.INDENT * 2.0f;

	private static final float BUTTON_HEIGHT = 10.7f;
	private static final float ROUNDING = 1.5f;
	private static final float TEXT_X_OFFSET = 3f;
	private static final float ENABLED_BIND_OFFSET = 5.1f;

	private static final float CHECK_X_OFFSET = 2.2f;
	private static final float CHECK_SIZE = 3.9f;

	@Getter
	private final Function function;
	private final SettingsListWidget settingsList;

	private final Animation hoveredAnimation = new Animation(Easing.IN_OUT_SINE, 300, false).finish();
	private final Animation enabledAnimation = new Animation(Easing.IN_OUT_SINE, 150, false).finish();
	private final Animation expandAnimation = new Animation(Easing.IN_OUT_DOUBLE, 300, false).finish();
	private final Animation hideAnimation = new Animation(Easing.IN_OUT_DOUBLE, 250).finish();

	private boolean expanded;

	@Getter
	private boolean settingsHovered;

	private final Button toggleButton = new Button();

	private float enabledAnim;
	private float expandAnim;
	private float hoveredAnim;
	private float hideAnim;

	private float centerY;

	public FunctionWidget(Function function) {
		this.function = function;
		settingsList = addChild(new SettingsListWidget(function.getSettings()));
	}

	@Override
	public String onGetDescription() {
		return toggleButton.isHovered() ? function.getDescription() : null;
	}

	@Override
	public void onOpen() {
		hoveredAnimation.finish(false);
		enabledAnimation.finish(function.isEnabled());
		hideAnimation.finish(true);
	}

	@Override
	public void onPreTick() {
		settingsList.setActive(expanded);
		settingsList.setVisible(!expandAnimation.isFinished(false));
	}

	@Override
	public void onAnimationTick() {
		enabledAnim = enabledAnimation.changeDirectionAndGet(function.isEnabled());
		expandAnim = expandAnimation.changeDirectionAndGet(expanded);
		hoveredAnim = hoveredAnimation.changeDirectionAndGet(toggleButton.isHovered());
		hideAnim = hideAnimation.changeDirectionAndGet(shouldDraw());

		settingsList.setOpacity(expandAnim);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		width = WIDTH;
		height = BUTTON_HEIGHT;

		if (!expandAnimation.isFinished(false)) {
			height += (settingsList.getHeight() + SettingsListWidget.DEPTH) * expandAnim;
		}

		height *= hideAnim;
		opacity *= hideAnim;

		setRounding(Math.min(ROUNDING, height / 2.0f));
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		settingsList.setX(SettingsListWidget.DEPTH);
		settingsList.setY(BUTTON_HEIGHT);

		centerY = drawY + (BUTTON_HEIGHT / 2.0f) * hideAnim;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		OpacityStack.push(hideAnim);
		RenderPackets.setDrawPacket(RenderPackets.BOTTOM_FONT_LAYER);

		drawButton(matrices);

		Fonts.enablePacking(RenderPackets.BOTTOM_FONT_LAYER);

		MONTSERRAT_MEDIUM.drawYCentered(
				matrices,
				function.getName(),
				4.0f,
				drawX + TEXT_X_OFFSET,
				drawY + BUTTON_HEIGHT / 2.0f * hideAnim,
				Color.WHITE
		);

		Fonts.disablePacking();

		drawInfo(matrices);

		OpacityStack.pop();
		RenderPackets.disablePacking();
	}

	private void drawInfo(Matrix3x2fStack matrices) {
		if (shouldDrawBind()) {
			Fonts.enablePacking(RenderPackets.BOTTOM_FONT_LAYER);

			String bind = function.getBindListener().getBind().toString();
			float bindWidth = MONTSERRAT_MEDIUM.getTextWidth(bind, 4.0f);

			float bindX = drawX + width - TEXT_X_OFFSET - ENABLED_BIND_OFFSET * enabledAnim - bindWidth;

			MONTSERRAT_MEDIUM.drawYCentered(
					matrices,
					bind,
					4f,
					bindX,
					centerY,
					Managers.COLOR.get(Managers.COLOR.BIND)
			);

			Fonts.disablePacking();
		}

		if (enabledAnim > 0) {
			float checkX = drawX + width - CHECK_SIZE - CHECK_X_OFFSET * enabledAnim;
			float checkY = centerY - CHECK_SIZE / 2.0f;

			OpacityStack.push(enabledAnim);

			Renderer2D.drawTexture(
					matrices,
					Textures.CHECK.getGlId(),
					checkX,
					checkY,
					CHECK_SIZE,
					CHECK_SIZE,
					Managers.COLOR.get(Managers.COLOR.MAIN),
					null
			);

			OpacityStack.pop();
		}
	}

	private boolean shouldDrawBind() {
		return function.getBindListener().getBind().isSet();
	}

	private void drawButton(Matrix3x2fStack matrices) {
		float alpha = hoveredAnim * 0.18f;

		Color color = MathUtils.interpolateColor(
				Managers.COLOR.get(Managers.COLOR.FUNCTION_BACKGROUND),
				Managers.COLOR.get(Managers.COLOR.FUNCTION_HOVERED),
				hoveredAnim
		);

		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.5f)
		          .alpha(alpha * hideAnim)
		          .outlineAlpha(hideAnim)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE))
		          .color(color)
		          .draw(matrices);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (toggleButton.isAnyClick(event, mouseX, mouseY)) {
			if (event.getValue() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (event.hasModifier(InputEvent.Modifier.SHIFT)) {
					openBind();
				}
				else {
					function.toggle();
				}
			}
			else if (event.getValue() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				expandIfHasSettings();
			}
			else if (event.getValue() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
				openBind();
			}
		}
	}

	private void openBind() {
		container.get().addWindow(BindEditWidget.createWindow(function.getBindListener(), mouseX, mouseY));
	}

	private void expandIfHasSettings() {
		if (function.getSettings().isInvisible()) {
			expanded = false;
		}
		else {
			expanded = !expanded;
		}
	}

	@Override
	protected void onUpdateHover() {
		super.onUpdateHover();
		toggleButton.setHovered(UiUtils.isHovered(mouseX, mouseY, drawX, drawY, width, BUTTON_HEIGHT));

		float settingsX = drawX + SettingsListWidget.DEPTH;
		float settingsY = drawY + BUTTON_HEIGHT;
		float settingsHeight = height - BUTTON_HEIGHT;
		float settingsWidth = width - SettingsListWidget.DEPTH * 2.0f;

		settingsHovered = UiUtils.isHovered(mouseX, mouseY, settingsX, settingsY, settingsWidth, settingsHeight);
	}

	@Override
	public void onDisableHover() {
		super.onDisableHover();
		toggleButton.setHovered(false);
	}

	@Override
	public boolean isAnimationFinished() {
		return hideAnimation.isFinished(false);
	}

	@Override
	public float getAnimationMultiplier() {
		return hideAnim;
	}
}
