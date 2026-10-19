package com.perfect.client.manager.game.mining;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.collection.ListRemoveHashMap;
import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.functions.mining.MiningUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.perfect.client.manager.system.FunctionManager.antiBot;
import static com.perfect.client.manager.system.FunctionManager.breakESP;


public class BreakInfoManager extends Manager {

	private final ListRemoveHashMap<Integer, MiningSequence<BreakInfo>> miningSequenceMap = new ListRemoveHashMap<>();

	private boolean doubleMine;

	@Override
	public void onGameLeft() {
		reset();
	}

	
	@Override
	public void onPreTick() {
		if (mc.world == null) {
			reset();
			return;
		}

		if (doubleMine != breakESP.getDoubleMine()) {
			doubleMine = breakESP.getDoubleMine();
			onConfigUpdate(doubleMine);
		}

		List<Integer> keysToRemove = new ArrayList<>();

		for (Map.Entry<Integer, MiningSequence<BreakInfo>> entry : miningSequenceMap.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			int entityId = entry.getKey();

			MiningSequence<BreakInfo> miningSequence = entry.getValue();
			List<BreakInfo> breakInfoList = miningSequence.getAll();

			for (BreakInfo breakInfo : breakInfoList) {
				Entity entity = mc.world.getEntityById(entityId);

				if (!(entity instanceof PlayerEntity player)) {
					continue;
				}

				BlockState state = getCalculationState(breakInfo);

				float blockStrength = MiningUtils.getBlockStrength(
						player,
						true,
						getCalculationStack(),
						state,
						state.getHardness(mc.world, breakInfo.getPos())
				);

				if (needRemove(miningSequence, entityId, breakInfo)) {
					miningSequence.remove(breakInfo);
				}

				breakInfo.updateState();
				breakInfo.addProgress(blockStrength);
			}

			if (miningSequence.isEmpty()) {
				keysToRemove.add(entityId);
			}
		}

		miningSequenceMap.removeAll(keysToRemove);
	}

	private void onConfigUpdate(boolean doubleMine) {
		for (Map.Entry<Integer, MiningSequence<BreakInfo>> entry : miningSequenceMap.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			entry.getValue().onDoubleMineConfig(doubleMine);
		}
	}

	private ItemStack getCalculationStack() {
		ItemStack stack = new ItemStack(Items.NETHERITE_PICKAXE);
		stack.addEnchantment(EnchantmentUtils.getEntry(Enchantments.EFFICIENCY), 5);
		return stack;
	}

	public BlockState getCalculationState(BreakInfo breakInfo) {
		BlockState state = BlockUtils.getState(breakInfo.getPos());

		if (!BlockUtils.isBlock(state)) {
			return Blocks.OBSIDIAN.getDefaultState();
		}

		return state;
	}

	private boolean needRemove(MiningSequence<BreakInfo> miningSequence, int playerId, BreakInfo info) {
		assert mc.world != null;

		Entity entity = mc.world.getEntityById(playerId);

		if (entity == null || entity.isRemoved()) {
			return true;
		}
		if (isOutOfRanges(entity, info.getPos())) {
			return true;
		}
		if (BlockUtils.isBlock(info.getBlockState()) && !BlockUtils.isBlock(info.getPos())) {
			return true;
		}
		return miningSequence.getProgress(info, breakESP.getSpeed()) >= 1f;
	}

	public List<PlayerBreakInfo> getAll() {
		List<PlayerBreakInfo> breakInfoList = new ArrayList<>();

		for (Map.Entry<Integer, MiningSequence<BreakInfo>> entry : miningSequenceMap.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			int entityId = entry.getKey();
			MiningSequence<BreakInfo> miningSequence = entry.getValue();

			for (BreakInfo info : miningSequence.getAll()) {
				breakInfoList.add(new PlayerBreakInfo(entityId, miningSequence, info));
			}
		}

		return breakInfoList;
	}

	public void reset() {
		miningSequenceMap.clear();
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof BlockBreakingProgressS2CPacket packet) {
			onBlockBreakingProgressPacket(packet);
		}
	}

	
	private void onBlockBreakingProgressPacket(BlockBreakingProgressS2CPacket packet) {
		int entityId = packet.getEntityId();
		int progress = packet.getProgress();
		BlockPos pos = packet.getPos();

		if (!shouldAddBreaking(entityId, pos, progress)) {
			return;
		}

		BreakInfo breakInfo = new BreakInfo(pos, 0);

		if (miningSequenceMap.containsKey(entityId)) {
			miningSequenceMap.get(entityId).put(breakInfo);
		}
		else {
			MiningSequence<BreakInfo> miningSequence = new MiningSequence<>();
			miningSequence.put(breakInfo);
			miningSequence.onDoubleMineConfig(doubleMine);

			miningSequenceMap.put(entityId, miningSequence);
		}
	}

	private boolean shouldAddBreaking(int entityId, BlockPos pos, int progress) {
		if (mc.world == null) {
			return false;
		}

		Entity entity = mc.world.getEntityById(entityId);

		if (!(entity instanceof PlayerEntity)) {
			return false;
		}
		if (entity.equals(mc.player)) {
			return false;
		}
		if (antiBot.isBot(entity)) {
			return false;
		}
		if (isOutOfRanges(entity, pos)) {
			return false;
		}
		return progress == 0;
	}

	private boolean isOutOfRanges(Entity player, BlockPos pos) {
		Vec3d vec3d = pos.toCenterPos();

		if (!isInTargetRange(vec3d)) {
			return true;
		}

		return !isInMiningRange(player, vec3d);
	}

	private boolean isInTargetRange(Vec3d vec3d) {
		return !PositionMath.isOutOfRange(vec3d, breakESP.getTargetRange());
	}

	private boolean isInMiningRange(Entity player, Vec3d vec3d) {
		return !PositionMath.isOutOfRange(player.getEyePos(), vec3d, breakESP.getMiningRange());
	}
}
