package com.perfect.client.core.render.shader.renderer.round;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.gui.utils.Coloring;
import com.perfect.client.gui.utils.MatrixUtils;
import com.perfect.client.gui.utils.Point;
import com.perfect.client.gui.utils.Rounding;
import org.joml.Matrix3x2f;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.color.ColorUtils.getArray3f;
import static com.perfect.client.core.utils.color.ColorUtils.getArray4f;


public abstract class AbstractRoundShader extends Shader {

	public AbstractRoundShader() {
		super(VertexFormats.POSITION_TEXTURE, VertexDrawMode.QUADS);
	}

	protected void setColorsAndAlphaUniforms(
			float alpha,
			float outlineAlpha,
			Coloring coloring,
			Color outline
	) {
		shaderProgram.setUniform("color1", getArray3f(coloring.getBottomLeft()));
		shaderProgram.setUniform("color2", getArray3f(coloring.getTopLeft()));
		shaderProgram.setUniform("color3", getArray3f(coloring.getBottomRight()));
		shaderProgram.setUniform("color4", getArray3f(coloring.getTopRight()));

		Color outlineColor = ColorUtils.applyAlpha(outline, outlineAlpha * Renderer2D.getDrawOpacity());
		shaderProgram.setUniform("outlineColor", getArray4f(outlineColor));

		shaderProgram.setUniform("alpha", alpha * Renderer2D.getDrawOpacity());
	}

	protected void setRoundingUniform(Rounding rounding, float s) {
		shaderProgram.setUniform(
				"radius",
				rounding.getTopRight() * s,
				rounding.getBottomRight() * s,
				rounding.getTopLeft() * s,
				rounding.getBottomLeft() * s
		);
	}

	protected void setLocationAndSizeUniforms(Point pos0, Point pos1) {
		float w = pos1.getX() - pos0.getX();
		float h = pos1.getY() - pos0.getY();

		float scale = (float) mc.getWindow().getScaleFactor();

		float x = pos0.getX() * scale;
		float y = mc.getWindow().getHeight() - (pos0.getY() + h) * scale;

		shaderProgram.setUniform("size", w * scale, h * scale);
		shaderProgram.setUniform("location", x, y);
	}

	protected void setSoftnessAndThicknessUniforms(Matrix3x2f matrix, float softness, float thickness) {
		float windowScale = (float) mc.getWindow().getScaleFactor();
		float matrixScale = MatrixUtils.getMatrixScaleFactor(matrix);

		shaderProgram.setUniform("thickness", thickness * windowScale * matrixScale);
		shaderProgram.setUniform("softness", softness * windowScale * matrixScale);
	}

	protected void drawVertices(Point pos0, Point pos1, float offset, boolean texture) {
		float x = pos0.getX() - offset;
		float y = pos0.getY() - offset;
		float x1 = pos1.getX() + offset;
		float y1 = pos1.getY() + offset;

		VertexBuilder vertexBuilder = getVertexBuilder();

		if (texture) {
			vertexBuilder.pos(x, y).screenTex(x, y);
			vertexBuilder.pos(x1, y).screenTex(x1, y);
			vertexBuilder.pos(x1, y1).screenTex(x1, y1);
			vertexBuilder.pos(x, y1).screenTex(x, y1);
		}
		else {
			vertexBuilder.pos(x, y).tex(0, 0);
			vertexBuilder.pos(x1, y).tex(1, 0);
			vertexBuilder.pos(x1, y1).tex(1, 1);
			vertexBuilder.pos(x, y1).tex(0, 1);
		}

		Renderer2D.preRender();
		vertexBuilder.draw(shaderProgram);
		Renderer2D.postRender();
	}

	protected String roundedBoxSDF() {
		return """
				float roundedBoxSDF(vec2 center, vec2 size, vec4 radius) {
				    radius.xy = (center.x > 0.0) ? radius.xy : radius.zw;
				    radius.x = (center.y > 0.0) ? radius.x : radius.y;
				
				    vec2 q = abs(center) - size + radius.x;
				    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius.x;
				}
				""";
	}

	protected String createGradient() {
		return """
				vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4) {
				    vec3 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);
				    color += mix(0.0019607843, -0.0019607843, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
				    return color;
				}
				""";
	}
}
