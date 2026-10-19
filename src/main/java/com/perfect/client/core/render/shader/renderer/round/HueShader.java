package com.perfect.client.core.render.shader.renderer.round;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.Orientation;
import com.perfect.client.gui.utils.MatrixUtils;
import com.perfect.client.gui.utils.Point;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;


public class HueShader extends AbstractRoundShader {

	public void draw(
			Matrix3x2fStack matrices,
			float x,
			float y,
			float width,
			float height,
			float radius,
			float softness,
			float alpha,
			float hueOffset,
			Orientation orientation
	) {
		Matrix3x2f matrix = new Matrix3x2f(matrices);

		Point pos0 = MatrixUtils.scale(matrix, x, y);
		Point pos1 = MatrixUtils.scale(matrix, x + width, y + height);

		setLocationAndSizeUniforms(pos0, pos1);

		float s = MatrixUtils.getFinalScale(matrix);
		float r = radius * s;

		shaderProgram.setUniform("radius", r, r, r, r);

		float _softness = softness * s;

		shaderProgram.setUniform("softness", _softness);
		shaderProgram.setUniform("alpha", alpha * Renderer2D.getDrawOpacity());
		shaderProgram.setUniform("hueOffset", hueOffset);

		shaderProgram.setUniform("direction", orientation == Orientation.VERTICAL ? 1 : 0);

		drawVertices(pos0, pos1, _softness, false);
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform vec2 size;
				uniform vec2 location;
				uniform vec4 radius;
				
				uniform float softness;
				uniform float alpha;
				uniform float hueOffset;
				
				uniform int direction;
				
				out vec4 fragColor;
				
				""" +
				roundedBoxSDF()
				+ """
				
				vec3 hsv2rgb_smooth(vec3 c) {
				    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
				    rgb = rgb * rgb * (3.0 - 2.0 * rgb); // cubic smoothing
				    return c.z * mix(vec3(1.0), rgb, c.y);
				}
				
				float getHue() {
				    if(direction == 0) {
				        return (gl_FragCoord.x - location.x + hueOffset) / (size.x - hueOffset * 2.);
				    } else {
				        return 1.0 - ((gl_FragCoord.y - location.y + hueOffset) / (size.y - hueOffset * 2.));
				    }
				}
				
				void main(void) {
				    float distance = roundedBoxSDF(gl_FragCoord.xy - location - (size / 2.0), size / 2.0, radius);
				
				    float smoothedAlpha = 1.0 - smoothstep(-1.0, softness + 1.0, distance);
				    float borderAlpha = 1.0 - smoothstep(-2.0, 0, abs(distance));
				
					float hue = getHue();
				    vec3 hsl = vec3(hue, 1.0, 1.0);
				
				    vec4 gradient = vec4(hsv2rgb_smooth(hsl), alpha);
				    vec4 basicColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);
				
				    fragColor = mix(vec4(gradient.rgb, 0.0), mix(basicColor, basicColor, borderAlpha), smoothedAlpha);
				}
				""";
	}
}
