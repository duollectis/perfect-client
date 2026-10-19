package com.perfect.client.functional.function.player.packetMine;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.mining.MiningUtils;
import com.perfect.client.core.utils.functions.mining.RebreakMode;
import com.perfect.client.core.utils.functions.mining.ToolUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import com.perfect.client.functional.function.player.packetMine.data.RebreakInfo;
import com.perfect.client.functional.function.player.packetMine.handlers.AttackBlockHandler;
import com.perfect.client.functional.function.player.packetMine.handlers.RebreakHandler;
import com.perfect.client.functional.function.player.packetMine.handlers.Render3DHandler;
import com.perfect.client.functional.function.player.packetMine.helper.PacketHelper;
import com.perfect.client.functional.function.player.packetMine.helper.SwapHelper;
import com.perfect.client.manager.game.mining.MiningSequence;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Predicate;

import static com.perfect.client.manager.system.FunctionManager.autoMine;


public class PacketMine extends Function {

	private static final float CC_TICKS_BOOST = 2f;

	private final PacketMineConfig config = new PacketMineConfig(this);

	private final MiningSequence<ClientBreakInfo> miningSequence = new MiningSequence<>();

	private final PacketHelper packetHelper = new PacketHelper(this, config);
	private final SwapHelper swapHelper = new SwapHelper(this, config);

	private final RebreakHandler rebreakHandler = addHandler(new RebreakHandler(this, config, miningSequence));
	private final Render3DHandler render3DHandler = addHandler(new Render3DHandler(this, config, miningSequence));

	@Getter
	private BlockPos lastClicked;

