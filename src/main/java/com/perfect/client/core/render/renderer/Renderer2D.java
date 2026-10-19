package com.perfect.client.core.render.renderer;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.shader.renderer.BlurShader;
import com.perfect.client.core.render.shader.renderer.BorderedFontShader;
import com.perfect.client.core.render.shader.renderer.FontShader;
import com.perfect.client.core.render.shader.renderer.SdfShader;
import com.perfect.client.core.render.shader.renderer.round.*;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.VertexRenderer;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.gui.utils.MatrixUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL40;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;


public class Renderer2D {

	public static VertexRenderer VERTEX_RENDERER;

	public static RoundShader ROUND_SHADER;
	public static HueShader HUE_SHADER;
	public static BlurShader BLUR_SHADER;
	public static BlurredRoundShader BLURRED_ROUND_SHADER;
	public static FontShader FONT_SHADER;
	public static BorderedFontShader BORDERED_FONT_SHADER;
	public static SdfShader SDF_SHADER;

	@Getter
	@Setter
	private static float drawOpacity = 1.0f;

	
	public static void init() {
		VERTEX_RENDERER = new VertexRenderer();

		ROUND_SHADER = new RoundShader();
		HUE_SHADER = new HueShader();
		BLUR_SHADER = new BlurShader();
		BLURRED_ROUND_SHADER = new BlurredRoundShader();
		FONT_SHADER = new FontShader();
		BORDERED_FONT_SHADER = new BorderedFontShader();
		SDF_SHADER = new SdfShader();
	}

	public static BlurredRoundBuilder blurredRound() {
		return new BlurredRoundBuilder((matrices, builder) -> BLURRED_ROUND_SHADER.draw(
				matrices,
				builder.getX(),
				builder.getY(),
				builder.getWidth(),
				builder.getHeight(),
				builder.getRounding(),
				builder.getThickness(),
				builder.getSoftness(),
				builder.getBlur(),
				builder.getAlpha(),
				builder.getOutlineAlpha(),
				builder.getOutlineColor(),
				builder.getColoring()
		));
	}

	public static ColoredRoundBuilder<?> round() {
		return new ColoredRoundBuilder<>((matrices, builder) -> ROUND_SHADER.draw(
				matrices,
				builder.getX(),
				builder.getY(),
				builder.getWidth(),
				builder.getHeight(),
				builder.getRounding(),
				builder.getThickness(),
				builder.getSoftness(),
				builder.getAlpha(),
				builder.getOutlineAlpha(),
				builder.getOutlineColor(),
				builder.getColoring()
		));
	}

	//——————————————————————TEXTURE——————————————————————//

	public static void drawLine(Matrix3x2fStack matrices, float x, float y, float x2, float y2, Color color) {
		Matrix3x2f matrix = new Matrix3x2f(matrices);
		preRender();

		Render.setShader(ShaderPrograms.POSITION);
		Render.setColor(color);

		VertexBuilder builder = new VertexBuilder(
				VertexFormats.POSITION,
				VertexDrawMode.LINES
		);

		builder.pos(matrix, x, y);
		builder.pos(matrix, x2, y2);

		builder.draw();

		postRender();
	}

	public static void drawOutline(Matrix3x2fStack matrices, float x, float y, float width, float height, Color color) {
		Matrix3x2f matrix = new Matrix3x2f(matrices);
		preRender();

		Render.setShader(ShaderPrograms.POSITION);
		Render.setColor(color);

		VertexBuilder builder = new VertexBuilder(
				VertexFormats.POSITION,
				VertexDrawMode.LINES
		);

		builder.pos(matrix, x, y);
		builder.pos(matrix, x + width, y);

		builder.pos(matrix, x + width, y);
		builder.pos(matrix, x + width, y + height);

		builder.pos(matrix, x + width, y + height);
		builder.pos(matrix, x, y + height);

		builder.pos(matrix, x, y + height);
		builder.pos(matrix, x, y);

		builder.draw();

		postRender();
	}

	public static void drawRect(Matrix3x2fStack matrices, float x, float y, float width, float height, Color color) {
		Matrix3x2f matrix = new Matrix3x2f(matrices);
		preRender();

		Render.setShader(ShaderPrograms.POSITION);
		Render.setColor(color);

		VertexBuilder builder = new VertexBuilder(
				VertexFormats.POSITION,
				VertexDrawMode.QUADS
		);

		builder.pos(matrix, x, y + height);
		builder.pos(matrix, x + width, y + height);
		builder.pos(matrix, x + width, y);
		builder.pos(matrix, x, y);

		builder.draw();

		postRender();
	}

	//—————————————————————————————————————————————————————————//

