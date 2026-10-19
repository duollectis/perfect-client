package com.perfect.client.core.functional.setting;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.setting.interfaces.IBaseSetting;
import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.text.NameUtils;
import com.perfect.client.gui.hud.base.HudWidget;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseSetting implements IBaseSetting {

	@Getter
	protected final String id;

	protected final IVisible visible;

	@Setter
	@Getter
	protected Function function;

	@Setter
	@Getter
	protected HudWidget hudWidget;

	public BaseSetting(String id, IVisible visible) {
		this.id = id;
		this.visible = visible;

		validateId();
	}

	private void validateId() {
		for (char c : id.toCharArray()) {
			if (!NameUtils.isValidIdChar(c)) {
				throw new IllegalArgumentException("Invalid setting id: %s".formatted(id));
			}
		}
	}

	@Override
	public String getFunctionId() {
		if (function == null) {
			return null;
		}
		return function.getId();
	}

	@Override
	public String getHudWidgetId() {
		if (hudWidget == null) {
			return null;
		}
		return hudWidget.getId();
	}

	@Override
	public String getName() {
		return getTranslation("");
	}

	@Override
	public boolean isVisible() {
		return visible == null || visible.isVisible();
	}

	@Override
	public String getDescription() {
		return getTranslation(".desc");
	}

	private String getTranslation(String val) {
		String localTranslation = getTranslation(val, false);

		if (localTranslation != null) {
			return localTranslation;
		}

		String standardTranslation = getTranslation(val, true);

		if (standardTranslation != null) {
			return standardTranslation;
		}

		return id;
	}

	public String getTranslation(String val, boolean useStandard) {
		String parent = "settings";

		if (function != null) {
			parent = function.getId();
		} else if (hudWidget != null) {
			parent = hudWidget.getId();
		}

		String path = "%s.%s%s".formatted(parent, id, val);

		String translation = Translations.get(path, null, useStandard);

		if (translation != null) {
			return translation;
		}

		if (function != null && function.getTypeId() != null) {
			translation = Translations.get("%s.%s%s".formatted(function.getTypeId(), id, val), null, useStandard);

			if (translation != null) {
				return translation;
			}
		}

		translation = Translations.get("globals.%s%s".formatted(id, val), null, useStandard);

		return translation;
	}

	public abstract Widget createWidget();
}
