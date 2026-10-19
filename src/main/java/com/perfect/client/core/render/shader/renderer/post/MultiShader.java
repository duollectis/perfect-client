package com.perfect.client.core.render.shader.renderer.post;

import com.perfect.client.core.render.ScreenTexture;
import com.perfect.client.core.render.postprocess.PostProcessShaders;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.ColorUtils;
import net.minecraft.client.gl.Framebuffer;

import java.awt.*;


public class MultiShader extends OutlineShader {

	private final ScreenTexture glowTexture = new ScreenTexture();

	public void draw(
			Framebuffer framebuffer,
			Color outlineColor, Color fillColor, float fillAlpha, int mode, boolean customOutlineColor, int lineWidth,
			float gradientFactor, float waveDistance, float waveWidth, float waveSpeed, float timeMultiplier,
			boolean glow, float glowMultiplier, float glowSize,
			Color glowColor
	) {
		if (glow) {
			PostProcessShaders.GLOW_SHADER.draw(
					framebuffer,
					glowTexture,
					glowMultiplier,
					glowSize
			);

			shaderProgram.setUniform("isGlow", 1);
			shaderProgram.setTexture("GlowSampler", glowTexture.getColorTexture(), 1);
			shaderProgram.setUniform("uGlowColor", ColorUtils.getArray4f(glowColor));
		}
		else {
			shaderProgram.setUniform("isGlow", 0);
		}

		shaderProgram.setUniform("uFillAlpha", fillAlpha);
		shaderProgram.setUniform("uWaveDistance", waveDistance);
		shaderProgram.setUniform("uWaveWidth", waveWidth);
		shaderProgram.setUniform("uWaveSpeed", waveSpeed);
		shaderProgram.setUniform("uGradientFactor", gradientFactor);
		shaderProgram.setUniform("uMode", mode);
		shaderProgram.setUniform("uCustomOutlineColor", customOutlineColor ? 1 : 0);

		float shaderTime = ((System.currentTimeMillis() % 1000000) / 1000f) * timeMultiplier;
		shaderProgram.setUniform("uTime", shaderTime);

		RenderUtils.preRender();
		super.draw(framebuffer, lineWidth, outlineColor, fillColor);
	}

