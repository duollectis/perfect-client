package com.perfect.client.inject.mixin.event;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.perfect.client.PerfectClient.mc;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Unique
	private boolean cancelledMouse;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	private GuiRenderState guiState;

	@Inject(
			method = "renderWorld",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					ordinal = 3
			)
	)
	public void perfect$render3d(
			RenderTickCounter tickCounter,
			CallbackInfo ci,
			@Local(ordinal = 0) Matrix4f projectionMatrix,
			@Local(ordinal = 0) float tickDelta
	) {
		Camera camera = mc.gameRenderer.getCamera();

		MatrixStack matrixStack = new MatrixStack();
		RenderSystem.getModelViewStack().pushMatrix().mul(matrixStack.peek().getPositionMatrix());

		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));

		Renderer3D.lastWorldSpaceMatrix.set(matrixStack.peek().getPositionMatrix());
		Renderer3D.lastModelViewMatrix.set(RenderSystem.getModelViewMatrix());
		Renderer3D.lastProjectionMatrix.set(projectionMatrix);

		PerfectClient.EVENT_MANAGER.onRender3D(new Render3DEvent(matrixStack, tickDelta));

		RenderSystem.getModelViewStack().popMatrix();
	}

	@Inject(
			method = "renderWorld",
			at = @At("TAIL")
	)
	public void perfect$renderWorld(RenderTickCounter tickCounter, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onPostProcess();
	}

	@Inject(
			method = "preloadPrograms(Lnet/minecraft/resource/ResourceFactory;)V",
			at = @At("TAIL")
	)
	public void perfect$preloadPrograms(ResourceFactory factory, CallbackInfo ci) {
		PerfectClient.onInitializePostProcess();
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/render/GuiRenderer;incrementFrame()V"
			)
	)
	public void perfect$onRenderGui(
			RenderTickCounter tickCounter,
			boolean tick,
			CallbackInfo ci
	) {
		RenderUtils.set2DProjection();

		DrawContext context = new DrawContext(client, guiState);
		PerfectClient.EVENT_MANAGER.onGuiRender(new Render2DEvent(context));

		RenderUtils.pop2DProjection();
	}

	@Inject(
			method = "render",
			at = @At("HEAD")
	)
	public void perfect$render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
		cancelledMouse = false;

		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onScreenMouse(event);

		if (event.isCancelled()) {
			cancelledMouse = true;
		}
	}

	@ModifyArgs(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Overlay;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"
			)
	)
	public void perfect$renderOverlay(Args args) {
		if (cancelledMouse) {
			args.set(1, -100);
			args.set(2, -100);
		}
	}

	@ModifyArgs(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"
			)
	)
	public void perfect$renderWithTooltip(Args args) {
		if (cancelledMouse) {
			args.set(1, -100);
			args.set(2, -100);
		}
	}
}
