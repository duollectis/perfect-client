package com.perfect.client.inject.mixin.impl;


import com.perfect.client.inject.mixinInterface.IMultiPhase;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderLayer.MultiPhase.class)
public abstract class MultiPhaseMixin implements IMultiPhase {

	@Shadow
	@Final
	private RenderLayer.MultiPhaseParameters phases;

	@Override
	public RenderLayer.MultiPhaseParameters perfect$getParameters() {
		return this.phases;
	}
}