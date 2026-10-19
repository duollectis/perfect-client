package com.perfect.client.functional.function.combat.blocker.modes.blind;

import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.functions.mining.MiningUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.blocker.Blocker;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import static com.perfect.client.PerfectClient.mc;

@Getter

public class PendingBlock {

	private final PlayerEntity enemy;
	private final BlindMode.Protect protect;
	private float progress = 0f;

	public PendingBlock(BlindMode.Protect protect, PlayerEntity enemy) {
		this.protect = protect;
		this.enemy = enemy;
	}

	
	public void addProgress(BlockPos pos) {
		if (progress >= 1.0f) {
			return;
		}

		ItemStack stack = new ItemStack(Items.NETHERITE_PICKAXE);

		EnchantmentUtils.add(stack, Enchantments.EFFICIENCY, 5);
		EnchantmentUtils.add(stack, Enchantments.EFFICIENCY, 5);

		BlockState state = BlockUtils.getState(pos);

		progress += MiningUtils.getBlockStrength(
				enemy,
				true,
				stack,
				state,
				state.getHardness(mc.world, pos)
		);
	}

	
	public boolean isProtected(BlockPos pos) {
		return switch (protect) {
			case SELF_TRAP -> !Blocker.canPlaceAt(pos.up());
			case SURROUND -> {
				for (BlockPos surroundPos : PositionMath.getSurroundingBlocks(pos)) {
					if (Blocker.canPlaceAt(surroundPos)) {
						yield false;
					}
				}
				yield true;
			}
			default -> false;
		};
	}
}