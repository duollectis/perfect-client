package com.perfect.client.functional.function.combat.autoCrystal.handlers;

import com.perfect.client.core.event.events.entity.EntityEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import net.minecraft.entity.decoration.EndCrystalEntity;

public class EntityRemovedHandler extends FunctionHandler<AutoCrystal, AutoCrystalConfig> {

	public EntityRemovedHandler(AutoCrystal function, AutoCrystalConfig config) {
		super(function, config);
	}

	@Override
	public void onEntityRemove(EntityEvent event) {
		if (!(event.getEntity() instanceof EndCrystalEntity crystal)) {
			return;
		}

		function.getBreakLimitedCrystals().remove(crystal);
		function.getClickedCrystals().remove(crystal);
	}
}
