package com.perfect.client.core.functional.bind;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.INamedToggleable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static com.perfect.client.PerfectClient.mc;

@Getter
@Setter
public abstract class AbstractBindListener {

	private boolean enableOnScreens;

	@Nullable
	private INamedToggleable toggleable;

	private boolean binding;
	private BindAction action;

	private Consumer<Bind> boundMessageAction;
	private Runnable unboundMessageAction;

	public abstract void setBind(Bind bind);
	public abstract Bind getBind();
	public abstract boolean isLock();

	public void setToggleable(INamedToggleable toggleable) {
		this.toggleable = toggleable;
		updateToggleableAction();
	}

	private void updateToggleableAction() {
		setAction((bind, event) -> {
			if (toggleable == null) {
				return;
			}

			switch (bind.getMode()) {
				case TOGGLE -> {
					if (event.isPress()) {
						toggleable.toggle();
					}
				}
				case HOLD -> {
					if (event.isPress() || toggleable.isEnabled()) {
						toggleable.toggle();
					}
				}
				case INVERSE -> {
					if (event.isPress() || !toggleable.isEnabled()) {
						toggleable.toggle();
					}
				}
			}
		});
	}

	public void onKey(KeyEvent event) {
		if (binding) {
			event.cancel();
			handleBinding(event);
		}
		else {
			run(true, event);
		}
	}

	private void handleBinding(KeyEvent event) {
		if (event.getValue() == GLFW.GLFW_KEY_ESCAPE) {
			if (event.isPress()) {
				setBind(Bind.NONE.withMode(getBind()));
				binding = false;
			}

			unboundMessage();

			event.cancel();
			return;
		}

		int mod = -1;

		for (int key : Managers.KEY_STATE.collectPressedKeyboard()) {
			if (key != event.getValue()) {
				mod = key;
				break;
			}
		}

		if (mod != -1 || event.isRelease()) {
			bind(event, mod);
		}
	}

	public void onMouse(MouseButtonEvent event) {
		if (binding) {
			if (event.isPress()) {
				event.cancel();

				int mod = -1;

				for (int key : Managers.KEY_STATE.collectPressedKeyboard()) {
					mod = key;
					break;
				}

				bind(event, mod);

				Managers.BIND.setWaitMouseRelease(true);
			}
		}
		else {
			run(false, event);
		}
	}

	private void bind(InputEvent event, int mod) {
		setBind(new Bind(
				event.isKey(),
				event.getValue(),
				mod,
				Managers.BIND.isSetFlags() ? Managers.BIND.getSetMode() : Bind.Mode.TOGGLE
		));

		binding = false;
		boundMessage();
	}

	private void boundMessage() {
		if (boundMessageAction != null) {
			boundMessageAction.accept(getBind());
			boundMessageAction = null;
		}
	}

	private void unboundMessage() {
		if (unboundMessageAction != null) {
			unboundMessageAction.run();
			unboundMessageAction = null;
		}
	}

	private void run(boolean isKey, InputEvent event) {
		if (action == null || isLock()) {
			return;
		}

		if (event.isPress()) {
			if (Managers.KEY_STATE.isKeyboardPressed(GLFW.GLFW_KEY_F3)) {
				return;
			}

			if (!enableOnScreens && mc.currentScreen != null) {
				return;
			}
		}

		Bind bind = getBind();

		if (checkPressAction(event, isKey, bind, this) || checkReleaseAction(event, isKey, bind)) {
			action.run(bind, event);
		}
	}

	private static boolean checkReleaseAction(InputEvent event, boolean isKey, Bind bind) {
		return event.isRelease() && bind.matches(isKey, event.getValue());
	}

	private static boolean checkPressAction(
			InputEvent event,
			boolean isKey,
			Bind bind,
			AbstractBindListener checkingBindListener
	) {
		if (event.isNotPress()) {
			return false;
		}

		return bind.matchesWithMod(isKey, event.getValue()) && checkBindsModifierNotPressed(bind, checkingBindListener);
	}

	private static boolean checkBindsModifierNotPressed(Bind bind, AbstractBindListener checkingBindListener) {
		if (bind.getModifier() == -1) {
			for (AbstractBindListener bindListener : Managers.BIND.getListeners()) {
				if (bindListener == checkingBindListener) {
					continue;
				}

				Bind checkingBind = bindListener.getBind();

				if (checkingBind.getModifier() != -1
						&& checkingBind.getValue() == bind.getValue()
						&& checkingBind.isModDefinedAndPressed()
				) {
					return false;
				}
			}
		}

		return true;
	}
}
