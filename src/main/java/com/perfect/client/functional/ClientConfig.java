package com.perfect.client.functional;

import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.lang.Language;
import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.Strict;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

import java.awt.*;


public class ClientConfig {

	@Getter
	private final Settings settings = new Settings();

	private final SettingGroup sgGui = settings.createGroup();
	private final SettingGroup sgFriends = settings.createGroup();
	private final SettingGroup sgHud = settings.createGroup();
	private final SettingGroup sgAntiCheat = settings.createGroup();
	private final SettingGroup sgRotations = settings.createGroup();

	// ————————————————————————————— GUI ——————————————————————————————— //

	private final BindSetting guiBind = sgGui.add(new BindSetting
			.Builder()
			.id("gui-keybind")
			.defaultValue(Bind.fromKey(GLFW.GLFW_KEY_RIGHT_ALT))
			.build());

	private final DoubleSetting guiScale = sgGui.add(new DoubleSetting
			.Builder()
			.id("gui-scale")
			.range(0.5, 4)
			.defaultValue(1)
			.build());

	private final EnumSetting<Language> language = sgGui.add(new EnumSetting
			.Builder<>(Language.values())
			.id("language")
			.defaultValue(Language.ENGLISH)
			.onChanged(Translations::loadLocalLanguage)
			.build());

	// ————————————————————————————— HUD ——————————————————————————————— //

	private final BoolSetting nameSpacings = sgHud.add(new BoolSetting
			.Builder()
			.id("name-spacings")
			.defaultValue(true)
			.build());

	// ————————————————————————————— FRIENDS ——————————————————————————————— //

	private final MultiColorSetting friendColor = sgFriends.add(new MultiColorSetting
			.Builder()
			.id("friend-color")
			.defaultValue(new MultiColor(Color.GREEN))
			.build());

	// ——————————————————————————— ANTI CHEAT ————————————————————————————— //

	private final BoolSetting airPlace = sgAntiCheat.add(new BoolSetting
			.Builder()
			.id("air-place")
			.defaultValue(true)
			.build());

	private final BoolSetting strictDirections = sgAntiCheat.add(new BoolSetting
			.Builder()
			.id("strict-directions")
			.defaultValue(false)
			.build());

	private final EnumSetting<Strict> strict = sgAntiCheat.add(new EnumSetting
			.Builder<>(Strict.values())
			.id("strict-timing")
			.defaultValue(Strict.NONE)
			.build());

	// ——————————————————————————— ROTATIONS ————————————————————————————— //

	private final BoolSetting movementSync = sgRotations.add(new BoolSetting
			.Builder()
			.id("movement-sync")
			.defaultValue(false)
			.build());

	private final BoolSetting clientRotation = sgRotations.add(new BoolSetting
			.Builder()
			.id("client-rotation")
			.defaultValue(false)
			.build());

	private final DoubleSetting rotationGap = sgRotations.add(new DoubleSetting
			.Builder()
			.id("rotation-gap")
			.defaultValue(5)
			.range(0, 90)
			.build());

	// —————————————————————————————————————————————————————————————————— //

	private float prevGuiSize = 1f;

	public Setting<Bind> getGuiBindSetting() {
		return guiBind;
	}

	public Bind getGuiBind() {
		return guiBind.get();
	}

	public float getGuiScale() {
		if (!guiScale.isWidgetDragging()) {
			prevGuiSize = guiScale.getFloat();
		}
		return prevGuiSize;
	}

	public boolean getNameSpacings() {
		return nameSpacings.get();
	}

	public MultiColor getFriendColor() {
		return friendColor.get();
	}

	public boolean getAirPlace() {
		return airPlace.get();
	}

	public boolean getStrictDirections() {
		return strictDirections.get();
	}

	public Strict getStrictMode() {
		return strict.get();
	}

	public boolean getMovementSync() {
		return movementSync.get();
	}

	public boolean getClientRotation() {
		return clientRotation.get();
	}

	public float getRotationGap() {
		return rotationGap.getFloat();
	}

	public boolean needSupport() {
		return !airPlace.get() || strictDirections.get();
	}
}
