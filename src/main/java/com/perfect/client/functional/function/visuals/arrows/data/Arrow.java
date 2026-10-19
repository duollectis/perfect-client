package com.perfect.client.functional.function.visuals.arrows.data;

import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;

public class Arrow {

	private final Animation animation;

	@Getter
	private final PlayerEntity player;

	public Arrow(PlayerEntity player, int time) {
		this.player = player;
		animation = new Animation(Easing.IN_OUT_CUBIC, time);
	}

	public void setVisible(boolean visible) {
		animation.changeDirection(visible);
	}

	public boolean isFinished() {
		return animation.isFinished(false);
	}

	public float getAnimation() {
		return animation.get();
	}
}