	@Override
	protected String getFragmentSource() {
		return """
               #version 330 core
    
                uniform sampler2D DiffuseSampler;
                uniform sampler2D GlowSampler;
    
                uniform vec2 uResolution;
    
                uniform float uFillAlpha;
                uniform float uTime;
                uniform float uGradientFactor;
                uniform float uWaveDistance;
                uniform float uWaveWidth;
                uniform float uWaveSpeed;
    
                uniform vec4 uFillColor;
                uniform vec4 uOutlineColor;
                uniform vec4 uGlowColor;
    
                uniform int uLineWidth;
                uniform int uMode;
                uniform int uCustomOutlineColor;
                uniform int isGlow;
    
                in vec2 texCoord;
                out vec4 fragColor;
    
               #define less(a,b,c)    mix(a,b,step(0.,c))
               #define sabs(x,k) less((.5/k)*x*x+k*.5,abs(x),abs(x)-k)
    
                vec3 palette(in float t) {
                    vec3 a = vec3(0.6, 0.6, 0.6);
					vec3 b = vec3(0.4, 0.4, 0.4);
					vec3 c = vec3(1.0, 1.0, 1.0);
					vec3 d = vec3(0.0, 0.33, 0.67);

					return a + b * cos(6.28318 * (c * t + d));
                }
    
                vec3 getColor() {
                    float minrz = min(uResolution.x, uResolution.y);
                    vec2 p = (((vec2(2.0, 2.0) * gl_FragCoord.xy) - uResolution.xy) * vec2((uGradientFactor / minrz), (uGradientFactor / minrz)));
   
                    float t = (p.x + p.y) * 0.25 + uTime * 0.2;
    
                    return palette(t);
                }
    
                float smin( float a, float b, float k )
                {
                   float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
                   return mix( b, a, h ) - k*h*(1.0-h);
                }
    
                vec3 getWaveColor() {
                    vec2 position = 2.0 * texCoord - 9.0;
                    position.x *= uResolution.x / uResolution.y;
    
                    float a = radians(-45.0);
                    mat2 rot = mat2(cos(a), -sin(a), sin(a), cos(a));
                    position *= rot;
    
                    position = sabs(position, 0.05);
    
                    float d  = length(position + vec2(1.0, 0.0)) - 0.5;
                    float d2 = length(position - vec2(1.0, 0.0)) - 0.5;
    
                    vec2 offset = vec2(sin(uTime * 1.3), cos(uTime));
                    offset = sabs(offset, 0.7);
                    offset *= 0.45;
    
                    float d3 = length(position - offset) - 0.3;
                    d = smin(d, d2, 0.45);
                    d = smin(d, d3, 0.3 + sin(uTime) * 0.125);
    
                    float finalShape = d + position.x;
    
                    float wave = sin(finalShape * uWaveDistance - (uTime * uWaveSpeed));
                    wave = sabs(wave, 0.5);
    
                    float brightness = uWaveWidth / wave;
    
                    return brightness * uFillColor.rgb;
                }
    
                bool isDefaultColor(vec4 col) {
                    return col.r < 0.01 && col.g < 0.01 && col.b < 0.01;
                }
    
               vec4 getColorFromParts(vec4 col, vec4 centerCol, bool def) {
                if (def) {
                   return col;
                }
                else {
                   return vec4(centerCol.r, centerCol.g, centerCol.b, centerCol.a * col.a);
                }
               }
    
                void main(void) {
                    vec4 centerCol = texture(DiffuseSampler, texCoord);
                    vec4 glowTex = texture(GlowSampler, texCoord);
    
                    vec4 glowColor;
                    if (isGlow == 1) {
                       if (uMode == 0) {
                           glowColor = vec4(uGlowColor.rgb, glowTex.a);
                       } else if (uMode == 1) {
                           if (uCustomOutlineColor == 1) {
                                glowColor = vec4(uOutlineColor.rgb, glowTex.a);
                           } else {
                                glowColor = vec4(getWaveColor(), glowTex.a);
                           }
                       } else {
                           glowColor = vec4(getColor(), glowTex.a);
                       }
                    } else {
                        glowColor = vec4(0.0);
                    }
    
                    if (centerCol.a < 0.001) {
                        fragColor = glowColor;
                    } else {
                        vec2 offset = 1.0 / uResolution.xy;
                        bool def = isDefaultColor(centerCol);
    
                        if (texture(DiffuseSampler, texCoord + vec2(uLineWidth, 0) * offset).a > 0.001 &&
                            texture(DiffuseSampler, texCoord - vec2(uLineWidth, 0) * offset).a > 0.001 &&
                            texture(DiffuseSampler, texCoord + vec2(0, uLineWidth) * offset).a > 0.001 &&
                            texture(DiffuseSampler, texCoord - vec2(0, uLineWidth) * offset).a > 0.001) {
	                            vec4 fillColor;
		                        if (uMode == 0) {
		                            fillColor = getColorFromParts(uFillColor, centerCol, def);
		                        } else if (uMode == 1) {
		                            fillColor = vec4(getWaveColor(), uFillAlpha);
		                        } else {
		                            fillColor = vec4(getColor(), uFillAlpha);
		                        }
		    
		                        fragColor = mix(fillColor, glowColor, glowColor.a);
	                            return;
                        }
                        
                        for (int x = -uLineWidth; x <= uLineWidth; x++) {
                            for (int y = -uLineWidth; y <= uLineWidth; y++) {
                                vec4 tex = texture(DiffuseSampler, texCoord + offset.xy * vec2(float(x), float(y)));
                                if (tex.a < 0.001) {
                                    if (uMode == 0) {
                                       fragColor = getColorFromParts(uOutlineColor, centerCol, def);
                                    } else if (uMode == 1) {
                                       if (uCustomOutlineColor == 1) {
                                            fragColor = uOutlineColor;
                                       } else {
                                            fragColor = vec4(getWaveColor(), uOutlineColor.a);
                                       }
                                   } else {
                                       fragColor = vec4(getColor(), 1.0);
                                   }
                                   return;
                                }
                            }
                        }
    
                        vec4 fillColor;
                        if (uMode == 0) {
                            fillColor = getColorFromParts(uFillColor, centerCol, def);
                        } else if (uMode == 1) {
                            fillColor = vec4(getWaveColor(), uFillAlpha);
                        } else {
                            fillColor = vec4(getColor(), uFillAlpha);
                        }
    
                        fragColor = mix(fillColor, glowColor, glowColor.a);
                    }
                }
    	""";
	}
}