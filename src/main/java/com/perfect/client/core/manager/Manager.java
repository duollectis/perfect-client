package com.perfect.client.core.manager;

import com.perfect.client.core.event.system.EventListener;
import net.minecraft.client.MinecraftClient;

public abstract class Manager extends EventListener {

	private static int idCounter = 0;

	protected static final MinecraftClient mc = MinecraftClient.getInstance();

	private final String managerId;

	public Manager() {
		super();

		managerId = "Manager:%s".formatted(idCounter);
		idCounter++;
	}

	@Override
	public int hashCode() {
		return managerId.hashCode();
	}
}
