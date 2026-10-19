package com.perfect.client.gui.widgets.settings.elements.enums;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.interfaces.settings.enums.IEnumSetting;
import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.text.INameable;
import com.perfect.client.core.utils.text.ITranslatable;
import com.perfect.client.gui.utils.Rounding;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class EnumButtonWidget<T, E extends Enum<?>> extends Widget {

	private static final float BUTTON_TEXT_DEPTH = 2.2f;
	private static final Rounding BUTTON_ROUNDING = Rounding.full(1.6f);
	private static final float BUTTON_HEIGHT = 6.7f;

	@Getter
	private final E value;
	private final IEnumSetting<T, E> setting;
	private final Runnable clickAction;

	private String name;

	private final Animation activeAnimation = new Animation(Easing.IN_OUT_SINE, 300, false).finish();

	private float activeAnim;

	public EnumButtonWidget(E value, IEnumSetting<T, E> setting, Runnable clickAction) {
		this.value = value;
		this.setting = setting;
		this.clickAction = clickAction;

		name = getName(value);
	}

	@Override
	public void onPreTick() {
		name = getName(value);
	}

	@Override
	public void onPreDraw() {
		boolean active = setting.get(value);
		activeAnim = activeAnimation.changeDirectionAndGet(active);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		width = Fonts.MONTSERRAT_MEDIUM.getTextWidth(name, 3.5f) + BUTTON_TEXT_DEPTH * 2f;
		height = BUTTON_HEIGHT;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Color color = MathUtils.interpolateColor(
				Managers.COLOR.get(Managers.COLOR.SETTING),
				Managers.COLOR.get(Managers.COLOR.ENUM_ACTIVE),
				activeAnim
		);

		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(BUTTON_ROUNDING)
		          .color(color)
		          .draw(matrices);

		Fonts.MONTSERRAT_MEDIUM.drawYCentered(
				matrices,
				name,
				3.5f,
				drawX + BUTTON_TEXT_DEPTH,
				drawY + height / 2f,
				Color.WHITE
		);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (event.isLeftOrRightPress() && isHovered()) {
			clickAction.run();
		}
	}

	private String getName(E value) {
		if (value instanceof INameable nameable) {
			return nameable.getName();
		}

		return getTranslation(value);
	}

	private String getTranslation(E value) {
		StringBuilder path = new StringBuilder();

		if (!isGlobalEnum(value)) {
			if (setting.getFunctionId() != null) {
				path.append(setting.getFunctionId());
				path.append(".");
			} else if (setting.getHudWidgetId() != null) {
				path.append(setting.getHudWidgetId());
				path.append(".");
			}
		}

		path.append("enums.");

		if (value instanceof ITranslatable translatable) {
			path.append(translatable.getTranslationId());
			path.append(".");
		}

		path.append(getEnumTranslationName(value));

		return Translations.get(path.toString());
	}

	private boolean isGlobalEnum(E value) {
		return value instanceof ITranslatable translatable && translatable.isGlobal();
	}

	private static String getEnumTranslationName(Enum<?> value) {
		return value.toString()
		            .toLowerCase()
		            .replace("_", " ")
		            .trim()
		            .replace(" ", "-");
	}
}
