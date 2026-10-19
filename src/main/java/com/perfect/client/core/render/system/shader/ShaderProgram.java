package com.perfect.client.core.render.system.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.perfect.client.core.render.renderer.Render;
import lombok.AllArgsConstructor;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.texture.GlTexture;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL40.*;


public class ShaderProgram {

	private final CompiledShader compiledShader;

	private final Map<String, Integer> textureBindings = new HashMap<>();
	private final Map<String, UniformData> uniforms = new HashMap<>();
	private final Map<String, GpuBufferSlice> bufferBindings = new HashMap<>();

	// Кэш для локаций униформов
	private final Map<String, Integer> uniformLocations = new HashMap<>();
	private final Map<String, Integer> blockIndices = new HashMap<>();

	private final boolean globals;

	public ShaderProgram(String vertexSource, String fragmentSource, boolean globals) {
		compiledShader = new CompiledShader(vertexSource, fragmentSource);
		this.globals = globals;
	}

	private int getLocation(String name) {
		return uniformLocations.computeIfAbsent(name, n -> glGetUniformLocation(compiledShader.getProgramId(), n));
	}

	private int getBlockIndex(String name) {
		return blockIndices.computeIfAbsent(name, n -> glGetUniformLocation(compiledShader.getProgramId(), n));
	}

	public void setUniform(String name, GpuBufferSlice gpuBufferSlice) {
		bufferBindings.put(name, gpuBufferSlice);
	}

	public void setUniform(String name, float... values) {
		int loc = getLocation(name);
		if (loc == -1) return;
		switch (values.length) {
			case 1 -> uniforms.put(name, new UniformData(UniformType.FLOAT, values, loc));
			case 2, 3, 4 -> uniforms.put(name, new UniformData(UniformType.FLOAT_ARRAY, values, loc));
			default -> throw new RuntimeException("Invalid number of uniforms.");
		}
	}

	public void setUniform(String name, Matrix4f matrix) {
		int loc = getLocation(name);
		if (loc != -1) uniforms.put(name, new UniformData(UniformType.MATRIX_4F, matrix.get(new float[16]), loc));
	}

	public void setUniform(String name, int value) {
		int loc = getLocation(name);
		if (loc != -1) uniforms.put(name, new UniformData(UniformType.INT, new float[]{value}, loc));
	}

	public void setTexture(String name, GpuTexture texture, int textureUnit) {
		setTexture(name, ((GlTexture) texture).getGlId(), textureUnit);
	}

	public void setTexture(String name, int textureId, int textureUnit) {
		textureBindings.put(name, textureUnit);
		Render.activeTexture(GL_TEXTURE0 + textureUnit);
		Render.bindTexture(textureId);
	}

	public void bind() {
		Render.invalidateProgram();
		setupGlobalUniforms();
		compiledShader.use();

		for (UniformData data : uniforms.values()) {
			switch (data.type) {
				case FLOAT -> glUniform1f(data.location, data.values[0]);
				case FLOAT_ARRAY -> {
					if (data.values.length == 2) glUniform2f(data.location, data.values[0], data.values[1]);
					else if (data.values.length == 3) glUniform3f(data.location, data.values[0], data.values[1], data.values[2]);
					else if (data.values.length == 4) glUniform4f(data.location, data.values[0], data.values[1], data.values[2], data.values[3]);
				}
				case INT -> glUniform1i(data.location, (int) data.values[0]);
				case MATRIX_4F -> glUniformMatrix4fv(data.location, false, data.values);
			}
		}

		for (Map.Entry<String, Integer> entry : textureBindings.entrySet()) {
			int location = getLocation(entry.getKey());
			if (location != -1) glUniform1i(location, entry.getValue());
		}

		int i = 0;
		for (Map.Entry<String, GpuBufferSlice> entry : bufferBindings.entrySet()) {
			int location = glGetUniformBlockIndex(compiledShader.getProgramId(), entry.getKey());
			if (location == -1) continue;

			glUniformBlockBinding(compiledShader.getProgramId(), location, i);
			glBindBufferRange(GL_UNIFORM_BUFFER, location, ((GlGpuBuffer) entry.getValue().buffer()).id, entry.getValue().offset(), entry.getValue().length());
			i++;
		}
	}

	private void setupGlobalUniforms() {
		setUniform("ModelViewMat", RenderSystem.getModelViewMatrix());
		setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
		if (globals) {
			setTexture("Sampler0", Render.getTextureId(), 0);
			setUniform("color", Render.getColor());
		}
	}

	public void unbind() {
		glUseProgram(0);
	}

	public enum UniformType { FLOAT, FLOAT_ARRAY, INT, MATRIX_4F }

	@AllArgsConstructor
	public static class UniformData {
		public UniformType type;
		public float[] values;
		public int location;
	}
}