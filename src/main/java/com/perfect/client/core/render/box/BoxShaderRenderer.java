package com.perfect.client.core.render.box;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.ScreenTexture;
import com.perfect.client.core.render.postprocess.PostProcessShaders;
import com.perfect.client.core.render.postprocess.PostProcessTask;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.renderer.buffer.TexturedRenderBuffer;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import java.awt.*;


public class BoxShaderRenderer {

	private static final TexturedRenderBuffer buffer = new TexturedRenderBuffer(
			VertexFormats.POSITION_COLOR,
			VertexDrawMode.QUADS
	);

	public static void drawShaderBox(
			MatrixStack matrices,
			Box box,
			MultiColor outlineColor, MultiColor fillColor,
			int lineWidth,
			boolean gradient, int segments, float gradientFactor, float gradientSpeed
	) {
		Managers.RENDER.drawPostProcess(new PostProcessTask(output -> drawShaderBoxIternal(
				matrices, output, box, outlineColor, fillColor, lineWidth, gradient, segments, gradientFactor, gradientSpeed
		)));
	}

	private static void drawShaderBoxIternal(
			MatrixStack matrices,
			ScreenTexture output,
			Box box,
			MultiColor outlineColor, MultiColor fillColor,
			int lineWidth,
			boolean gradient, int segments, float gradientFactor, float gradientSpeed
	) {
		BoxRenderer.drawBoxFill(buffer, matrices, box, BoxColors.plain(Color.WHITE));

		buffer.draw(builder -> {
			Renderer3D.preRender();
			Renderer3D.disableSmooth();
			Render.setShader(ShaderPrograms.POSITION_COLOR);
			builder.draw();
			Renderer3D.postRender();
		});

		Renderer3D.postRender();

		output.write(
				() -> {
					if (gradient) {
						PostProcessShaders.GRADIENT_SHADER.draw(
								buffer.getFramebuffer(),
								outlineColor.getGradientColors(segments),
								lineWidth,
								fillColor.getAlpha(),
								gradientFactor,
								gradientSpeed
						);
					} else {
						PostProcessShaders.OUTLINE_SHADER.draw(
								buffer.getFramebuffer(),
								lineWidth,
								outlineColor.getColor(),
								fillColor.getColor()
						);
					}
				}, false
		);
	}
}
