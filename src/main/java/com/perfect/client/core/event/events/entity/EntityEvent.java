package com.perfect.client.core.event.events.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter
@AllArgsConstructor

public class EntityEvent {

	private final Entity entity;
}
