package com.perfect.client.core.render.system.vertex.type;

import com.perfect.client.core.render.system.vertex.VertexSources;

public class VertexFormats {

	public static VertexFormat BLIT_SCREEN = new VertexFormat(
			VertexSources.BLIT_SCREEN,
			true, true, false, false
	);

	public static VertexFormat BLIT_SCREEN_LIGHT_MAP = new VertexFormat(
			VertexSources.BLIT_SCREEN_LIGHT_MAP,
			true, false, false, false
	);

	public static VertexFormat POSITION = new VertexFormat(
			VertexSources.POSITION,
			true, false, false, false
	);

	public static VertexFormat POSITION_TEXTURE = new VertexFormat(
			VertexSources.POSITION_TEXTURE,
			true, true, false, false
	);

	public static VertexFormat POSITION_COLOR = new VertexFormat(
			VertexSources.POSITION_COLOR,
			true, false, false, true
	);

	public static VertexFormat POSITION_TEXTURE_COLOR = new VertexFormat(
			VertexSources.POSITION_TEXTURE_COLOR,
			true, true, false, true
	);
}
