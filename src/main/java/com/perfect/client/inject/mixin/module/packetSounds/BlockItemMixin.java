package com.perfect.client.inject.mixin.module.packetSounds;

import com.perfect.client.inject.MixinHooks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.packetSounds;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Inject(
			method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"
			)
	)
	public void perfect$beforePlaySound(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (packetSounds.disableClientPlaceSound()) {
			MixinHooks.setCancelPlaceSound(true);
		}
	}
}
