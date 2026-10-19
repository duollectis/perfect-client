package com.perfect.client.gui.widgets.settings;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.render.RenderPackets;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SettingWidget<T, S extends ISetting<T>> extends Widget {

	public static final float TEXT_HEIGHT = 4.4f;

	public final static float PLATE_DEPTH = 2.8f;
	public final static float WIDTH = SettingsListWidget.WIDTH - PLATE_DEPTH * 2.0f;

	private boolean canHover = true;

	protected final S setting;

	public SettingWidget(S setting) {
		this.setting = setting;
	}

	public String getSettingDescription() {
		return setting.getDescription();
	}

	public static void enableFontPacking() {
		Fonts.enablePacking(RenderPackets.BOTTOM_FONT_LAYER);
	}

	public static void disableFontPacking() {
		Fonts.disablePacking();
	}

	@Override
	protected void onUpdateHover() {
		super.onUpdateHover();
		button.setHovered(isHovered() && canHover);
	}

	public abstract float getBorderedWidth();
}
