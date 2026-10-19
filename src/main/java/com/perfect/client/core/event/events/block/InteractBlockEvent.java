package com.perfect.client.core.event.events.block;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

@Getter
@AllArgsConstructor

public class InteractBlockEvent extends Cancellable {

	private final Hand hand;
	private final BlockHitResult hitResult;
}
