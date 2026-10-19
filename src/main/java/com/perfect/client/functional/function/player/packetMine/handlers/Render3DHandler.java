package com.perfect.client.functional.function.player.packetMine.handlers;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.mining.MiningUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.functional.function.player.packetMine.PacketMine;
import com.perfect.client.functional.function.player.packetMine.PacketMineConfig;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import com.perfect.client.functional.function.player.packetMine.data.RebreakInfo;
import com.perfect.client.functional.function.player.packetMine.utils.PacketMineHandler;
import com.perfect.client.manager.game.mining.MiningSequence;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;

public class Render3DHandler extends PacketMineHandler {

	private final PlaceRenderer breakRenderer = new PlaceRenderer(
			() -> getLineColor(1f),
			() -> getFillColor(1f),
			() -> config.getCosmetics().get().bundle()
	);

	public Render3DHandler(
			PacketMine function,
			PacketMineConfig config,
			MiningSequence<ClientBreakInfo> miningSequence
	) {
		super(function, config, miningSequence);
	}

	public void renderBreak(BlockPos pos) {
		if (!config.getRender().get()) {
			return;
		}
		if (!config.getCosmetics().get().isAnimationEnabled()) {
			return;
		}

		Box box = BlockUtils.getOutlineShapeBox(pos);

		if (box == null) {
			return;
		}

		breakRenderer.add(box);
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (!config.getRender().get()) {
			return;
		}

		for (ClientBreakInfo info : miningSequence.getAll()) {
			if (!BlockUtils.isBlock(info.getPos())) {
				continue;
			}

			float speed = config.getSpeed().getFloat();

			float progress = miningSequence.getProgress(info, speed);
			float prevProgress = miningSequence.getPrevProgress(info, speed);

			float lerpProgress = MiningUtils.getRenderProgress(
					prevProgress,
					progress,
					config.getSmoothAnimation().get()
			);

			MiningUtils.render(
					event.getMatrices(),
					info.getPos(),
					lerpProgress,
					config.getAnimation().get(),
					getLineColor(lerpProgress),
					getFillColor(lerpProgress),
					config.getCosmetics().get().bundle()
			);
		}

		if (config.getRenderRebreak().get()) {
			renderRebreak(event.getMatrices());
		}
	}

	private void renderRebreak(MatrixStack matrices) {
		RebreakInfo rebreakInfo = function.getRebreakInfo();

		if (rebreakInfo == null) {
			return;
		}
		if (rebreakInfo.isAirPreBreak()) {
			return;
		}
		if (rebreakInfo.getBlockState() == null) {
			return;
		}

		Box box = BlockUtils.getOutlineShapeBox(
				rebreakInfo.getPos(),
				rebreakInfo.getBlockState()
		);

		if (box == null) {
			return;
		}

		BoxRenderer.drawBox(
				matrices,
				box,
				getLineColor(1f),
				getFillColor(1f),
				config.getCosmetics().get().bundle()
		);
	}

	private MultiColor getLineColor(float progress) {
		return getColorIternal(
				config.getStaticLineColor().get(),
				config.getStartLineColor().getColor(),
				config.getEndLineColor().getColor(),
				progress
		);
	}

	private MultiColor getFillColor(float progress) {
		return getColorIternal(
				config.getStaticSideColor().get(),
				config.getStartSideColor().getColor(),
				config.getEndSideColor().getColor(),
				progress
		);
	}

	private MultiColor getColorIternal(
			MultiColor staticColor,
			Color startColor,
			Color endColor,
			float progress
	) {
		return MiningUtils.getColor(
				config.getColorMode().get(),
				staticColor,
				startColor,
				endColor,
				progress,
				config.getSmoothColor().get()
		);
	}
}
