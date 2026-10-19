package com.perfect.client.core.render.shader.renderer.post;

import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.color.ColorUtils;
import net.minecraft.client.gl.Framebuffer;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;


public class WaveShader extends Shader {

    public WaveShader() {
        super(VertexFormats.BLIT_SCREEN, VertexDrawMode.QUADS);
    }

    public void draw(Framebuffer framebuffer, Color waveColor, float waveDistance,
                     float waveWidth, float waveSpeed) {
        shaderProgram.setTexture("DiffuseSampler", framebuffer.getColorAttachment(), 0);
        shaderProgram.setUniform("uResolution", mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);

        shaderProgram.setUniform("uWaveColor", ColorUtils.getArray4f(waveColor));
        shaderProgram.setUniform("uWaveDistance", waveDistance);
        shaderProgram.setUniform("uWaveWidth", waveWidth);
        shaderProgram.setUniform("uWaveSpeed", waveSpeed);

        float shaderTime = (System.currentTimeMillis() % 1000000) / 1000f;
        shaderProgram.setUniform("uTime", shaderTime);

        Renderer2D.preRender();
        Render.depthMask(false);

        VertexBuilder vertexBuilder = getVertexBuilder();

        vertexBuilder.pos(-1f, -1f).tex(0f, 0f);
        vertexBuilder.pos(1f, -1f).tex(1f, 0f);
        vertexBuilder.pos(1f, 1f).tex(1f, 1f);
        vertexBuilder.pos(-1f, 1f).tex(0f, 1f);

        vertexBuilder.draw(shaderProgram);

        Render.depthMask(true);
        Renderer2D.postRender();
    }

    @Override
    protected String getFragmentSource() {
        return """
                #version 330 core
                
                uniform sampler2D DiffuseSampler;
                
                uniform vec2 uResolution;
                
                uniform vec4 uWaveColor;
                uniform float uWaveDistance;
                uniform float uWaveWidth;
                uniform float uWaveSpeed;
                uniform float uTime;
                
                in vec2 texCoord;
                out vec4 fragColor;
                
                #define WIDTH 1
                #define less(a,b,c)    mix(a,b,step(0.,c))
                #define sabs(x,k) less((.5/k)*x*x+k*.5,abs(x),abs(x)-k)
    
                float smin( float a, float b, float k )
                {
                    float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
                    return mix( b, a, h ) - k*h*(1.0-h);
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
				
                vec3 getColor() {
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
                
                     return brightness * uWaveColor.rgb;
                 }
                
                void main(void) {
                	vec4 centerCol = texture(DiffuseSampler, texCoord);

                    if(centerCol.a < 0.001) {
                        discard;
                        return;
                    }
                    else {
                        vec2 offset = 1. / uResolution.xy;
                
                        bool def = isDefaultColor(centerCol);

                        for(int x = -WIDTH; x <= WIDTH; x++) {
                            for(int y = -WIDTH; y <= WIDTH; y++) {
                                vec4 tex = texture(DiffuseSampler, texCoord + offset.xy * vec2(float(x), float(y)));
                
                                if (tex.a < 0.001) {
                                    fragColor = getColorFromParts(vec4(getColor(), 1.0), centerCol, def);
                                    return;
                                }
                            }
                        }
                
                        fragColor = getColorFromParts(vec4(getColor(), uWaveColor.a), centerCol, def);
                    }
                }
                """;
    }
}
