package com.perfect.client.core.event.events.entity;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter
@AllArgsConstructor

public class AttackEntityEvent extends Cancellable {

	private final Entity entity;
}
