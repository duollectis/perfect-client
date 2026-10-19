package com.perfect.client.core.render;

import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import lombok.Getter;

public class RenderPackets {

	public static final VertexBuilder BOTTOM_FONT_LAYER = new VertexBuilder(
			VertexFormats.POSITION_TEXTURE_COLOR,
			VertexDrawMode.QUADS
	);

	public static final VertexBuilder NAMETAGS_FONT_LAYER = new VertexBuilder(
			VertexFormats.POSITION_TEXTURE_COLOR,
			VertexDrawMode.QUADS
	);

	@Getter
	private static VertexBuilder currentPacket;

	public static void setDrawPacket(VertexBuilder packet) {
		currentPacket = packet;
	}

	public static boolean isPackingEnabled() {
		return currentPacket != null;
	}

	public static void disablePacking() {
		currentPacket = null;
	}
}
