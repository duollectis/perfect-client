package com.perfect.client.functional.function.visuals.breakEsp;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.mining.MiningUtils;
import com.perfect.client.manager.game.mining.PlayerBreakInfo;
import net.minecraft.entity.Entity;

import java.awt.*;

import static com.perfect.client.PerfectClient.clientConfig;


public class BreakESP extends Function {

	private final BreakEspConfig config = new BreakEspConfig(this);

	public BreakESP() {
		super("break-esp", "Break ESP", Category.VISUALS, "Break Highlight");
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null) {
			return;
		}

		for (PlayerBreakInfo breakInfo : Managers.BREAK_INFO.getAll()) {

			Entity player = mc.world.getEntityById(breakInfo.getPlayerId());

			float progress = breakInfo.getProgress(getSpeed());
			float prevProgress = breakInfo.getPrevProgress(getSpeed());

			float lerpProgress = MiningUtils.getRenderProgress(
					prevProgress,
					progress,
					config.getSmoothAnimation().get()
			);

			MiningUtils.render(
					event.getMatrices(),
					breakInfo.getPos(),
					lerpProgress,
					config.getAnimation().get(),
					getLineColor(player, lerpProgress),
					getFillColor(player, lerpProgress),
					config.getCosmetics().get().bundle()
			);
		}
	}

	public MultiColor getLineColor(Entity player, float progress) {
		MultiColor color = getColorIternal(
				config.getStaticLineColor().get(),
				config.getStartLineColor().getColor(),
				config.getEndLineColor().getColor(),
				progress
		);

		if (Managers.FRIEND.isFriend(player)) {
			return getFriendColor(color);
		}

		return color;
	}

	public MultiColor getFillColor(Entity player, float progress) {
		MultiColor color = getColorIternal(
				config.getStaticSideColor().get(),
				config.getStartSideColor().getColor(),
				config.getEndSideColor().getColor(),
				progress
		);

		if (Managers.FRIEND.isFriend(player)) {
			return getFriendColor(color);
		}

		return color;
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

	private MultiColor getFriendColor(MultiColor defaultColor) {
		MultiColor friendColor = new MultiColor(clientConfig.getFriendColor());
		friendColor.setAlpha(defaultColor.getAlpha());
		return friendColor;
	}

	public double getTargetRange() {
		return config.getTargetRange().get();
	}

	public double getMiningRange() {
		return config.getMiningRange().get();
	}

	public float getSpeed() {
		return config.getSpeed().getFloat();
	}

	public boolean getDoubleMine() {
		return config.getDoubleMine().get();
	}
}
