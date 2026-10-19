package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IMultiPhaseParameters;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public abstract class MultiPhaseParametersMixin implements IMultiPhaseParameters {

	@Shadow
	@Final
	private RenderPhase.Target target;

	@Override
	public RenderPhase.Target perfect$getTarget() {
		return this.target;
	}
}