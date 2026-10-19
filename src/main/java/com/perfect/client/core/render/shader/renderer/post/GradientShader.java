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
import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class GradientShader extends Shader {

    public GradientShader() {
        super(VertexFormats.BLIT_SCREEN, VertexDrawMode.QUADS);
    }

    public void draw(
            Framebuffer framebuffer,
            List<Color> colors,
            int lineWidth,
            float fillAlpha,
            float gradientFactor,
            float timeMultiplier
    ) {
        shaderProgram.setTexture("DiffuseSampler", framebuffer.getColorAttachment(), 0);
        shaderProgram.setUniform("uResolution", mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);

        int colorCount = Math.clamp(colors.size(), 2, 8);
        shaderProgram.setUniform("uColorCount", colorCount);

        for (int i = 0; i < colorCount; i++) {
            shaderProgram.setUniform("uColors[" + i + "]", ColorUtils.getArray4f(colors.get(i)));
        }

        shaderProgram.setUniform("uLineWidth", lineWidth);
        shaderProgram.setUniform("uOutlineAlpha", (colors.getFirst().getAlpha() / 255f));
        shaderProgram.setUniform("uFillAlpha", fillAlpha);
        shaderProgram.setUniform("uGradientFactor", gradientFactor);

        float shaderTime = ((System.currentTimeMillis() % 1000000) / 1000f) * timeMultiplier;
        shaderProgram.setUniform("uTime", shaderTime);

        Renderer2D.preRender(false);
        Render.depthMask(false);

        VertexBuilder vertexBuilder = getVertexBuilder();

        vertexBuilder.pos(-1.0f, -1.0f).tex(0.0f, 0.0f);
        vertexBuilder.pos(1.0f, -1.0f).tex(1.0f, 0.0f);
        vertexBuilder.pos(1.0f, 1.0f).tex(1.0f, 1.0f);
        vertexBuilder.pos(-1.0f, 1.0f).tex(0.0f, 1.0f);

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
                
                uniform float uOutlineAlpha;
                uniform float uFillAlpha;
                uniform float uTime;
                uniform float uGradientFactor;
                
                uniform int uLineWidth;
                uniform int uColorCount;
                uniform vec4 uColors[8];
                
                in vec2 texCoord;
                out vec4 fragColor;
                
                vec4 getGradientColor() {
                    float minrz = min(uResolution.x, uResolution.y);
                    vec2 p = (((vec2(2.0, 2.0) * gl_FragCoord.xy) - uResolution.xy) * vec2((uGradientFactor / minrz), (uGradientFactor / minrz)));
                    
                    float t = (p.x - p.y) * 0.25 + uTime * 0.5;
                    
                    t = fract(t);
                    
                    float scaledT = t * float(uColorCount);
                    int index1 = int(scaledT);
                    int index2 = index1 + 1;
                    
                    if (index2 >= uColorCount) {
                        index2 = 0;
                    }
                    
                    float blend = fract(scaledT);
                    
                    return mix(uColors[index1], uColors[index2], blend);
                }
                
                void main(void) {
                    vec4 centerCol = texture(DiffuseSampler, texCoord);
                    
                    if (centerCol.a < 0.001) {
                        discard;
                        return;
                    }
                    
                    vec2 offset = 1.0 / uResolution.xy;
                    vec4 gradColor = getGradientColor();
                    
                    if (texture(DiffuseSampler, texCoord + vec2(uLineWidth, 0) * offset).a > 0.001 &&
                        texture(DiffuseSampler, texCoord - vec2(uLineWidth, 0) * offset).a > 0.001 &&
                        texture(DiffuseSampler, texCoord + vec2(0, uLineWidth) * offset).a > 0.001 &&
                        texture(DiffuseSampler, texCoord - vec2(0, uLineWidth) * offset).a > 0.001) {
                            fragColor = vec4(gradColor.rgb, uFillAlpha);
                            return;
                    }
                        
                    for (int x = -uLineWidth; x <= uLineWidth; x++) {
                        for (int y = -uLineWidth; y <= uLineWidth; y++) {
                            vec4 tex = texture(DiffuseSampler, texCoord + offset.xy * vec2(float(x), float(y)));
                            
                            if (tex.a < 0.001) {
                                fragColor = vec4(gradColor.rgb, uOutlineAlpha);
                                return;
                            }
                        }
                    }
                    
                    fragColor = vec4(gradColor.rgb, uFillAlpha);
                }
                """;
    }
}