	public static Framebuffer initBuffer(Framebuffer framebuffer) {
		Framebuffer main = mc.getFramebuffer();
		int width = main.textureWidth;
		int height = main.textureHeight;

		if (framebuffer == null) {
			return new SimpleFramebuffer("perfect$framebuffer", width, height, true);
		}

		if (framebuffer.textureWidth != width || framebuffer.textureHeight != height) {
			framebuffer.resize(width, height);
		}

		return framebuffer;
	}

	//—————————————————————————————————————————————————————————//

	public static void preRender() {
		preRender(true);
	}

	public static void preRender(boolean setFramebuffer) {
		if (setFramebuffer) {
			RenderUtils.preRender();
		}

		Render.colorMask(true, true, true, true);
		GlStateManager._polygonMode(GL40.GL_FRONT_AND_BACK, GL40.GL_FILL);
		GlStateManager._disablePolygonOffset();
		GlStateManager._disableColorLogicOp();

		Render.enableBlend();
		Render.defaultBlendFunc();
		Render.disableDepthTest();
		Render.setColor(Color.WHITE);
		Render.disableCull();
	}

	public static void postRender() {
		Render.defaultBlendFunc();
		Render.disableBlend();
		Render.enableDepthTest();
		Render.setColor(Color.WHITE);
		Render.enableCull();

		RenderUtils.postRender();
	}

	//———————————————————————TEXTURES————————————————————————//

	public static void drawTexture(
			Matrix3x2fStack matrices,
			int texture,
			float x, float y, float width, float height,
			Color color,
			Runnable glArgs
	) {
		RenderUtils.preRender();
		preRender();

		Render.setColor(ColorUtils.multiplyAlpha(color, drawOpacity));
		Render.setShader(ShaderPrograms.POSITION_TEXTURE);
		Render.setTextureId(texture);

		VertexBuilder builder = new VertexBuilder(
				VertexFormats.POSITION_TEXTURE,
				VertexDrawMode.QUADS
		);

		if (glArgs != null) {
			glArgs.run();
		}

		Matrix3x2f matrix = new Matrix3x2f(matrices);

		builder.pos(matrix, x, y + height).tex(0.0f, 1.0f);
		builder.pos(matrix, x + width, y + height).tex(1.0f, 1.0f);
		builder.pos(matrix, x + width, y).tex(1.0f, 0.0f);
		builder.pos(matrix, x, y).tex(0.0f, 0.0f);

		builder.draw();

		postRender();
		RenderUtils.postRender();
	}

	public static void drawCenteredTexture(
			Matrix3x2fStack matrices,
			int texture,
			float x, float y, float size,
			Color color,
			Runnable glArgs
	) {
		drawTexture(matrices, texture, x - size / 2f, y - size / 2f, size, size, color, glArgs);
	}

	public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay, String countOverride, boolean disableGuiScale) {
		Matrix3x2fStack matrices = drawContext.getMatrices();
		matrices.pushMatrix();

		if (disableGuiScale) {
			matrices.scale(1.0f / mc.getWindow().getScaleFactor());
		}

		matrices.scale(scale, scale);

		int scaledX = (int) (x / scale);
		int scaledY = (int) (y / scale);

		drawContext.drawItem(itemStack, scaledX, scaledY);
		if (overlay) drawContext.drawStackOverlay(mc.textRenderer, itemStack, scaledX, scaledY, countOverride);

		matrices.popMatrix();
	}

	public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay) {
		drawItem(drawContext, itemStack, x, y, scale, overlay, null, true);
	}


	public static void drawItem(
			Matrix3x2fStack matrices,
			ItemStack stack,
			float centerX,
			float centerY,
			float scale
	) {
		if (stack == null || stack.isEmpty()) {
			return;
		}

		preRender();

		MatrixStack itemMatrices = new MatrixStack();

		Matrix3x2f transformMatrix = new Matrix3x2f(matrices);
		Vector2f pos = transformMatrix.transformPosition(centerX, centerY, new Vector2f());

		itemMatrices.translate(pos.x, pos.y, 100.0f);

		float size = scale * MatrixUtils.getMatrixScaleFactor(transformMatrix);

		itemMatrices.scale(size, -size, size);

		VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

		KeyedItemRenderState state = new KeyedItemRenderState();
		mc.getItemModelManager().clearAndUpdate(state, stack, ItemDisplayContext.GUI, mc.world, null, 0);

		if (!state.isSideLit()) {
			mc.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
		}
		else {
			mc.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
		}

		state.render(
				itemMatrices,
				immediate,
				15728880,
				OverlayTexture.DEFAULT_UV
		);

		immediate.draw();

		postRender();
	}
}