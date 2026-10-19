package com.perfect.client.core.utils.files;

import com.perfect.client.core.utils.files.json.system.JsonObject;

public interface ISerializable<T> {

	JsonObject toJson();
	T fromJson(JsonObject json);
}
