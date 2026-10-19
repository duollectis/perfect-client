package com.perfect.client.core.render.system.vertex;

import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.shader.ShaderProgram;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormat;
import com.perfect.client.core.utils.color.Color4f;
import lombok.Getter;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

import java.awt.*;
import java.lang.Math;
import java.util.Arrays;

import static com.perfect.client.PerfectClient.mc;


public class VertexBuilder {

	// Понимаю, что лист выглядел красивее, но не для оптимизации рендера
	// Переделал на массив, дабы исключить обертку примитива в объект и дабы исключить изнасилование сборщика мусора
	private static final int BUFFER_SIZE = 24;

	private float[] vertices = new float[BUFFER_SIZE];
	private float[] formatCache = new float[0];
	private int size = 0;
	@Getter
	private int activeSize = 0;

	private final VertexFormat format;
	private final VertexDrawMode drawMode;

	public VertexBuilder(VertexFormat format, VertexDrawMode drawMode) {
		this.format = format;
		this.drawMode = drawMode;
	}

	private void ensureCapacity(int additional) {
		if (size + additional > vertices.length) {
			vertices = Arrays.copyOf(vertices, Math.max(vertices.length * 2, size + additional));
		}
	}

	public void draw() {
		draw(Render.getShader());
	}

	public void draw(ShaderProgram shaderProgram) {
		shaderProgram.bind();
		Renderer2D.VERTEX_RENDERER.drawVertices(this);
		shaderProgram.unbind();
	}

	public float[] build() {
		if (drawMode == VertexDrawMode.QUADS) {
			int components = getStride();
			int comp2 = components * 2;
			int comp3 = components * 3;
			int comp4 = components * 4;

			int quadCount = size / comp4;
			activeSize = quadCount * 6 * components;

			if (formatCache == null || formatCache.length < activeSize) {
				formatCache = new float[activeSize];
			}

			int resIdx = 0;
			int base = 0;

			for (int quad = 0; quad < quadCount; quad++) {
				System.arraycopy(vertices, base, formatCache, resIdx, comp3);
				resIdx += comp3;

				System.arraycopy(vertices, base + comp2, formatCache, resIdx, comp2);
				resIdx += comp2;

				System.arraycopy(vertices, base, formatCache, resIdx, components);
				resIdx += components;

				base += comp4;
			}
			return formatCache;
		}

		activeSize = size;

		return vertices;
	}

	public void clear() {
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public VertexBuilder pos(float x, float y) {
		return pos(x, y, 0);
	}

	public VertexBuilder pos(Matrix3x2fStack matrices, float x, float y) {
		return pos(new Matrix3x2f(matrices), x, y);
	}

	public VertexBuilder pos(Matrix3x2f matrix, float x, float y) {
		Vector2f vector2f = matrix.transformPosition(x, y, new Vector2f());
		return pos(vector2f.x, vector2f.y, 0);
	}

	public VertexBuilder pos(MatrixStack matrices, float x, float y, float z) {
		return pos(matrices.peek().getPositionMatrix(), x, y, z);
	}

	public VertexBuilder pos(MatrixStack matrices, Vec3d vec3d) {
		return pos(
				matrices.peek().getPositionMatrix(),
				(float) vec3d.getX(),
				(float) vec3d.getY(),
				(float) vec3d.getZ()
		);
	}

	public VertexBuilder pos(Matrix4f matrix, float x, float y, float z) {
		Vector3f vector3f = matrix.transformPosition(x, y, z, new Vector3f());
		return pos(vector3f);
	}

	public VertexBuilder pos(Vector3f vector3f) {
		return pos(vector3f.x, vector3f.y, vector3f.z);
	}

	public VertexBuilder pos(Vec3d vec3d) {
		return pos((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
	}

	public VertexBuilder pos(float x, float y, float z) {
		ensureCapacity(3);

		vertices[size++] = x;
		vertices[size++] = y;
		vertices[size++] = z;

		return this;
	}

	public VertexBuilder screenTex(Matrix3x2fStack matrices, float u, float v) {
		return screenTex(new Matrix3x2f(matrices), u, v);
	}

	public VertexBuilder screenTex(Matrix3x2f matrix, float u, float v) {
		Vector2f vector2f = matrix.transformPosition(u, v, new Vector2f());
		return screenTex(vector2f.x, vector2f.y);
	}

	public VertexBuilder screenTex(float u, float v) {
		Window window = mc.getWindow();
		float glU = u / window.getScaledWidth();
		float glV = (window.getScaledHeight() - v) / window.getScaledHeight();

		return tex(glU, glV);
	}

	public VertexBuilder tex(float u, float v) {
		ensureCapacity(2);

		vertices[size++] = u;
		vertices[size++] = v;

		return this;
	}

	public VertexBuilder normal(float... components) {
		ensureCapacity(components.length);

		for (float c : components) {
			vertices[size++] = c;
		}

		return this;
	}

	public VertexBuilder color(Color color) {
		return color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public VertexBuilder color(Color4f color) {
		return color(color.r, color.g, color.b, color.a);
	}

	public VertexBuilder color(float[] rgba) {
		return color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public VertexBuilder color(int argb) {
		return color(argb >> 16 & 255, argb >> 8 & 255, argb & 255, argb >>> 24);
	}

	public VertexBuilder color(int r, int g, int b, int a) {
		return color(
				r / 255f,
				g / 255f,
				b / 255f,
				a / 255f
		);
	}

	public VertexBuilder color(float r, float g, float b, float a) {
		ensureCapacity(4);

		vertices[size++] = r;
		vertices[size++] = g;
		vertices[size++] = b;
		vertices[size++] = a;

		return this;
	}

	public int getGlMode() {return drawMode.getGlMode();}

	public int getStride() {return format.getStride();}

	public int getPositionSize() {return format.getPositionSize();}

	public int getPositionId() {return format.getPositionId();}

	public int getPositionOffset() {return format.getPositionOffset();}

	public int getTexCoordSize() {return format.getTexCoordSize();}

	public int getTexCoordId() {return format.getTexCoordId();}

	public int getTexCoordOffset() {return format.getTexCoordOffset();}

	public int getNormalSize() {return format.getNormalSize();}

	public int getNormalId() {return format.getNormalId();}

	public int getNormalOffset() {return format.getNormalOffset();}

	public int getColorSize() {return format.getColorSize();}

	public int getColorId() {return format.getColorId();}

	public int getColorOffset() {return format.getColorOffset();}
}