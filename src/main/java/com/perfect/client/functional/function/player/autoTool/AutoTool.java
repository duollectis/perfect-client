package com.perfect.client.functional.function.player.autoTool;

import com.perfect.client.core.event.events.block.BlockEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.functions.delay.TickDelayUtils;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.mining.ToolUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.text.StringUtils;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;


public class AutoTool extends Function {

	private final AutoToolConfig config = new AutoToolConfig(this);

	private boolean breaking;

	@Getter
	private final TickDelayUtils swapDelay = new TickDelayUtils(() -> config.getSwapDelay().get());

	private SwapInfo swapBackInfo;

	public AutoTool() {
		super("auto-tool", "Auto Tool", Category.PLAYER);
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getSwap().get());
	}

	@Override
	public void onPreTick() {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		if (breaking && !mc.interactionManager.isBreakingBlock() && !isMining()) {
			swapDelay.resetTimer();
			breaking = false;
			swapDelay.update();
		}

		if (swapDelay.isPassed()) {
			if (mc.interactionManager.isBreakingBlock()) {
				swap(mc.interactionManager.currentBreakingPos);
			}
			else if (!breaking && swapBackInfo != null) {
				swapBackInfo.swapBack();
				swapBackInfo = null;
			}
		}

		swapDelay.tick();
	}

	private boolean isMining() {
		return mc.options.attackKey.isPressed() && mc.crosshairTarget instanceof BlockHitResult;
	}

	@Override
	public void onStartBreakingBlock(BlockEvent event) {
		swapDelay.resetTimer();

		if (swapDelay.isPassed()) {
			swap(event.getPos());
		}
	}

	
	private void swap(BlockPos pos) {
		BlockState state = BlockUtils.getState(pos);
		ToolUtils.Enchantment preference = getPreference(pos);

		FindItemResult fir = ToolUtils.getBestTool(state, preference, config.getSwap().get());

		SwapInfo swapInfo = SwapUtils.find(config.getSwap().get(), fir);

		if (swapInfo.isNeedSwap()) {
			swapBackInfo = swapInfo;
			swapInfo.swap();

			swapDelay.update();
			swapDelay.resetTimer();
		}

		breaking = true;
	}

	private ToolUtils.Enchantment getPreference(BlockPos pos) {
		if (config.getEnderChestSilkTouch().get() && BlockUtils.isOf(pos, Blocks.ENDER_CHEST)) {
			return ToolUtils.Enchantment.SILK_TOUCH;
		}
		return config.getPrefer().get();
	}
}
