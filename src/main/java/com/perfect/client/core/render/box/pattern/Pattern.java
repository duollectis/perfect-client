package com.perfect.client.core.render.box.pattern;

import com.perfect.client.core.render.box.BoxColors;
import com.perfect.client.core.render.box.pattern.impl.CagePattern;
import com.perfect.client.core.render.box.pattern.impl.EmptyPattern;
import com.perfect.client.core.render.box.pattern.impl.WebPattern;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.text.ITranslatable;
import org.joml.Matrix4f;

public enum Pattern implements ITranslatable {
	NONE(new EmptyPattern()),
	WEB(new WebPattern()),
	CAGE(new CagePattern());

	private final IPattern pattern;

	Pattern(IPattern pattern) {
		this.pattern = pattern;
	}

	public void render(
			VertexBuilder builder, Matrix4f matrix,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			BoxColors colors
	) {
		pattern.render(builder, matrix, x1, y1, z1, x2, y2, z2, colors);
	}

	@Override
	public String getTranslationId() {
		return "pattern";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
