package com.perfect.client.functional.function.visuals.blockSelection;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.FramebufferUtils;
import com.perfect.client.core.render.animation.value.DoubleAnimation;
import com.perfect.client.core.render.animation.value.Vec3dAnimation;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.renderer.buffer.TexturedRenderBuffer;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;


public class BlockSelection extends Function {

	private final BlockSelectionConfig config = new BlockSelectionConfig(this);

	private final Vec3dAnimation vec3dAnimation = new Vec3dAnimation();
	private final DoubleAnimation notSelectedAnimation = new DoubleAnimation(1.0);

	private final TexturedRenderBuffer fillBuffer = new TexturedRenderBuffer(
			VertexFormats.POSITION_COLOR,
			VertexDrawMode.QUADS
	);

	private boolean prevSelected;

	public BlockSelection() {
		super("block-selection", "Block Selection", Category.VISUALS, "Block Highlight");
	}

	private boolean shouldRenderLines() {
		return config.getLineColor().get().getAlpha() > 0f;
	}

	private boolean shouldRenderSides() {
		return config.getSideColor().get().getAlpha() > 0f;
	}

	@Override
	public void onGameLeft() {
		vec3dAnimation.defined(false);
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		updateSpeed();

		if (mc.crosshairTarget instanceof BlockHitResult hitResult && hitResult.getType() != HitResult.Type.MISS) {
			BlockPos pos = hitResult.getBlockPos();
			Vec3d animatedVec3d = getAnimatedVec3d(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));

			BlockState state = BlockUtils.getState(pos);

			VoxelShape shape = state.getOutlineShape(
					mc.world,
					pos,
					ShapeContext.of(mc.gameRenderer.getCamera().getFocusedEntity())
			);

			if (shape.isEmpty()) {
				return;
			}

			if (shouldRenderLines()) {
				drawOutline(event.getMatrices(), animatedVec3d, shape);
			}

			if (shouldRenderSides()) {
				drawSides(event.getMatrices(), animatedVec3d, shape);
			}

			prevSelected = true;
			notSelectedAnimation.current(0.0);
		}
		else {
			prevSelected = false;
			notSelectedAnimation.updateAndGet(1.0);
		}
	}

	private Vec3d getAnimatedVec3d(Vec3d vec3d) {
		if (!config.getAnimation().get()) {
			return vec3d;
		}
		else {
			if (prevSelected) {
				return vec3dAnimation.updateAndGet(vec3d);
			}
			else {
				return vec3dAnimation.setProgressAndGet(vec3d, notSelectedAnimation.getCurrent());
			}
		}
	}

	private void updateSpeed() {
		double speed = 0.5f * config.getAnimationSpeed().fromPercent();

		vec3dAnimation.speed(speed);
		notSelectedAnimation.speed(speed);
	}

	private void drawSides(MatrixStack matrices, Vec3d pos, VoxelShape shape) {
		if (config.getSideColor().get().getAlpha() == 0) {
			return;
		}

		shape.getBoundingBoxes().forEach(box -> BoxRenderer.drawBoxFill(
				fillBuffer,
				matrices,
				box.offset(pos).expand(0.0001),
				new MultiColor(Color.WHITE),
				config.getCosmetics().get().bundle()
		));

		fillBuffer.draw(builder -> {
			Renderer3D.preRender();
			builder.draw();
			Renderer3D.postRender();
		});

		Renderer2D.preRender();
		Render.setColor(config.getSideColor().get().getColor());

		FramebufferUtils.draw(fillBuffer.getFramebuffer(), false);

		Renderer2D.postRender();
	}

	private void drawOutline(MatrixStack matrices, Vec3d pos, VoxelShape shape) {
		Color outlineColor = config.getLineColor().get().getColor();

		if (outlineColor.getAlpha() == 0) {
			return;
		}

		Renderer3D.preRender();

		Render.setShader(ShaderPrograms.POSITION);
		Render.setColor(outlineColor);

		VertexBuilder builder = new VertexBuilder(VertexFormats.POSITION, VertexDrawMode.LINES);

		Camera camera = mc.gameRenderer.getCamera();
		double offsetX = pos.getX() - camera.getPos().getX();
		double offsetY = pos.getY() - camera.getPos().getY();
		double offsetZ = pos.getZ() - camera.getPos().getZ();

		shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
			builder.pos(matrices, (float) (minX + offsetX), (float) (minY + offsetY), (float) (minZ + offsetZ));
			builder.pos(matrices, (float) (maxX + offsetX), (float) (maxY + offsetY), (float) (maxZ + offsetZ));
		});

		RenderUtils.preRender();
		builder.draw();
		Renderer3D.postRender();
		RenderUtils.postRender();
	}
}