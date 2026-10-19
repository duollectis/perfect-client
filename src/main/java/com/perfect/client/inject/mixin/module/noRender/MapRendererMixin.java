package com.perfect.client.inject.mixin.module.noRender;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.map.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(MapRenderer.class)
public class MapRendererMixin {

	@ModifyExpressionValue(method = "draw",
	                       at = @At(value = "FIELD",
	                                target = "Lnet/minecraft/client/render/MapRenderState;decorations:Ljava/util/List;"))
	private List<MapDecoration> perfect$noMapMarkers(List<MapDecoration> original) {
		if (noRender.contains(Elements.MAP_MARKERS)) {
			return Collections.emptyList();
		}

		return original;
	}

	@Inject(method = "draw",
	        at = @At("HEAD"),
	        cancellable = true)
	private void perfect$draw(
			MapRenderState state,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumerProvider,
			boolean bl,
			int light,
			CallbackInfo ci
	) {
		if (noRender.contains(Elements.MAP_CONTENT)) {
			ci.cancel();
		}
	}
}