package com.perfect.client.gui.widgets.bind;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.utils.Rounding;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import org.joml.Matrix3x2fStack;

import java.util.HashMap;
import java.util.Map;

public class BindModeLineWidget extends HorizontalListWidget {

	private static final float ROUNDING = 1.25f;

	private final AbstractBindListener bindListener;

	private final Map<Bind.Mode, BindModeWidget> modes = new HashMap<>();

	public BindModeLineWidget(AbstractBindListener bindListener) {
		this.bindListener = bindListener;

		addMode(Bind.Mode.TOGGLE, Rounding.left(ROUNDING));
		addMode(Bind.Mode.HOLD, Rounding.none());
		addMode(Bind.Mode.INVERSE, Rounding.right(ROUNDING));

		setNoWidthCalc(true);

		Bind.Mode mode = bindListener.getBind().getMode();

		enableMode(mode);
		modes.get(mode).finishAnimation();
	}

	private void addMode(Bind.Mode mode, Rounding rounding) {
		modes.put(mode, addChild(new BindModeWidget(mode, this::enableMode, rounding)));
	}

	private void enableMode(Bind.Mode mode) {
		modes.values().forEach(widget -> widget.setEnabled(false));

		bindListener.setBind(bindListener.getBind().withMode(mode));
		modes.get(mode).setEnabled(true);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(7.0f);

		modes.values().forEach(mode -> {
			mode.setWidth(width / 3.0f);
			mode.setHeight(height);
		});
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(ROUNDING)
		          .thickness(0.5f)
		          .color(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE))
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND))
		          .draw(matrices);
	}
}
