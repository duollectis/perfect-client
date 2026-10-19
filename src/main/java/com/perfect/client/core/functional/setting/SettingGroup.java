package com.perfect.client.core.functional.setting;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SettingGroup implements Iterable<BaseSetting> {

	private final List<BaseSetting> allSettings;
	private final List<BaseSetting> groupSettings = new ArrayList<>();

	public SettingGroup(List<BaseSetting> allSettings) {
		this.allSettings = allSettings;
	}

	public <T extends BaseSetting> T add(T setting) {
		allSettings.add(setting);
		groupSettings.add(setting);
		return setting;
	}

	@Override
	public @NotNull Iterator<BaseSetting> iterator() {
		return groupSettings.iterator();
	}
}
