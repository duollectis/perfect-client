package com.perfect.client.core.event.events.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.chunk.Chunk;

@Getter
@AllArgsConstructor
public class ChunkDataEvent {

	private final Chunk chunk;
}
