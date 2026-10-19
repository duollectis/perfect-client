package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements IEntityRenderState {
	@Unique
	private Entity entity;

	@Override
	@Nullable
	public Entity perfect$getEntity() {
		return entity;
	}

	@Override
	public void perfect$setEntity(Entity entity) {
		this.entity = entity;
	}
}