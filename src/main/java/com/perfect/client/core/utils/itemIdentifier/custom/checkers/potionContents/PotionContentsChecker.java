package com.perfect.client.core.utils.itemIdentifier.custom.checkers.potionContents;

import com.perfect.client.core.utils.itemIdentifier.custom.CustomItemChecker;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PotionContentsChecker extends CustomItemChecker<StatusEffectLevel> {

	public PotionContentsChecker(StatusEffectLevel... components) {
		super(components);
	}

	@Override
	public boolean check(ItemStack stack) {
		PotionContentsComponent potionContents = stack.get(DataComponentTypes.POTION_CONTENTS);

		if (potionContents == null) {
			return false;
		}

		List<StatusEffectLevel> notFoundLevels = new ArrayList<>(components);

		for (StatusEffectInstance effectInstance : potionContents.getEffects()) {
			StatusEffectLevel level = new StatusEffectLevel(
					effectInstance.getEffectType(),
					effectInstance.getDuration(),
					effectInstance.getAmplifier() + 1
			);

			notFoundLevels.remove(level);
		}

		return notFoundLevels.isEmpty();
	}
}
