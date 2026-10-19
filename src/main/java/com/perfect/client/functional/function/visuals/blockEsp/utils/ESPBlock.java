package com.perfect.client.functional.function.visuals.blockEsp.utils;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.Color4f;
import net.minecraft.block.Block;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public class ESPBlock {

	public final Block block;
	public final Box box;
	public final int x, y, z;

	public ESPBlock(Block block, Box box, int x, int y, int z) {
		this.block = block;
		this.box = box;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static long getKey(int x, int y, int z) {
		return ((long) y << 16) | ((long) (z & 15) << 8) | ((long) (x & 15));
	}

	public void draw(
			Matrix4f matrix,
			Color4f lineColor,
			Color4f sideColor
	) {
		drawFill(matrix, sideColor);
		drawOutline(matrix, lineColor);
	}

	private void drawFill(Matrix4f matrix, Color4f color) {
		VertexBuilder builder = Managers.RENDER.getGeneralBoxFillBuffer().getVertexBuilder();

		float x1 = (float) (box.minX);
		float y1 = (float) (box.minY);
		float z1 = (float) (box.minZ);
		float x2 = (float) (box.maxX);
		float y2 = (float) (box.maxY);
		float z2 = (float) (box.maxZ);

		builder.pos(matrix, x1, y1, z1).color(color);
		builder.pos(matrix, x2, y1, z1).color(color);
		builder.pos(matrix, x2, y1, z2).color(color);
		builder.pos(matrix, x1, y1, z2).color(color);

		builder.pos(matrix, x1, y1, z1).color(color);
		builder.pos(matrix, x1, y2, z1).color(color);
		builder.pos(matrix, x2, y2, z1).color(color);
		builder.pos(matrix, x2, y1, z1).color(color);

		builder.pos(matrix, x2, y1, z1).color(color);
		builder.pos(matrix, x2, y2, z1).color(color);
		builder.pos(matrix, x2, y2, z2).color(color);
		builder.pos(matrix, x2, y1, z2).color(color);

		builder.pos(matrix, x1, y1, z2).color(color);
		builder.pos(matrix, x2, y1, z2).color(color);
		builder.pos(matrix, x2, y2, z2).color(color);
		builder.pos(matrix, x1, y2, z2).color(color);

		builder.pos(matrix, x1, y1, z1).color(color);
		builder.pos(matrix, x1, y1, z2).color(color);
		builder.pos(matrix, x1, y2, z2).color(color);
		builder.pos(matrix, x1, y2, z1).color(color);

		builder.pos(matrix, x1, y2, z1).color(color);
		builder.pos(matrix, x1, y2, z2).color(color);
		builder.pos(matrix, x2, y2, z2).color(color);
		builder.pos(matrix, x2, y2, z1).color(color);
	}

	private void drawOutline(Matrix4f matrix, Color4f color) {
		VertexBuilder builder = Managers.RENDER.getGeneralBoxOutlineBuffer().getVertexBuilder();

		float x1 = (float) (box.minX);
		float y1 = (float) (box.minY);
		float z1 = (float) (box.minZ);
		float x2 = (float) (box.maxX);
		float y2 = (float) (box.maxY);
		float z2 = (float) (box.maxZ);

		builder.pos(matrix, x1, y1, z1).color(color);
		builder.pos(matrix, x1, y1, z2).color(color);

		builder.pos(matrix, x1, y1, z2).color(color);
		builder.pos(matrix, x2, y1, z2).color(color);

		builder.pos(matrix, x2, y1, z2).color(color);
		builder.pos(matrix, x2, y1, z1).color(color);

		builder.pos(matrix, x2, y1, z1).color(color);
		builder.pos(matrix, x1, y1, z1).color(color);

		builder.pos(matrix, x1, y1, z1).color(color);
		builder.pos(matrix, x1, y2, z1).color(color);

		builder.pos(matrix, x1, y2, z1).color(color);
		builder.pos(matrix, x1, y2, z2).color(color);

		builder.pos(matrix, x1, y2, z2).color(color);
		builder.pos(matrix, x2, y2, z2).color(color);

		builder.pos(matrix, x2, y2, z2).color(color);
		builder.pos(matrix, x2, y2, z1).color(color);

		builder.pos(matrix, x2, y2, z1).color(color);
		builder.pos(matrix, x1, y2, z1).color(color);

		builder.pos(matrix, x2, y1, z1).color(color);
		builder.pos(matrix, x2, y2, z1).color(color);

		builder.pos(matrix, x2, y1, z2).color(color);
		builder.pos(matrix, x2, y2, z2).color(color);

		builder.pos(matrix, x1, y1, z2).color(color);
		builder.pos(matrix, x1, y2, z2).color(color);
	}
}
