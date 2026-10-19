package com.perfect.client.core.render.system.vertex.type;

import lombok.Getter;

import static org.lwjgl.opengl.GL40.*;

// Для всех поддерживаемых в современных версиях GL примитивов
// используется режим COMMON.
// Для остальных примитивов пишем форматирование в поддерживаемые.
//
// Поддерживаемые примитивы:
// GL_POINTS, GL_LINES, GL_LINE_STRIP, GL_LINE_LOOP,
// GL_TRIANGLES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN

public enum VertexDrawMode {
	POINTS(GL_POINTS),
	TRIANGLES(GL_TRIANGLES),
	TRIANGLE_FAN(GL_TRIANGLE_FAN),
	TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
	LINES(GL_LINES),
	LINE_STRIP(GL_LINE_STRIP),
	LINE_LOOP(GL_LINE_LOOP),
	QUADS(GL_TRIANGLES);

	@Getter
	private final int glMode;

	VertexDrawMode(int glMode) {
		this.glMode = glMode;
	}
}
