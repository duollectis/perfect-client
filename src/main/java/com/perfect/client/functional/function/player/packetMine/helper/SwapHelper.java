package com.perfect.client.functional.function.player.packetMine.helper;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.functional.function.player.packetMine.PacketMine;
import com.perfect.client.functional.function.player.packetMine.PacketMineConfig;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import com.perfect.client.functional.function.player.packetMine.data.TimedSwapInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwapHelper extends FunctionHelper<PacketMine, PacketMineConfig> {

	private final HashMap<ClientBreakInfo, TimedSwapInfo> swapMap = new HashMap<>();

	public SwapHelper(PacketMine function, PacketMineConfig config) {
		super(function, config);
	}

	public boolean isSwapped(ClientBreakInfo breakInfo) {
		return swapMap.containsKey(breakInfo);
	}

	public void swap(ClientBreakInfo breakInfo, SwapInfo swapInfo) {
		if (!swapInfo.isFound() || !swapInfo.isNeedSwap()) {
			return;
		}

		swapInfo.swap();

		TimedSwapInfo swap = new TimedSwapInfo(swapInfo);
		swapMap.put(breakInfo, swap);
	}

	public void swapBack(ClientBreakInfo breakInfo) {
		if (function.isStrictSwap(breakInfo)) {
			TimedSwapInfo swapInfo = swapMap.get(breakInfo);

			if (swapInfo != null) {
				swapInfo.setMined();
			}
		}
		else {
			swapBackIternal(breakInfo);
		}
	}

	
	public void tick() {
		List<ClientBreakInfo> swapBackList = new ArrayList<>();

		for (Map.Entry<ClientBreakInfo, TimedSwapInfo> entry : swapMap.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			ClientBreakInfo breakInfo = entry.getKey();
			TimedSwapInfo swapInfo = entry.getValue();

			if (!swapInfo.isMined()) {
				continue;
			}

			if (swapInfo.getTicksPost() >= Math.max(0, config.getSwapTime().getTo())) {
				swapBackList.add(breakInfo);
			}
			swapInfo.tick();
		}

		swapBackList.forEach(this::swapBackIternal);
	}

	private void swapBackIternal(ClientBreakInfo info) {
		TimedSwapInfo swapInfo = swapMap.get(info);

		if (swapInfo == null) {
			return;
		}

		swapMap.remove(info);

		if (swapMap.isEmpty()) {
			swapInfo.getSwap().swapBack();
		}
	}
}
