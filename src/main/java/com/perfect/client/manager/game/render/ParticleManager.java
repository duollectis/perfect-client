package com.perfect.client.manager.game.render;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.render.particle.AbstractParticle;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager extends Manager {

	private final List<AbstractParticle> particles = new ArrayList<>();

	@Override
	public void onGameLeft() {
		clear();
	}

	@Override
	public void onPostTick() {
		if (mc.world == null || mc.player == null || particles.isEmpty()) {
			return;
		}

		synchronized (particles) {
			particles.removeIf(AbstractParticle::update);
		}
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null || mc.player == null || mc.gameRenderer.getCamera() == null || particles.isEmpty()) return;

		Renderer3D.preRender();
		Renderer3D.disableSmooth();
		Render.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
		Render.enableDepthTest();
		Render.setTexture(Textures.FIREFLY);

		VertexBuilder vertexBuilder = new VertexBuilder(VertexFormats.POSITION_TEXTURE_COLOR, VertexDrawMode.QUADS);
		float tickDelta = RenderUtils.getTickDelta();

		MatrixStack matrices = event.getMatrices();
		matrices.push();

		synchronized (particles) {
			for (AbstractParticle particle : particles) {
				particle.render(matrices, vertexBuilder, tickDelta);
			}
		}

		matrices.pop();
		vertexBuilder.draw(ShaderPrograms.BRIGHT_POSITION_TEXTURE_COLOR);
		Renderer3D.postRender();
	}

	public void add(AbstractParticle particle) {
		synchronized (particles) {
			particles.add(particle);
		}
	}

	public void clear() {
		synchronized (particles) {
			particles.clear();
		}
	}
}