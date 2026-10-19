package com.perfect.client.core.functional.function.config;


import com.perfect.client.gui.hud.base.HudWidget;
import lombok.Getter;

@Getter
public abstract class HudConfig<T extends HudWidget> extends Config {

	protected final T hudWidget;

	public HudConfig(T hudWidget) {
		super(hudWidget.getSettings());
		this.hudWidget = hudWidget;
	}
}