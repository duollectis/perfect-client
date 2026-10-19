package com.perfect.client.core.render.animation.world.scan;

import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.animation.world.scan.direction.DrawDirection;
import com.perfect.client.core.render.animation.world.scan.direction.DrawDirections;
import com.perfect.client.core.utils.game.world.BlockUtils;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScanBlock {

	private final List<DrawDirection> directions = new ArrayList<>();

	@Getter
	private final BlockPos pos;
	@Getter
	private final Color lineColor;
	@Getter
	private final Color sideColor;
	private final float rangeFactor;

	private final Animation opacityAnimation;
	private final Animation positionAnimation;

	public ScanBlock(BlockPos pos, int lifetime, float rangeFactor, Color lineColor, Color sideColor) {
		this.pos = pos;
		this.rangeFactor = rangeFactor;
		this.lineColor = lineColor;
		this.sideColor = sideColor;

		opacityAnimation = new Animation(Easing.LINEAR, lifetime);
		positionAnimation = new Animation(Easing.IN_OUT_SINE, lifetime / 3);

		for (Direction direction : Direction.values()) {
			if (BlockUtils.isReplaceable(pos.offset(direction))) {
				directions.add(DrawDirections.get(direction));
			}
		}
	}

	public float getOpacityAnimation() {
		return opacityAnimation.get();
	}

	public float getPositionAnimation() {
		return positionAnimation.get();
	}

	public float getRangeAnimation() {
		return rangeFactor;
	}

	public boolean isPassed() {
		return opacityAnimation.isFinished(true);
	}

	public void forEachDirection(Consumer<DrawDirection> directionConsumer) {
		directions.forEach(directionConsumer);
	}
}