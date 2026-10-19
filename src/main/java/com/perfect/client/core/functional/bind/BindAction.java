package com.perfect.client.core.functional.bind;

import com.perfect.client.core.event.events.input.InputEvent;

public interface BindAction {

	void run(Bind bind, InputEvent event);
}