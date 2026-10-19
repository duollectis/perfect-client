package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.entity.FinishUsingItemEvent;
import com.perfect.client.core.event.events.render.TooltipRenderEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(
			method = "finishUsing",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$finishUsing(
			ItemStack stack,
			World world,
			LivingEntity user,
			CallbackInfoReturnable<ItemStack> cir
	) {
		FinishUsingItemEvent event = new FinishUsingItemEvent(user, stack);
		PerfectClient.EVENT_MANAGER.onFinishUsingItem(event);

		if (event.isCancelled()) {
			cir.setReturnValue(event.getStack());
		}
	}

	@Inject(
			method = "getTooltipData",
			at = @At("HEAD"),
			cancellable = true
	)
	private void onTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
		TooltipRenderEvent event = new TooltipRenderEvent(null, stack);
		PerfectClient.EVENT_MANAGER.onRenderTooltip(event);

		TooltipData tooltipData = event.getTooltipData();
		if (tooltipData != null) {
			cir.setReturnValue(Optional.of(tooltipData));
		}
	}
}
