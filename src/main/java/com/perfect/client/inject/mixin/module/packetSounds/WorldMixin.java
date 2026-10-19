package com.perfect.client.inject.mixin.module.packetSounds;

import com.perfect.client.inject.MixinHooks;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {

	@Inject(
			method = "playSoundAtBlockCenterClient",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$playBreakSound(
			BlockPos pos,
			SoundEvent sound,
			SoundCategory category,
			float volume,
			float pitch,
			boolean useDistance,
			CallbackInfo ci
	) {
		if (MixinHooks.isCancelBreakSound()) {
			MixinHooks.setCancelBreakSound(false);
			ci.cancel();
		}
	}

	@Inject(
			method = "playSound(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$playPlaceSound(
			Entity source,
			BlockPos pos,
			SoundEvent sound,
			SoundCategory category,
			float volume,
			float pitch,
			CallbackInfo ci
	) {
		if (MixinHooks.isCancelPlaceSound()) {
			MixinHooks.setCancelPlaceSound(false);
			ci.cancel();
		}
	}
}
