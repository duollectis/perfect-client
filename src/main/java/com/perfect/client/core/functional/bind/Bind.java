package com.perfect.client.core.functional.bind;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.enums.EnumUtils;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.text.INameable;
import com.perfect.client.core.utils.text.NameUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

public class Bind {

	public static final Bind NONE = new Bind(true, -1, -1, Mode.TOGGLE);

	private final boolean isKey;

	@Getter
	private final int value;

	@Getter
	private final int modifier;

	@Getter
	private final Mode mode;

	private final String string;

	public Bind(boolean isKey, int value, int modifier, Mode mode) {
		this.isKey = isKey;
		this.value = value;
		this.modifier = modifier;
		this.mode = mode;

		if (value == -1) {
			this.string = "None";
		}
		else {
			String modName = modifier == -1 ? "" : (NameUtils.getKeyName(modifier) + " + ");
			this.string = modName + (isKey ? NameUtils.getKeyName(value) : NameUtils.getButtonName(value));
		}
	}

	public Bind withMode(Bind bind) {
		return withMode(bind.mode);
	}

	public Bind withMode(Mode mode) {
		return new Bind(isKey, value, modifier, mode);
	}

	public boolean isSet() {
		return value != -1;
	}

	public boolean isKeyPressed() {
		return isKey ? Managers.KEY_STATE.isKeyboardPressed(value) : Managers.KEY_STATE.isMousePressed(value);
	}

	public boolean isModPressed() {
		return modifier == -1 || Managers.KEY_STATE.isKeyboardPressed(modifier);
	}

	public boolean isModDefinedAndPressed() {
		return modifier != -1 && Managers.KEY_STATE.isKeyboardPressed(modifier);
	}

	public boolean matchesWithMod(boolean isKey, int value) {
		return matches(isKey, value) && isModPressed();
	}

	public boolean matches(boolean isKey, int value) {
		return this.isKey == isKey && this.value == value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Bind bind) {
			return isKey == bind.isKey
					&& value == bind.value
					&& modifier == bind.modifier
					&& mode == bind.mode;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(isKey, value, modifier, mode);
	}

	@Override
	public String toString() {
		return string;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("isKey", isKey);
		json.add("value", value + 1);
		json.add("modifier", modifier + 1);
		json.add("mode", mode.name());

		return json;
	}

	public static Bind fromJson(JsonObject json) {
		if (json == null) {
			return NONE;
		}

		return new Bind(
				json.getBoolean("isKey"),
				json.getInt("value") - 1,
				json.getInt("modifier") - 1,
				EnumUtils.valueFromName(Mode.values(), json.getString("mode")).orElse(Mode.TOGGLE)
		);
	}

	public static Bind fromMouseKey(int key, int modifier) {
		return new Bind(false, key, modifier, Mode.TOGGLE);
	}

	public static Bind fromMouseKey(int key) {
		return new Bind(false, key, -1, Mode.TOGGLE);
	}

	public static Bind fromKey(int key, int modifier) {
		return new Bind(true, key, modifier, Mode.TOGGLE);
	}

	public static Bind fromKey(int key) {
		return new Bind(true, key, -1, Mode.TOGGLE);
	}

	@AllArgsConstructor
	public enum Mode implements INameable {
		TOGGLE("Toggle"),
		HOLD("Hold"),
		INVERSE("Inverse");

		private final String name;

		@Override
		public String getName() {
			return name;
		}
	}
}
