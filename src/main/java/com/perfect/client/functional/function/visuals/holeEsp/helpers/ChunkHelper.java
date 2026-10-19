package com.perfect.client.functional.function.visuals.holeEsp.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.game.world.ChunkUtils;
import com.perfect.client.functional.function.visuals.holeEsp.HoleESP;
import com.perfect.client.functional.function.visuals.holeEsp.HoleEspConfig;
import com.perfect.client.functional.function.visuals.holeEsp.data.HoleChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChunkHelper extends FunctionHelper<HoleESP, HoleEspConfig> {

	public ChunkHelper(HoleESP function, HoleEspConfig config) {
		super(function, config);
	}

	public List<HoleChunk> getChunks() {
		float rangeXZ = config.getRangeXZ().getFloat();
		float rangeY = config.getRangeY().getFloat();
		return getChunks(rangeXZ, rangeY);
	}

	private List<HoleChunk> getChunks(float rangeXZ, float rangeY) {
		if (mc.player == null || mc.world == null) {
			return Collections.emptyList();
		}

		int blockRangeXZ = Math.round(rangeXZ);
		int blockRangeY = Math.round(rangeY);

		BlockPos startBlock = mc.player.getBlockPos().add(-blockRangeXZ, -blockRangeY, -blockRangeXZ);
		BlockPos endBlock = mc.player.getBlockPos().add(blockRangeXZ, blockRangeY, blockRangeXZ);

		Vec3i start = ChunkUtils.blockPosToChunkPos(startBlock);
		Vec3i end = ChunkUtils.blockPosToChunkPos(endBlock);

		List<HoleChunk> chunks = new ArrayList<>();

		for (int x = start.getX(); x <= end.getX(); x++) {
			for (int y = start.getY(); y <= end.getY(); y++) {
				for (int z = start.getZ(); z <= end.getZ(); z++) {

					if (mc.world.getChunkManager().isChunkLoaded(x, z)) {
						HoleChunk chunk = new HoleChunk(x, y, z);
						Utils.add(chunks, chunk);
					}
				}
			}
		}

		return chunks;
	}
}
