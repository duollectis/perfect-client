package com.perfect.client.gui.widgets.settings.elements.order;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.impl.enums.EnumOrderSetting;
import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.text.INameable;
import com.perfect.client.core.utils.text.ITranslatable;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.settings.elements.toggle.ToggleWidget;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class OrderElementWidget<T extends Enum<T>> extends Widget {

	private static final float HEIGHT = 6.5f;
	private static final float NAME_X_OFFSET = 9.25f;
	private static final float X_OFFSET = 3.0f;

	@Getter
	private final T value;
	private final EnumOrderSetting<T> setting;

	private final ToggleWidget toggleWidget = addChild(new ToggleWidget(this::isEnabled));

	private final Animation dragHoveredAnimation = new Animation(Easing.IN_OUT_DOUBLE, 300, false).finish();

	private final OrderFieldWidget<T> parent;

	private boolean dragHovered;

	@Setter
	private float targetY;

	private final FloatAnimation yAnimation = new FloatAnimation(0.3).target(() -> targetY);

	public OrderElementWidget(OrderFieldWidget<T> parent, T value, EnumOrderSetting<T> setting) {
		this.parent = parent;
		this.value = value;
		this.setting = setting;
	}

	private boolean isEnabled() {
		return setting.isEnabled(value);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(HEIGHT);
	}

	@Override
	public void onAnimationTick() {
		dragHoveredAnimation.changeDirection(dragHovered);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		setY(yAnimation.updateAndGet());

		toggleWidget.setX(width - toggleWidget.getWidth() - X_OFFSET);
		toggleWidget.setY(HEIGHT / 2.0f - toggleWidget.getHeight() / 2.0f);
	}

	@Override
	public void onUpdateHover() {
		super.onUpdateHover();
		dragHovered = UiUtils.isHovered(mouseX, mouseY, drawX, drawY, NAME_X_OFFSET, height);
	}

	@Override
	public void onDisableHover() {
		super.onDisableHover();
		dragHovered = false;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Color dragColor = MathUtils.interpolateColor(
				Color.WHITE,
				Managers.COLOR.get(Managers.COLOR.MAIN),
				dragHoveredAnimation.get()
		);

		Renderer2D.drawCenteredTexture(
				matrices,
				Textures.DRAGGABLE.getGlId(),
				drawX + NAME_X_OFFSET / 2.0f,
				drawY + height / 2.0f,
				3.6f,
				dragColor,
				null
		);

		Fonts.MONTSERRAT_MEDIUM.drawYCentered(
				matrices,
				getName(),
				3.5f,
				drawX + NAME_X_OFFSET,
				drawY + height / 2f,
				Color.WHITE
		);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!event.isPress()) {
			return;
		}

		if (dragHovered) {
			parent.setCurrentDragging(this, event.getValue());
		}
		else if (isHovered()) {
			setting.toggle(value);
		}
	}

	private String getName() {
		if (value instanceof INameable nameable) {
			return nameable.getName();
		}

		return getTranslation();
	}

	private String getTranslation() {
		StringBuilder path = new StringBuilder();

		if (setting.getFunctionId() != null && !isGlobalEnum()) {
			path.append(setting.getFunctionId());
			path.append(".");
		}

		path.append("enums.");

		if (value instanceof ITranslatable translatable) {
			path.append(translatable.getTranslationId());
			path.append(".");
		}

		path.append(getEnumTranslationName(value));

		return Translations.get(path.toString());
	}

	private boolean isGlobalEnum() {
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
