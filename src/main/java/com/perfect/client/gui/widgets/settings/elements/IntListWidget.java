package com.perfect.client.gui.widgets.settings.elements;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.impl.number.IntListSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.MutableInt;
import com.perfect.client.core.utils.text.filter.TextFilters;
import com.perfect.client.core.utils.widget.Button;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.TextFieldWidget;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.scrollable.TableWidget;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.List;

public class IntListWidget extends SettingWidget<List<MutableInt>, IntListSetting> {

	private static final float LIST_Y = 5.5f;

	private static final float ADD_BUTTON_SIZE = 4.4f;
	private static final float ADD_BUTTON_ROUNDING = 1.25f;

	private final TableWidget table = addChild(new TableWidget().staticWidth());

	private final FloatAnimation heightAnimation = new FloatAnimation(0.3).defined(false);

	private final Button addButton = new Button();

	public IntListWidget(IntListSetting setting) {
		super(setting);

		for (MutableInt value : setting.get()) {
			table.addChild(new IntWidget(setting, value));
		}
	}

	@Override
	public void onOpen() {
		addButton.unPress();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		table.setY(LIST_Y);
		table.setWidth(width);
		table.setIndent(1.0f);

		setHeight(heightAnimation.updateAndGet(table.getHeight() + LIST_Y));
	}

	@Override
	public float getBorderedWidth() {
		return ADD_BUTTON_SIZE;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		float addButtonX = width - ADD_BUTTON_SIZE;

		Renderer2D.round()
		          .pos(drawX + addButtonX, drawY, ADD_BUTTON_SIZE, ADD_BUTTON_SIZE)
		          .rounding(ADD_BUTTON_ROUNDING)
		          .color(Managers.COLOR.get(Managers.COLOR.MAIN))
		          .draw(matrices);

		Renderer2D.drawCenteredTexture(
				matrices,
				Textures.PLUS.getGlId(),
				drawX + addButtonX + ADD_BUTTON_SIZE / 2.0f,
				drawY + ADD_BUTTON_SIZE / 2.0f,
				3.0f,
				Color.WHITE,
				null
		);
	}

	@Override
	public void onUpdateHover() {
		super.onUpdateHover();
		addButton.setHovered(UiUtils.isHovered(mouseX, mouseY, drawX, drawY, width, ADD_BUTTON_SIZE));
	}

	public void onDisableHover() {
		super.onDisableHover();
		addButton.setHovered(false);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (addButton.isClick(event, mouseX, mouseY)) {
			MutableInt integer = new MutableInt();
			setting.add(integer);

			IntWidget widget = new IntWidget(setting, integer);
			table.addChild(widget);

			widget.startListening();
		}
	}

	public static class IntWidget extends Widget {

		private static final float X_OFFSET = 2.75f;
		private static final float MIN_WIDTH = 10.5f;

		private final TextFieldWidget textField = addChild(new TextFieldWidget(this::enter, TextFilters.INT));

		private final Animation animation = new Animation(Easing.IN_OUT_DOUBLE, 250);

		private final IntListSetting setting;
		private final MutableInt value;

		private float anim;

		public IntWidget(IntListSetting setting, MutableInt value) {
			this.setting = setting;
			this.value = value;

			textField.limit(setting.getMaxLength());
		}

		private void enter(String str) {
			try {
				int val = Integer.parseInt(str);
				setting.set(value, val);
			}
			catch (Exception exception) {
				destroyed = true;
				animation.changeDirection(false);

				setting.remove(value);
			}
		}

		@Override
		public void onPreTick() {
			textField.setColor(Managers.COLOR.get(Managers.COLOR.MAIN));
			textField.setNoListenedText(Integer.toString(value.getValue()));
		}

		@Override
		public void onAnimationTick() {
			anim = animation.get();
			opacity *= anim;
		}

		@Override
		public void onSizeUpdate(Matrix3x2fStack matrices) {
			setHeight(5.5f);
			setWidth(Math.max(getTextWidth() + X_OFFSET * 2.0f, MIN_WIDTH) * anim);
			setRounding(1.0f);
		}

		@Override
		public void onPositionUpdate(Matrix3x2fStack matrices) {
			super.onPositionUpdate(matrices);

			textField.setX(width / 2.0f - getTextWidth() / 2.0f);
			textField.setY(height / 2.0f);
		}

		@Override
		public void onDraw(Matrix3x2fStack matrices) {
			Renderer2D.round()
			          .pos(drawX, drawY, width, height)
			          .rounding(rounding)
			          .color(Managers.COLOR.get(Managers.COLOR.SETTING))
			          .draw(matrices);
		}

		@Override
		public void onMouseButton(MouseButtonEvent event) {
			if (!event.isPress()) {
				return;
			}

			if (!isHovered()) {
				textField.stopListening();
				return;
			}

			if (!textField.isListening() || textField.enterOnMouse()) {
				if (event.isRight() || event.isLeft()) {
					startListening();
				}
			}
		}

		private void startListening() {
			textField.startListening("");
		}

		private float getTextWidth() {
			return textField.getFont().getTextWidth(getText());
		}

		private String getText() {
			return textField.isListening() ? textField.getString() : textField.getNoListenedVal();
		}

		@Override
		public float getAnimationMultiplier() {
			return anim;
		}

		@Override
		public boolean isAnimationFinished() {
			return animation.isFinished(false);
		}
	}
}
