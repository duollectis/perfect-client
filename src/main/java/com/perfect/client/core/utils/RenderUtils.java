package com.perfect.client.core.utils;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.FramebufferUtils;
import com.perfect.client.core.render.renderer.Render;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;

import java.awt.*;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.manager.system.FunctionManager.*;


public class RenderUtils {

	private static final ProjectionMatrix2 matrix2d = new ProjectionMatrix2("perfect$2d", 1000.0f, 21000.0f, true);

	public static Matrix3x2fStack expandCapacity(Matrix3x2fStack matrices, int capacity) {
		Matrix3x2fStack matrix3x2fStack = new Matrix3x2fStack(capacity);
		matrix3x2fStack.mul(matrices);
		return matrix3x2fStack;
	}

	public static Color getEntityColor(Entity entity, Color defaultColor) {
		Color teamColor = minigameHelper.getTeamColor(entity);

		if (teamColor != null) {
			return teamColor;
		}

		if (Managers.FRIEND.isFriend(entity)) {
			return clientConfig.getFriendColor().getColor();
		}

		return defaultColor;
	}

	public static Vec3d getEntityPos(Entity entity) {
		return entity.getLerpedPos(getTickDelta());
	}

	public static float getTickDelta() {
		return mc.getRenderTickCounter().getTickProgress(true);
	}

	public static boolean shouldSkipEntityRender(Entity entity) {
		if (entity == null) {
			return true;
		}
		if (antiBot.isBot(entity)) {
			return true;
		}
		return entity.equals(mc.player) && !shouldRenderClientPlayer();
	}

	public static boolean shouldRenderClientPlayer() {
		return freecam.isEnabled() || !mc.options.getPerspective().isFirstPerson();
	}

	public static void preRender() {
		FramebufferUtils.beginWrite(getActiveFramebuffer());
	}

	private static Framebuffer getActiveFramebuffer() {
		if (Render.getActiveFramebuffer() != null) {
			return Render.getActiveFramebuffer();
		}
		return mc.getFramebuffer();
	}

	public static void postRender() {
		Render.bindFramebuffer(0);
	}

	public static void set2DProjection() {
		RenderSystem.backupProjectionMatrix();

		Window window = mc.getWindow();

		RenderSystem.getModelViewStack().pushMatrix();
		RenderSystem.getModelViewStack().translate(0.0f, 0.0f, -11000f);

		RenderSystem.setProjectionMatrix(matrix2d.set(
				(float) window.getFramebufferWidth() / window.getScaleFactor(),
				(float) window.getFramebufferHeight() / window.getScaleFactor()
		), ProjectionType.ORTHOGRAPHIC);
	}

	public static void pop2DProjection() {
		RenderSystem.restoreProjectionMatrix();
		RenderSystem.getModelViewStack().popMatrix();
	}

	public static int getRenderDistance() {
		int chunkLoadDistance = mc.player == null ? 0 : mc.player.networkHandler.chunkLoadDistance;
		return Math.max(mc.options.getViewDistance().getValue(), chunkLoadDistance);
	}
}
