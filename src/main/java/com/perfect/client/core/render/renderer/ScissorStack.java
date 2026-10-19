package com.perfect.client.core.render.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perfect.client.manager.system.FunctionManager;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

import java.util.Stack;

import static com.perfect.client.PerfectClient.mc;


public class ScissorStack {

	private final static Stack<ScissorBox> STACK = new Stack<>();

	public static void pushScissor(Matrix3x2fStack matrices, float x, float y, float width, float height) {
		ScissorBox scissorBox;

		if (STACK.empty()) {
			scissorBox = new ScissorBox();
			scissorBox.x2 = mc.getWindow().getWidth();
			scissorBox.y2 = mc.getWindow().getHeight();
		}
		else {
			scissorBox = STACK.peek().copy();
		}

		scissorBox.set(x, y, width + x, height + y);

		enableScissor(matrices, STACK.push(scissorBox));
	}

	private static void enableScissor(Matrix3x2fStack matrices, ScissorBox scissorBox) {
		double scaleFactor = mc.getWindow().getScaleFactor();

		Matrix3x2f matrix = new Matrix3x2f(matrices);

		Vector2f pos1 = matrix.transformPosition(scissorBox.x1, scissorBox.y1, new Vector2f());
		Vector2f pos2 = matrix.transformPosition(scissorBox.x2, scissorBox.y2, new Vector2f());

		int x1 = (int) Math.floor(pos1.x * scaleFactor);
		int y1 = (int) Math.floor(pos1.y * scaleFactor);

		int x2 = (int) Math.ceil(pos2.x * scaleFactor);
		int y2 = (int) Math.ceil(pos2.y * scaleFactor);

		int w = x2 - x1;
		int h = y2 - y1;

		int y = mc.getWindow().getHeight() - y1 - h;

		if (!FunctionManager.testFunction.disableScissor.get()) {
			Render.enableScissor(Math.max(0, x1), Math.max(0, y), Math.max(0, w), Math.max(0, h));

			RenderSystem.enableScissorForRenderTypeDraws(
					Math.max(0, x1),
					Math.max(0, y),
					Math.max(0, w),
					Math.max(0, h)
			);
		}
	}

	public static void popScissor(Matrix3x2fStack matrices) {
		STACK.pop();

		if (STACK.isEmpty()) {
			Render.disableScissor();
			RenderSystem.disableScissorForRenderTypeDraws();
		}
		else {
			enableScissor(matrices, STACK.peek());
		}
	}

	public static void setLast(Matrix3x2fStack matrices) {
		if (!STACK.empty()) {
			enableScissor(matrices, STACK.peek());
		}
	}

	public static void expand(Matrix3x2fStack matrices, float val) {
		expand(matrices, val, val, val, val);
	}

	public static void expand(Matrix3x2fStack matrices, float x1, float y1, float x2, float y2) {
		ScissorBox box = STACK.peek().copy();

		box.x1 -= x1;
		box.y1 -= y1;
		box.x2 += x2;
		box.y2 += y2;

		enableScissor(matrices, box);
	}

	public static class ScissorBox {

		public float x1;
		public float y1;
		public float x2;
		public float y2;

		public void set(float x1, float y1, float x2, float y2) {
			this.x1 = Math.max(x1, this.x1);
			this.y1 = Math.max(y1, this.y1);
			this.x2 = Math.min(x2, this.x2);
			this.y2 = Math.min(y2, this.y2);
		}

		public ScissorBox copy() {
			ScissorBox scissorBox = new ScissorBox();
			scissorBox.x1 = x1;
			scissorBox.x2 = x2;
			scissorBox.y1 = y1;
			scissorBox.y2 = y2;

			return scissorBox;
		}
	}
}
