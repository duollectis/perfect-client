package com.perfect.client.gui.widgets.picker.multi.panel;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.SizedFont;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.gui.widgets.common.TextWidget;
import com.perfect.client.gui.widgets.common.button.ButtonWidget;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.PerfectClient.mc;

public class ColorButtonsWidget extends HorizontalListWidget {

	private static final SizedFont font = new SizedFont(() -> Fonts.MONTSERRAT_MEDIUM, 3.5f);

	private final ISetting<MultiColor> setting;

	private final Runnable resetRunnable;

	public ColorButtonsWidget(ISetting<MultiColor> setting, Runnable resetRunnable) {
		this.setting = setting;
		this.resetRunnable = resetRunnable;

		ButtonWidget copyButton = addChild(new ButtonWidget(this::copy));
		ButtonWidget pasteButton = addChild(new ButtonWidget(this::paste));

		setupButton(copyButton.add(new TextWidget(() -> Translations.get("gui.copy")).font(font)));
		setupButton(pasteButton.add(new TextWidget(() -> Translations.get("gui.paste")).font(font)));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(1.0f);

		float buttonWidth = width / 2.0f - getIndent() / 2.0f;

		getChildren().forEach(button -> {
			button.setWidth(buttonWidth);
			button.setHeight(height);
			button.setRounding(0.75f);
		});

		super.onSizeUpdate(matrices);

		setHeight(5.75f);
	}

	private ButtonWidget setupButton(ButtonWidget button) {
		button.setOutlineSize(0.6f);
		button.setOutlineColorSupplier(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_OUTLINE));
		button.backgroundColor(() -> Managers.COLOR.get(Managers.COLOR.LIST_SETTING_BACKGROUND));
		return button;
	}

	public void copy() {
		String string = setting.get().asString();
		mc.keyboard.setClipboard(string);
	}

	public void paste() {
		String string = mc.keyboard.getClipboard();
		setting.get().parse(string);
		resetRunnable.run();
	}
}
