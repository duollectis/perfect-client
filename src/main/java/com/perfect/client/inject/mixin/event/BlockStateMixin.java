package com.perfect.client.inject.mixin.event;

import com.mojang.serialization.MapCodec;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public abstract class BlockStateMixin extends AbstractBlock.AbstractBlockState {
	public BlockStateMixin(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> mapCodec) {
		super(block, propertyMap, mapCodec);
	}

	@Override
	public ActionResult onUse(World world, PlayerEntity player, BlockHitResult hit) {
		BlockState state = (BlockState) (Object) this;

		PerfectClient.EVENT_MANAGER.onBlockInteract(new BlockUpdateEvent(hit.getBlockPos(), state, state));

		return super.onUse(world, player, hit);
	}
}