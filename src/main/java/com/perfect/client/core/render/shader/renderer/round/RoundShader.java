package com.perfect.client.core.render.shader.renderer.round;

import com.perfect.client.gui.utils.Coloring;
import com.perfect.client.gui.utils.MatrixUtils;
import com.perfect.client.gui.utils.Point;
import com.perfect.client.gui.utils.Rounding;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.awt.*;


public class RoundShader extends AbstractRoundShader {

	public void draw(
			Matrix3x2fStack matrices,
			float x, float y, float width, float height,
			Rounding rounding, float thickness, float softness,
			float alpha, float outlineAlpha,
			Color outline,
			Coloring coloring
	) {
		Matrix3x2f matrix = new Matrix3x2f(matrices);

		Point pos0 = MatrixUtils.scale(matrix, x, y);
		Point pos1 = MatrixUtils.scale(matrix, x + width, y + height);

		setLocationAndSizeUniforms(pos0, pos1);
		setSoftnessAndThicknessUniforms(matrix, softness, thickness);

		setColorsAndAlphaUniforms(
				alpha,
				outlineAlpha,
				coloring,
				outline
		);

		float s = MatrixUtils.getFinalScale(matrix);

		setRoundingUniform(rounding, s);

		drawVertices(pos0, pos1, softness * s, false);
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform vec3 color1;
				uniform vec3 color2;
				uniform vec3 color3;
				uniform vec3 color4;
				
				uniform float alpha;
				
				uniform vec4 outlineColor;
				uniform vec2 size;
				uniform vec2 location;
				uniform vec4 radius;
				uniform float thickness;
				uniform float softness;
				
				out vec4 fragColor;
				
				"""
				+ roundedBoxSDF()
				+ createGradient()
				+ """
				
				void main() {
				    float distance = roundedBoxSDF(gl_FragCoord.xy - location - (size / 2.0), size / 2.0, radius);
				
				    float smoothedAlpha = 1.0 - smoothstep(-1.0, thickness > 0. ? 1. : softness + 1., distance);
				    float smoothedborderAlpha = (1.0 - smoothstep(-softness,  softness, distance));
				
				    float borderAlpha = 1.0 - smoothstep(thickness - 2.0, thickness, abs(distance));
				
				    if(smoothedAlpha < 0.49 && thickness > 0.){
				        fragColor = vec4(outlineColor.rgb, smoothedborderAlpha);
				    } else {
				        vec3 gradient = createGradient((gl_FragCoord.xy - location) / size, color1, color2, color3, color4);
				        vec4 basicColor = vec4(gradient.rgb, alpha * smoothedAlpha);
				
				        fragColor = mix(vec4(gradient, 0.), mix(basicColor, thickness > 0. ? outlineColor : basicColor, borderAlpha), smoothedAlpha);
				    }
				}
				""";
	}
}
