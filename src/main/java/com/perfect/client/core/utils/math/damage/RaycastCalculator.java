package com.perfect.client.core.utils.math.damage;

import com.perfect.client.core.utils.game.world.BlockUtils;
import lombok.Setter;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.perfect.client.PerfectClient.mc;

@Setter

public class RaycastCalculator {

    public BlockHitResult getRaycastResult(
            RaycastContext context,
            boolean ignoreTerrain,
            boolean placing,
            BlockPos ignored,
            List<BlockPos> support
    ) {
        if (mc.world == null) {
            return null;
        }

        BiFunction<RaycastContext, BlockPos, BlockHitResult> blockHitFactory = getBlockHitFactory(
                ignored,
                ignoreTerrain,
                placing,
                support
        );

        return BlockView.raycast(context.getStart(), context.getEnd(), context, blockHitFactory, getMissFactory());
    }

    private BiFunction<RaycastContext, BlockPos, BlockHitResult> getBlockHitFactory(
            BlockPos ignored,
            boolean ignoreTerrain,
            boolean placing,
            List<BlockPos> support
    ) {
        if (mc.world == null) {
            return null;
        }

        return (raycastContext, pos) -> raycastBlock(raycastContext, pos, ignored, ignoreTerrain, placing, support);
    }

    private BlockHitResult raycastBlock(
            RaycastContext context,
            BlockPos pos,
            BlockPos ignored,
            boolean ignoreTerran,
            boolean placing,
            List<BlockPos> support
    ) {
        if (mc.world == null) {
            return null;
        }

        BlockState state = getBlockState(context, pos, ignored, ignoreTerran, placing, support);

        return mc.world.raycastBlock(
                context.getStart(),
                context.getEnd(),
                pos,
                context.getBlockShape(state, mc.world, pos),
                state
        );
    }

    private Function<RaycastContext, BlockHitResult> getMissFactory() {
        return raycastContext -> {
            Direction direction = getRaycastFacing(raycastContext);

            return BlockHitResult.createMissed(
                    raycastContext.getEnd(),
                    direction,
                    getCollisionPos(raycastContext)
            );
        };
    }

    private BlockPos.Mutable getCollisionPos(RaycastContext context) {
        return new BlockPos.Mutable(
                context.getEnd().x,
                context.getEnd().y,
                context.getEnd().z
        );
    }

    private Direction getRaycastFacing(RaycastContext context) {
        return Direction.getFacing(
                context.getStart().x - context.getEnd().x,
                context.getStart().y - context.getEnd().y,
                context.getStart().z - context.getEnd().z
        );
    }

    private BlockState getBlockState(
            RaycastContext raycastContext,
            BlockPos pos,
            BlockPos ignored,
            boolean ignoreTerrain,
            boolean placing,
            List<BlockPos> support
    ) {
        return modifyBlockState(
                pos,
                getCollisionPos(raycastContext),
                ignored,
                ignoreTerrain,
                placing,
                support
        );
    }

    private BlockState modifyBlockState(
            BlockPos pos,
            BlockPos collisionPos,
            BlockPos ignored,
            boolean ignoreTerrain,
            boolean placing,
            List<BlockPos> support
    ) {
        if (mc.world == null) {
            return getDefaultState();
        }

        BlockState blockState = mc.world.getBlockState(pos);

        if (pos.equals(ignored)) {
            return getDefaultState();
        }
        if (isObsidianPos(pos, collisionPos, placing, support)) {
            return Blocks.OBSIDIAN.getDefaultState();
        }
        if (ignoreTerrain && !BlockUtils.isBlastResist(blockState.getBlock())) {
            return getDefaultState();
        }
        if (isExplodePos(pos, collisionPos)) {
            return getDefaultState();
        }
        if (isBedBlock(pos, collisionPos)) {
            return getDefaultState();
        }

        return blockState;
    }

    private BlockState getDefaultState() {
        return Blocks.AIR.getDefaultState();
    }

    private boolean isObsidianPos(BlockPos pos, BlockPos collisionPos, boolean placing, List<BlockPos> support) {
        if (!placing) {
            return false;
        }
        if (pos.equals(collisionPos.down())) {
            return true;
        }

        return support != null && support.contains(pos);
    }

    private boolean isExplodePos(BlockPos pos, BlockPos collisionPos) {
        if (!pos.equals(collisionPos)) {
            return false;
        }
        return BlockUtils.isOf(pos, Blocks.RESPAWN_ANCHOR) || BlockUtils.getBlock(pos) instanceof BedBlock;
    }

    private boolean isBedBlock(BlockPos pos, BlockPos collisionPos) {
        if (!(BlockUtils.getBlock(pos) instanceof BedBlock)) {
            return false;
        }
        if (!(BlockUtils.getBlock(collisionPos) instanceof BedBlock)) {
            return false;
        }

        return pos.offset(BedBlock.getOppositePartDirection(BlockUtils.getState(pos))).equals(collisionPos);
    }
}
