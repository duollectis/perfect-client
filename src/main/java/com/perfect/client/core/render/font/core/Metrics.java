package com.perfect.client.core.render.font.core;

public record Metrics(
		float atlasWidth,
		float atlasHeight,
		float emSize,
		float lineHeight,
		float ascender,
		float descender,
		float underlineY,
		float underlineThickness
) {
}
