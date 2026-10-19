package com.perfect.client.manager.system;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.functional.ClientConfig;

import static com.perfect.client.PerfectClient.clientConfig;

public class ClientConfigManager extends Manager implements ISystem<ClientConfigManager> {

	private final ClientConfig config;

	public ClientConfigManager() {
		config = new ClientConfig();
		clientConfig = config;
	}

	@Override
	public String getId() {
		return "client_config";
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("settings", config.getSettings().toJson());
		return json;
	}

	@Override
	public ClientConfigManager fromJson(JsonObject json) {
		config.getSettings().fromJson(json.getJsonArray("settings"));
		return this;
	}

	@Override
	public void reset() {
		config.getSettings().reset();
	}
}
