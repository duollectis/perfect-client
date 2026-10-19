package com.perfect.client.core.utils;

public interface INamedToggleable extends IToggleable {

	String getToggleableId();
	String getType();
	void sendBoundMessage(String message);
}
