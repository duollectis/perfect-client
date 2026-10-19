package com.perfect.client.gui.widgets.settings.elements;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.setting.interfaces.settings.IBindSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.gui.utils.StringIndicator;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public class BindWidget extends SettingWidget<Bind, IBindSetting> {

	private static final float HEIGHT = 4.4f;
	private static final float BOX_ROUNDING = 1.0f;
	private static final float TEXT_X_DEPTH = 1.2f;

	private final StringIndicator indicator = new StringIndicator();

	private float boxWidth;

	public BindWidget(IBindSetting setting) {
		super(setting);
	}

	@Override
	public float getBorderedWidth() {
		return boxWidth;
	}

	@Override
	public void onPreDraw() {
		super.onPreDraw();

		if (setting.getListener().isBinding()) {
			container.get().disableEscape();
		}
		else {
			indicator.reset();
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(HEIGHT);

		String string = setting.getListener().isBinding() ? indicator.get() : setting.get().toString();
		boxWidth = MONTSERRAT_MEDIUM.getTextWidth(string, 3.5f) + TEXT_X_DEPTH * 2.0f;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		String string = setting.getListener().isBinding() ? indicator.get() : setting.get().toString();

		float boxX = drawX + width - boxWidth;

		Renderer2D.round()
		          .pos(boxX, drawY, boxWidth, getHeight())
		          .rounding(BOX_ROUNDING)
		          .color(Managers.COLOR.get(Managers.COLOR.SETTING))
		          .draw(matrices);

		ScissorStack.pushScissor(matrices, drawX, drawY, width, height);

		MONTSERRAT_MEDIUM.drawYCentered(
				matrices,
				string,
				3.5f,
				boxX + TEXT_X_DEPTH,
				drawY + height / 2.0f,
				getBindColor()
		);

		ScissorStack.popScissor(matrices);
	}

	private Color getBindColor() {
		if (setting.get().isSet() || setting.getListener().isBinding()) {
			return Managers.COLOR.get(Managers.COLOR.MAIN);
		}

		return Color.WHITE;
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!Managers.BIND.isWaitMouseRelease()
				&& button.isClick(event, mouseX, mouseY)
				&& !setting.getListener().isBinding()) {
			setting.getListener().setBinding(true);
		}
	}
}
