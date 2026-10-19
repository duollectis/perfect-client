package com.perfect.client.functional.function.visuals.animations.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.world.scan.ScanAnimation;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.functional.function.visuals.animations.Animations;
import com.perfect.client.functional.function.visuals.animations.AnimationsConfig;
import net.minecraft.entity.player.PlayerEntity;

public abstract class AnimationsHandler extends FunctionHandler<Animations, AnimationsConfig> {

	public AnimationsHandler(
			Animations function,
			AnimationsConfig config
	) {
		super(function, config);
	}

	protected void addAnimation(
			PlayerEntity player,
			MultiColor lineColor,
			MultiColor sideColor,
			int range,
			int lifeTime,
			boolean totemAnimation,
			boolean waveMode
	) {
		if (mc.player == null || mc.world == null || mc.cameraEntity == null) {
			return;
		}

		Managers.ANIMATION.add(new ScanAnimation(
				player.getBlockPos().down(),
				lineColor,
				sideColor,
				range,
				lifeTime,
				totemAnimation,
				waveMode
		));
	}
}
