package com.perfect.client.inject.mixin.module.packetMine;

import com.perfect.client.core.utils.functions.mining.MiningUtils;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.packetMine;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Shadow
	private BlockPos currentBreakingPos;

	@Inject(
			method = "cancelBlockBreaking",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
			),
			cancellable = true
	)
	public void perfect$sendPacket(CallbackInfo ci) {
		if (packetMine.isEnabled() && MiningUtils.canBreak(currentBreakingPos)) {
			ci.cancel();
		}
	}
}
