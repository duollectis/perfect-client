package com.perfect.client.inject.mixinInterface;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface IEntityRenderState {

	@Nullable
	Entity perfect$getEntity();

	void perfect$setEntity(Entity entity);
}