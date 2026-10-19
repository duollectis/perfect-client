package com.perfect.client.functional.function.visuals.holeEsp.data;

import com.perfect.client.core.utils.functions.hole.Hole;

import java.util.HashMap;
import java.util.List;


public record FindResult(
		List<Hole> holes,
		HashMap<HoleChunk, ChunkCache> cache
) {
}
