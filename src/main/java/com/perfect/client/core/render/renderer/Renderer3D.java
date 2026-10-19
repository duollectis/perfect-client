package com.perfect.client.core.render.renderer;

import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.RenderUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL40;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;


public class Renderer3D {

	private static final FrustumIntersection frustumIntersection = new FrustumIntersection();
	private static final Matrix4f frustumProjectionView = new Matrix4f();

	public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
	public static final Matrix4f lastModelViewMatrix = new Matrix4f();
	public static final Matrix4f lastProjectionMatrix = new Matrix4f();

	public static Vec3d screenCenter = Vec3d.ZERO;

	public static boolean isVisibleBox(MatrixStack matrices, Box box) {
		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		frustumProjectionView.set(Renderer3D.lastProjectionMatrix);
		frustumProjectionView.mul(matrices.peek().getPositionMatrix());

		frustumIntersection.set(frustumProjectionView);

		float minX = (float) (box.minX - camera.x);
		float minY = (float) (box.minY - camera.y);
		float minZ = (float) (box.minZ - camera.z);
		float maxX = (float) (box.maxX - camera.x);
		float maxY = (float) (box.maxY - camera.y);
		float maxZ = (float) (box.maxZ - camera.z);

		return frustumIntersection.testAab(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static void preRender() {
		enableSmooth();

		RenderUtils.preRender();

		Render.enableBlend();
		Render.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
		Render.disableDepthTest();
		Render.depthMask(false);
		Render.setColor(Color.WHITE);
		Render.disableCull();
	}

	public static void postRender() {
		disableSmooth();

		Render.setColor(Color.WHITE);
		Render.defaultBlendFunc();
		Render.enableDepthTest();
		Render.disableBlend();
		Render.depthMask(true);
		Render.enableCull();
		RenderUtils.postRender();
	}

	public static void enableSmooth() {
		Render.enableLineSmooth();
		Render.enablePolygonSmooth();
	}

	public static void disableSmooth() {
		Render.disableLineSmooth();
		Render.disablePolygonSmooth();
	}

	public static void drawLine(MatrixStack matrices, Vec3d start, Vec3d end, Color color) {
		preRender();

		Render.setShader(ShaderPrograms.POSITION_COLOR);
		VertexBuilder builder = new VertexBuilder(VertexFormats.POSITION_COLOR, VertexDrawMode.LINES);

		matrices.push();

		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		float x1 = (float) (start.getX() - camera.x);
		float y1 = (float) (start.getY() - camera.y);
		float z1 = (float) (start.getZ() - camera.z);

		float x2 = (float) (end.getX() - camera.x);
		float y2 = (float) (end.getY() - camera.y);
		float z2 = (float) (end.getZ() - camera.z);

		builder.pos(matrices, x1, y1, z1).color(color);
		builder.pos(matrices, x2, y2, z2).color(color);

		builder.draw();

		matrices.pop();

		postRender();
	}

	public static void updateScreenCenter(Matrix4f viewMatrix) {
		Matrix4f projection = new Matrix4f(lastProjectionMatrix).invert();
		Matrix4f view = new Matrix4f(viewMatrix).invert();

		Vector4f vector4f = new Vector4f(0.0f, -0.0f, 1.0f, 1.0f).mul(projection).mul(view);
		vector4f.div(vector4f.w);

		Vec3d camera = mc.gameRenderer.getCamera().getPos();
		screenCenter = new Vec3d(camera.x + vector4f.x, camera.y + vector4f.y, camera.z + vector4f.z);

		/*screenCenter = new Vec3d(0.0, -0.0, 1.0)
				.rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
				.rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
				.add(mc.gameRenderer.getCamera().getPos());*/
	}

	public static Vec3d worldSpaceToScreenSpace(Vec3d pos) {
		Camera camera = mc.getEntityRenderDispatcher().camera;
		int displayHeight = mc.getWindow().getHeight();
		int[] viewport = new int[4];
		GL40.glGetIntegerv(GL40.GL_VIEWPORT, viewport);
		Vector3f target = new Vector3f();

		float deltaX = (float) (pos.x - camera.getPos().x);
		float deltaY = (float) (pos.y - camera.getPos().y);
		float deltaZ = (float) (pos.z - camera.getPos().z);

		Vector4f transformedCoordinates = new Vector4f(
				deltaX,
				deltaY,
				deltaZ,
				1.f
		).mul(lastWorldSpaceMatrix);

		Matrix4f matrixProj = new Matrix4f(lastProjectionMatrix);
		Matrix4f matrixModel = new Matrix4f(lastModelViewMatrix);

		matrixProj.mul(matrixModel).project(
				transformedCoordinates.x(),
				transformedCoordinates.y(),
				transformedCoordinates.z(),
				viewport,
				target
		);

		return new Vec3d(
				target.x / mc.getWindow().getScaleFactor(),
				(displayHeight - target.y) / mc.getWindow().getScaleFactor(),
				target.z
		);
	}

	public static Vec3d getRenderPosition(Vec3d pos) {
		double minX = pos.getX() - mc.gameRenderer.getCamera().getPos().x;
		double minY = pos.getY() - mc.gameRenderer.getCamera().getPos().y;
		double minZ = pos.getZ() - mc.gameRenderer.getCamera().getPos().z;
		return new Vec3d(minX, minY, minZ);
	}
}
