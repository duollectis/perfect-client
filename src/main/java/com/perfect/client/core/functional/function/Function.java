package com.perfect.client.core.functional.function;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.IEventListener;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.bind.BindListener;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.functional.function.helper.container.SetModeContainer;
import com.perfect.client.core.functional.function.helper.container.ToggleContainer;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.functional.function.helper.handler.IHandler;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.utils.INamedToggleable;
import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.core.utils.functions.FunctionUtils;
import com.perfect.client.core.utils.game.PacketUtils;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.perfect.client.PerfectClient.clientConfig;

public abstract class Function implements ISerializable<Function>, INamedToggleable, IEventListener {

	protected static final MinecraftClient mc = MinecraftClient.getInstance();

	@Getter
	private final String id;
	private final String name;

	@Getter
	private final Category category;

	@Getter
	private final String[] aliases;

	@Getter
	private final BindListener bindListener = new BindListener();

	private final List<IHandler> handlers = new ArrayList<>();

	@Getter
	protected final Settings settings = new Settings();

	private boolean enabled;

	public Function(String id, String name, Category category, String... aliases) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.aliases = aliases;

		bindListener.setToggleable(this);

		settings.reset();
	}

	public String getName() {
		return getName(PerfectClient.isInitialized() && clientConfig.getNameSpacings());
	}

	public String getName(boolean nameSpacings) {
		if (nameSpacings) {
			return name;
		}

		return name.replace(" ", "");
	}

	public String getStatus() {
		return "";
	}

	public String getTypeId() {
		return null;
	}

	public String getDescription() {
		return Translations.get("%s.desc".formatted(id));
	}

	@Override
	public void enable() {
		enable(true);
	}

	@Override
	public void disable() {
		disable(true);
	}

	@Override
	public void toggle() {
		toggle(true);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void toggle(boolean message) {
		if (enabled) {
			disable(message);
		}
		else {
			enable(message);
		}
	}

	public void enable(boolean message) {
		if (enabled) {
			return;
		}

		enabled = true;

		if (message) {
			sendToggledMessage();
		}

		onEnable();

		PerfectClient.EVENT_MANAGER.subscribe(this);

		handlers.forEach(IHandler::enable);
	}

	public void disable(boolean message) {
		if (enabled) {
			enabled = false;
			handlers.forEach(IHandler::disable);

			PerfectClient.EVENT_MANAGER.unsubscribe(this);

			onDisable();

			if (message) {
				sendToggledMessage();
			}
		}
	}

	public <T extends IHandler> T addHandler(T handler) {
		handlers.add(handler);
		return handler;
	}

	public <T extends Enum<T>, K extends FunctionHandler<?, ?>> ModeContainer<T, K> createModeContainer(
			Supplier<Setting<T>> settingSupplier
	) {
		return addHandler(new ModeContainer<>(() -> settingSupplier.get().get(), this));
	}

	public <T extends Enum<T>, K extends FunctionHandler<?, ?>> SetModeContainer<T, K> createListModeContainer(
			Supplier<Setting<Set<T>>> settingSupplier
	) {
		return addHandler(new SetModeContainer<>(() -> settingSupplier.get().get(), this));
	}

	public <T extends FunctionHandler<?, ?>> ToggleContainer<T> createToggleHandler() {
		return addHandler(new ToggleContainer<>(this));
	}

	public void info(Object info) {
		info(hashCode(), info);
	}

	public void info(int id, Object info) {
		ChatUtils.sendMsg(id, FunctionUtils.getInfoText(getName(), info.toString()));
	}

	public void error(String error) {
		ChatUtils.sendMsg(0, FunctionUtils.getErrorText(getName(), error));
	}

	private void sendToggledMessage() {
		ChatUtils.sendMsg(hashCode(), FunctionUtils.getToggledText(getName(), isEnabled()));
	}

	protected static void sendPacket(Packet<?> packet) {
		PacketUtils.sendPacket(packet);
	}

	protected static void sendSequencedPacket(SequencedPacketCreator creator) {
		PacketUtils.sendSequencedPacket(creator);
	}

	protected void onEnable() {}

	protected void onDisable() {}

	@Override
	public String getToggleableId() {
		return getName(false);
	}

	@Override
	public String getType() {
		return "function";
	}

	@Override
	public void sendBoundMessage(String message) {
		info(0, message);
	}

	public void resetOptions() {
		disable(false);
		bindListener.setBind(Bind.NONE);
		settings.reset();
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("name", getId());

		if (bindListener.getBind().isSet()) {
			json.add("bind", bindListener.getBind().toJson());
		}

		json.add("enabled", enabled);
		json.add("settings", settings.toJson());

		return json;
	}

	@Override
	public Function fromJson(JsonObject json) {
		bindListener.setBind(Bind.fromJson(json.getJsonObject("bind")));
		settings.fromJson(json.getJsonArray("settings"));

		boolean active = json.getBoolean("enabled");

		if (active != isEnabled()) {
			toggle(false);
		}

		return this;
	}

	@Override
	public int hashCode() {
		return ("function:" + getId()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Function function) {
			return function.getId().equals(getId());
		}

		return false;
	}
}
