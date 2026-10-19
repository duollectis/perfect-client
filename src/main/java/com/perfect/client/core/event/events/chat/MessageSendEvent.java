package com.perfect.client.core.event.events.chat;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MessageSendEvent extends Cancellable {

	private String message;
}
