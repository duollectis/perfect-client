package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {

	@Shadow protected abstract void syncSelectedSlot();

	@Override
	public void perfect$syncSelectedSlot() {
		syncSelectedSlot();
	}
}
