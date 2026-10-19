package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(WeatherRendering.class)
public class WeatherRenderingMixin {

	@Inject(
			method = "renderPrecipitation(Lnet/minecraft/world/World;Lnet/minecraft/client/render/VertexConsumerProvider;IFLnet/minecraft/util/math/Vec3d;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$renderWeather(
			World world,
			VertexConsumerProvider vertexConsumers,
			int ticks,
			float tickProgress,
			Vec3d pos,
			CallbackInfo ci
	) {
		if (noRender.contains(Elements.WEATHER)) {
			ci.cancel();
		}
	}
}
