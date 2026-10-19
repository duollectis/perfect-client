package com.perfect.client.functional.function.visuals.blockEsp.utils;

import com.perfect.client.core.utils.RenderUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.function.Consumer;

import static com.perfect.client.PerfectClient.mc;

public class ESPChunk {

	private final int x, z;

	private Long2ObjectMap<ESPBlock> blocks;

	public ESPChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public void addBlock(Block block, Box box, int x, int y, int z) {
		if (box == null) {
			return;
		}

		ESPBlock espBlock = new ESPBlock(block, box, x, y, z);

		if (blocks == null) {
			blocks = new Long2ObjectOpenHashMap<>(64);
		}

		long key = ESPBlock.getKey(x, y, z);
		blocks.put(key, espBlock);
	}

	public void removeBlock(int x, int y, int z) {
		if (blocks != null) {
			long key = ESPBlock.getKey(x, y, z);
			blocks.remove(key);
		}
	}

	public boolean isOutOfRenderDistance() {
		if (mc.player == null) {
			return false;
		}

		int viewDist = RenderUtils.getRenderDistance() + 1;

		int chunkX = ChunkSectionPos.getSectionCoord(mc.player.getBlockPos().getX());
		int chunkZ = ChunkSectionPos.getSectionCoord(mc.player.getBlockPos().getZ());

		return x > chunkX + viewDist || x < chunkX - viewDist || z > chunkZ + viewDist || z < chunkZ - viewDist;
	}

	public void forEach(Consumer<ESPBlock> blockConsumer) {
		if (blocks != null) {
			blocks.values().forEach(blockConsumer);
		}
	}

	public static long getKey(int chunkX, int chunkZ) {
		return ChunkPos.toLong(chunkX, chunkZ);
	}
}