	public PacketMine() {
		super("packet-mine", "Packet Mine", Category.PLAYER, "Speed Mine", "Auto Mine");

		addHandler(new AttackBlockHandler(this, config, miningSequence));
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getSwap().get());
	}

	public void onDoubleMineConfig(boolean doubleMine) {
		miningSequence.onDoubleMineConfig(doubleMine);
	}

	@Override
	public void onDisable() {
		reset();
	}

	@Override
	public void onGameJoin() {
		reset();
	}

	private void reset() {
		miningSequence.clear();
		lastClicked = null;
	}

	
	@Override
	public void onPreTick() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		rebreakHandler.tick();

		for (ClientBreakInfo info : miningSequence.getAll()) {

			if (isOutOfRange(info)) {
				miningSequence.remove(info);
				continue;
			}

			BlockState state = BlockUtils.getState(info.getPos());

			if (!BlockUtils.isBlock(state)) {
				rebreakHandler.setRebreakInfo(info, true);
				rebreakHandler.setTicks(info.getTicks() + 1);

				miningSequence.remove(info);
				continue;
			}

			FindItemResult tool = getTool(state);

			if (miningSequence.getProgress(info, config.getSpeed().getFloat()) >= 1f) {
				if (BlockUtils.isBlock(info.getPos()) && !shouldPause()) {
					if (!swapHelper.isSwapped(info)) {
						swap(info, tool);
					}

					info.updatePrevProgress();
					breakBlock(info);
				}
				continue;
			}

			float blockStrength = getBlockStrength(info.getPos(), state, tool);

			if (isStrictSwap(info) && isPreSwap(info, blockStrength)) {
				swap(info, tool);
			}

			info.addProgress(blockStrength);
			info.updateState();
			info.tick();
		}

		swapHelper.tick();
	}

	private boolean isOutOfRange(ClientBreakInfo info) {
		return PositionMath.isOutOfRange(
				PositionMath.closestVec3d(mc.player, info.getPos()),
				config.getRange().get()
		);
	}

	private void swap(ClientBreakInfo info, FindItemResult tool) {
		SwapInfo swapInfo = SwapUtils.find(config.getSwap().get(), tool);
		swapHelper.swap(info, swapInfo);
	}

	private boolean isPreSwap(ClientBreakInfo info, float blockStrength) {
		int swapTimePre = Math.max(0, (config.getSwapTime().getFrom() * -1));

		float progress = info.getProgress() + blockStrength * swapTimePre;
		return miningSequence.getProgress(progress, info, config.getSpeed().getFloat()) >= 1f;
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket) {
			onSwap();
		}
	}

	public boolean isStrictSwap(ClientBreakInfo info) {
		if (config.getNewVersion().get()) {
			return true;
		}
		if (config.getDoubleMine().get()) {
			return !miningSequence.isFreeMining(info) && info.getTicks() > 0;
		}
		return false;
	}

	private void onSwap() {
		if (!config.getResetOnSwap().get()) {
			return;
		}

		List<ClientBreakInfo> breakInfoList = miningSequence.getAll();
		miningSequence.clear();

		rebreakHandler.restartRebreak();

		for (ClientBreakInfo breakInfo : breakInfoList) {
			if (BlockUtils.isBlock(breakInfo.getPos())) {
				startBreaking(breakInfo);
			}
		}
	}

	public void startBreaking(ClientBreakInfo info) {
		startBreaking(info.getPos(), info.getFacing(), info.getPriority(), info.getFunctionId());
	}

	
	public void startBreaking(BlockPos pos, Direction facing, int priority, String functionId) {
		if (!isEnabled()) {
			return;
		}

		ClientBreakInfo info = new ClientBreakInfo(pos, facing, priority, functionId);

		if (!miningSequence.put(info)) {
			return;
		}

		resetRebreak();

		if (!BlockUtils.isBlock(pos)) {
			miningSequence.remove(info);

			if (!miningSequence.isEmpty()) {
				return;
			}
			rebreakHandler.setRebreakInfo(info, true);
		}

		miningSequence.getAll().forEach(breakInfo -> breakInfo.setClickedLast(false));
		info.setClickedLast(true);

		packetHelper.sendBlockStartPackets(pos, facing);
		sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

		lastClicked = pos;
	}

	public void resetRebreak() {
		rebreakHandler.reset();
	}

	
	private void breakBlock(ClientBreakInfo info) {
		if (mc.player == null) {
			return;
		}

		BlockPos pos = info.getPos();
		Direction facing = info.getFacing();
		BlockState state = BlockUtils.getState(pos);

		if (config.getRotate().get()) {
			Vec3d rotateVec3d = pos.toCenterPos().add(0, 0.5, 0);
			Managers.ROTATION.packetRotate(rotateVec3d);
		}

		autoMine.placeCrystal(pos);
		render3DHandler.renderBreak(pos);

		packetHelper.sendBlockBreakPackets(pos, facing);
		mc.player.swingHand(Hand.MAIN_HAND);

		swapHelper.swapBack(info);

		miningSequence.remove(info); // TODO: заменить на атемпты

		if (info.isClickedLast()) {
			rebreakHandler.setRebreakInfo(info, state, false);
		}
	}

	public FindItemResult getTool(BlockState state) {
		if (mc.player == null || mc.world == null) {
			return FindItemResult.fromSelectedSlot();
		}

		FindItemResult tool = ToolUtils.getBestTool(
				state,
				ToolUtils.Enchantment.SILK_TOUCH,
				config.getSwap().get()
		);

		if (tool.found()) {
			return tool;
		}
		else {
			return FindItemResult.fromSelectedSlot();
		}
	}

	public float getBlockStrength(BlockPos pos, BlockState state) {
		return getBlockStrength(pos, state, getTool(state));
	}

	public float getBlockStrength(BlockPos pos, BlockState state, FindItemResult tool) {
		if (mc.player == null) {
			return 0f;
		}

		float blockStrength = MiningUtils.getBlockStrength(
				mc.player,
				mc.player.isOnGround(),
				tool.stack(),
				state,
				state.getHardness(mc.world, pos)
		);

		if (config.getCcSpeed().get() && mc.player.isOnGround()) {
			float breakTicks = (float) Math.ceil(1f / blockStrength);
			float ccBreakTicks = breakTicks - CC_TICKS_BOOST;
			blockStrength = 1f / ccBreakTicks;
		}

		return blockStrength;
	}

	private boolean shouldPause() {
		return PlayerUtils.shouldPause(
				config.getPauseOnEat().get(),
				config.getPauseOnBlink().get()
		);
	}

	public RebreakInfo getRebreakInfo() {
		return rebreakHandler.getRebreakInfo();
	}

	public boolean isMining(BlockPos pos) {
		if (!isEnabled()) {
			return false;
		}
		return miningSequence.containsPos(pos);
	}

	public void removePos(BlockPos pos) {
		removeIf(info -> info.getPos().equals(pos));
	}

	public void removeIf(Predicate<ClientBreakInfo> predicate) {
		miningSequence.removeIf(predicate);
	}

	public boolean isFull() {
		return miningSequence.isFull();
	}

	public List<ClientBreakInfo> getAll() {
		return miningSequence.getAll();
	}

	public RebreakMode getRebreak() {
		return config.getRebreak().get();
	}

	public int getRebreakTicks() {
		return rebreakHandler.getTicks();
	}

	public float getFastRebreakProgress(float blockStrength, int extrapolation) {
		return rebreakHandler.getFastRebreakProgress(blockStrength, extrapolation);
	}

	public boolean isAirPreBreaking() {
		return rebreakHandler.getRebreakInfo().isAirPreBreak();
	}

	public boolean canAddExtra(int priority) {
		return miningSequence.canAddExtra(priority);
	}
}