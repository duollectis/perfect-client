package com.perfect.client.inject.mixin.module.packetSounds;

import com.perfect.client.inject.MixinHooks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.packetSounds;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(
			method = "onBreak",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/Block;spawnBreakParticles(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
			)
	)
	private void perfect$beforeBreakParticles(
			World world,
			BlockPos pos,
			BlockState state,
			PlayerEntity player,
			CallbackInfoReturnable<BlockState> cir
	) {
		if (packetSounds.disableClientBreakSound()) {
			MixinHooks.setCancelBreakSound(true);
		}
	}

	@Inject(
			method = "onBreak",
			at = @At(
					shift = At.Shift.AFTER,
					value = "INVOKE",
					target = "Lnet/minecraft/block/Block;spawnBreakParticles(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
			)
	)
	private void perfect$afterBreakParticles(
			World world,
			BlockPos pos,
			BlockState state,
			PlayerEntity player,
			CallbackInfoReturnable<BlockState> cir
	) {
		MixinHooks.setCancelBreakSound(false);
		MixinHooks.setCancelBreakParticles(false);
	}
}
