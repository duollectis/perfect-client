package com.perfect.client.core.render.font.utils;

import com.perfect.client.core.render.font.core.Bounds;
import com.perfect.client.core.render.font.core.Glyph;
import com.perfect.client.core.render.font.core.Metrics;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class FontUtils {

	
	public static Metrics parseMetrics(JsonObject json) {
		JsonObject atlas = json.getJsonObject("atlas");

		double width = atlas.getDouble("width");
		double height = atlas.getDouble("height");

		JsonObject metrics = json.getJsonObject("metrics");

		double emSize = metrics.getDouble("emSize");
		double lineHeight = metrics.getDouble("lineHeight");
		double ascender = metrics.getDouble("ascender");
		double descender = metrics.getDouble("descender");
		double underlineY = metrics.getDouble("underlineY");
		double underlineThickness = metrics.getDouble("underlineThickness");

		return new Metrics(
				(float) width,
				(float) height,
				(float) emSize,
				(float) lineHeight,
				(float) ascender,
				(float) descender,
				(float) underlineY,
				(float) underlineThickness
		);
	}

	
	public static Map<Integer, Glyph> parseGlyphs(JsonObject json) {
		Map<Integer, Glyph> glyphs = new HashMap<>();

		JsonArray glyphArray = json.getJsonArray("glyphs");

		for (JsonElement<?> element : glyphArray) {
			JsonObject glyphObject = element.asJsonObject();

			int unicode = glyphObject.getInt("unicode");

			double advance = glyphObject.getDouble("advance");

			JsonObject planeBounds = glyphObject.getJsonObject("planeBounds");


			double planeLeft = planeBounds.getDouble("left");
			double planeBottom = planeBounds.getDouble("bottom");
			double planeRight = planeBounds.getDouble("right");
			double planeTop = planeBounds.getDouble("top");

			Bounds plane = new Bounds((float) planeLeft, (float) planeRight, (float) planeBottom, (float) planeTop);

			JsonObject atlasBounds = glyphObject.getJsonObject("atlasBounds");
			
			double atlasLeft = atlasBounds.getDouble("left");
			double atlasBottom = atlasBounds.getDouble("bottom");
			double atlasRight = atlasBounds.getDouble("right");
			double atlasTop = atlasBounds.getDouble("top");

			Bounds atlas = new Bounds((float) atlasLeft, (float) atlasRight, (float) atlasBottom, (float) atlasTop);

			Glyph glyph = new Glyph((float) advance, plane, atlas);
			glyphs.put(unicode, glyph);
		}

		return glyphs;
	}
}
