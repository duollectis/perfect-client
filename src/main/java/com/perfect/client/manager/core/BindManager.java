package com.perfect.client.manager.core;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.manager.Managers;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BindManager extends Manager {

	private final List<AbstractBindListener> bindListeners = new ArrayList<>();

	@Setter
	private boolean waitEnterRelease;

	@Setter
	@Getter
	private boolean waitMouseRelease;

	@Setter
	private boolean setFlags;

	@Getter
	@Setter
	private Bind.Mode setMode = Bind.Mode.TOGGLE;

	@Override
	public void onStartRenderTick() {
		if (waitMouseRelease && !Managers.KEY_STATE.isAnyMousePressed()) {
			waitMouseRelease = false;
		}
	}

	public void add(AbstractBindListener bindListener) {
		if (PerfectClient.isInitialized()) {
			throw new RuntimeException("Can't add bind listener after initialization!");
		}

		bindListeners.add(bindListener);
	}

	public List<AbstractBindListener> getListeners() {
		return bindListeners;
	}

	@Override
	public void onBindManagerKey(KeyEvent event) {
		if (event.isRepeat()) {
			return;
		}

		if (waitEnterRelease) {
			if (event.isRelease()) {
				if (event.getValue() == GLFW.GLFW_KEY_ENTER || event.getValue() == GLFW.GLFW_KEY_KP_ENTER) {
					waitEnterRelease = false;
				}
			}

			return;
		}

		bindListeners.forEach(listener -> listener.onKey(event));
	}

	@Override
	public void onBindManagerMouseButton(MouseButtonEvent event) {
		bindListeners.forEach(listener -> listener.onMouse(event));
	}

	public boolean isSetFlags() {
		boolean _set = setFlags;
		setFlags = false;
		return _set;
	}
}
