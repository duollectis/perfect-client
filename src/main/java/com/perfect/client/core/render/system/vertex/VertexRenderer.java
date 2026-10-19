package com.perfect.client.core.render.system.vertex;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL40.*;

public class VertexRenderer {

	private FloatBuffer buffer = MemoryUtil.memAllocFloat(128);
	private final int vaoId;
	private final int vboId;

	public VertexRenderer() {
		vaoId = glGenVertexArrays();
		vboId = glGenBuffers();
	}

	
	
	public void drawVertices(VertexBuilder vertexBuilder) {
		float[] vertices = vertexBuilder.build();
		int activeSize = vertexBuilder.getActiveSize();

		if (activeSize == 0) {
			return;
		}

		if (buffer.capacity() < activeSize) {
			MemoryUtil.memFree(buffer);
			buffer = MemoryUtil.memAllocFloat(Math.max(buffer.capacity() * 2, activeSize));
		}

		buffer.clear();
		buffer.put(vertices, 0, activeSize).flip();

		glBindVertexArray(vaoId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

		int stride = vertexBuilder.getStride() * Float.BYTES;

		if (vertexBuilder.getPositionSize() > 0) {
			glVertexAttribPointer(vertexBuilder.getPositionId(), vertexBuilder.getPositionSize(), GL_FLOAT, false, stride, (long) vertexBuilder.getPositionOffset() * Float.BYTES);
			glEnableVertexAttribArray(vertexBuilder.getPositionId());
		}

		if (vertexBuilder.getTexCoordSize() > 0) {
			glVertexAttribPointer(vertexBuilder.getTexCoordId(), vertexBuilder.getTexCoordSize(), GL_FLOAT, false, stride, (long) vertexBuilder.getTexCoordOffset() * Float.BYTES);
			glEnableVertexAttribArray(vertexBuilder.getTexCoordId());
		}

		if (vertexBuilder.getNormalSize() > 0) {
			glVertexAttribPointer(vertexBuilder.getNormalId(), vertexBuilder.getNormalSize(), GL_FLOAT, false, stride, (long) vertexBuilder.getNormalOffset() * Float.BYTES);
			glEnableVertexAttribArray(vertexBuilder.getNormalId());
		}

		if (vertexBuilder.getColorSize() > 0) {
			glVertexAttribPointer(vertexBuilder.getColorId(), vertexBuilder.getColorSize(), GL_FLOAT, false, stride, (long) vertexBuilder.getColorOffset() * Float.BYTES);
			glEnableVertexAttribArray(vertexBuilder.getColorId());
		}

		glDrawArrays(vertexBuilder.getGlMode(), 0, activeSize / vertexBuilder.getStride());

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public void delete() {
		MemoryUtil.memFree(buffer);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboId);
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
}