package com.perfect.client.gui.widgets.picker.multi.panel.settings;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.color.multiColor.MutableColor;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.picker.multi.panel.settings.table.TableLineWidget;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;

public class MultiColorSettingsWidget extends VerticalListWidget {

	private final ColorValueWidget opacity;
	private final ColorValueWidget speed;

	private final TableLineWidget tableLine;

	public MultiColorSettingsWidget(ISetting<MultiColor> setting, Consumer<MutableColor> selectColorConsumer) {
		opacity = addChild(new ColorValueWidget(
				() -> Translations.get("gui.opacity"),
				() -> (int) (setting.get().getAlpha() * 100.0f),
				a -> setting.get().setAlpha(Math.clamp(a / 100.0f, 0.0f, 100.0f))
		));

		speed = addChild(new ColorValueWidget(
				() -> Translations.get("gui.speed"),
				() -> (int) (setting.get().getSpeed() * 100.0f),
				speed -> setting.get().setSpeed(Math.clamp(speed / 100.0f, 0.0f, 100.0f))
		));

		tableLine = addChild(new TableLineWidget(setting, selectColorConsumer));

		expandWidth();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(1.75f);

		tableLine.setHeight(height - opacity.getHeight() - speed.getHeight() - indent * 2.0f);

		super.onSizeUpdate(matrices);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {

	}

	public void reset() {
		tableLine.resetTable();
	}
}
