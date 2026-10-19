package com.perfect.client.core.event.events.chat;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

@Getter
@AllArgsConstructor
public class MessageReceiveEvent extends Cancellable {

	private Text message;
	private MessageIndicator indicator;
	private boolean modified;

	public void setMessage(Text message) {
		this.message = message;
		this.modified = true;
	}
}
