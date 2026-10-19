package com.perfect.client.functional.function.visuals.blockEsp;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.event.events.world.ChunkDataEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.BlockColor;
import com.perfect.client.core.utils.color.Color4f;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.functional.function.visuals.blockEsp.utils.ESPChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;


public class BlockESP extends Function {

	private final BlockEspConfig config = new BlockEspConfig(this);

	private final ConcurrentHashMap<Long, ESPChunk> chunks = new ConcurrentHashMap<>();

	public BlockESP() {
		super("block-esp", "Block ESP", Category.VISUALS, "search", "xray");
	}

	@Override
	public void onEnable() {
		rescan();
	}

	public void rescan() {
		Managers.ASYNC.run(() -> {
					chunks.clear();

					if (mc.world == null) {
						return;
					}

					for (int i = 0; i < mc.world.getChunkManager().chunks.chunks.length(); i++) {
						Chunk chunk = mc.world.getChunkManager().chunks.chunks.get(i);

						if (chunk != null) {
							scanChunk(chunk);
						}
					}
				}
		);
	}

	@Override
	public void onChunkData(ChunkDataEvent event) {
		if (mc.world == null) {
			return;
		}

		scanChunk(event.getChunk());
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		Managers.ASYNC.run(() -> {
			BlockPos pos = event.getPos();

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			int chunkX = x >> 4;
			int chunkZ = z >> 4;

			long key = ESPChunk.getKey(chunkX, chunkZ);

			ESPChunk chunk = chunks.get(key);
			chunk.removeBlock(x, y, z);

			Block block = BlockUtils.getBlock(pos);

			if (config.getBlocks().get(block).isEnabled()) {
				Box box = BlockUtils.getOutlineShapeBox(pos);
				chunk.addBlock(block, box, x, y, z);
			}
		});
	}

	private void scanChunk(Chunk chunk) {
		Managers.ASYNC.run(() -> {
			scanChunkIternal(chunk);
		});
	}

	private void scanChunkIternal(Chunk chunk) {
		if (mc.world == null) {
			return;
		}

		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;

		long key = ESPChunk.getKey(chunkX, chunkZ);

		ESPChunk espChunk = new ESPChunk(chunkX, chunkZ);

		BlockPos.Mutable blockPos = new BlockPos.Mutable();

		int bottomY = mc.world.getBottomY();
		int topY = mc.world.getTopYInclusive();

		int startBlockX = chunkX << 4;
		int startBlockZ = chunkZ << 4;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int blockY = bottomY; blockY <= topY; blockY++) {

					int blockX = startBlockX + x;
					int blockZ = startBlockZ + z;

					blockPos.set(blockX, blockY, blockZ);

					BlockState state = chunk.getBlockState(blockPos);
					Block block = state.getBlock();

					if (config.getBlocks().get(block).isEnabled()) {
						Box box = BlockUtils.getOutlineShapeBox(blockPos);
						espChunk.addBlock(block, box, blockX, blockY, blockZ);
					}
				}
			}
		}

		chunks.put(key, espChunk);
	}

	@Override
	public void onPostTick() {
		Managers.ASYNC.run(() -> {
			chunks.values().removeIf(ESPChunk::isOutOfRenderDistance);
		});
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		MatrixStack matrices = event.getMatrices();

		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		matrices.push();
		matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

		Matrix4f matrix = matrices.peek().getPositionMatrix();

		float lineOpacity = (float) config.getLineOpacity().fromPercent();
		float sideOpacity = (float) config.getSideOpacity().fromPercent();
		float tracerOpacity = (float) config.getTracerOpacity().fromPercent();

		Color defaultColor = Managers.COLOR.get(Managers.COLOR.MAIN);

		Color4f defaultLineColor = new Color4f(ColorUtils.multiplyAlpha(defaultColor, lineOpacity));
		Color4f defaultSideColor = new Color4f(ColorUtils.multiplyAlpha(defaultColor, sideOpacity));
		Color4f defaultTracerColor = new Color4f(ColorUtils.multiplyAlpha(defaultColor, tracerOpacity));

		chunks.values().forEach(chunk -> chunk.forEach(block -> {
			BlockColor blockColor = config.getBlocks().get(block.block);

			Color4f lineColor;
			Color4f sideColor;
			Color4f tracerColor;

			if (blockColor.isSet()) {
				Color color = blockColor.getColor();

				lineColor = new Color4f(ColorUtils.multiplyAlpha(color, lineOpacity));
				sideColor = new Color4f(ColorUtils.multiplyAlpha(color, sideOpacity));
				tracerColor = new Color4f(ColorUtils.multiplyAlpha(color, tracerOpacity));
			}
			else {
				lineColor = defaultLineColor;
				sideColor = defaultSideColor;
				tracerColor = defaultTracerColor;
			}

			block.draw(
					matrix,
					lineColor,
					sideColor
			);

			if (config.getTracers().get()) {
				drawTracer(matrix, block.box.getCenter(), tracerColor);
			}
		}));

		matrices.pop();
	}

	private void drawTracer(Matrix4f matrix, Vec3d center, Color4f color) {
		VertexBuilder builder = Managers.RENDER.getGeneralBoxOutlineBuffer().getVertexBuilder();

		Vec3d screenCenter = Renderer3D.screenCenter;

		float x0 = (float) screenCenter.getX();
		float y0 = (float) screenCenter.getY();
		float z0 = (float) screenCenter.getZ();

		float x1 = (float) center.getX();
		float y1 = (float) center.getY();
		float z1 = (float) center.getZ();

		builder.pos(matrix, x0, y0, z0).color(color);
		builder.pos(matrix, x1, y1, z1).color(color);
	}
}
