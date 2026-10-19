package com.perfect.client.core.event.events.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;

@Getter
@AllArgsConstructor

public class PlayerEvent {

	private final PlayerEntity player;
	private final int playerChatId;
	private final int poppedTotemsCount;
}