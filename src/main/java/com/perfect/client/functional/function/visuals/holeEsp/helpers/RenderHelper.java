package com.perfect.client.functional.function.visuals.holeEsp.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.render.box.BoxColors;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.hole.Hole;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.visuals.holeEsp.HoleESP;
import com.perfect.client.functional.function.visuals.holeEsp.HoleEspConfig;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;


public class RenderHelper extends FunctionHelper<HoleESP, HoleEspConfig> {

	public RenderHelper(HoleESP function, HoleEspConfig config) {
		super(function, config);
	}

	public void render(MatrixStack matrices, List<Hole> holes) {
		if (mc.player == null || holes.isEmpty()) {
			return;
		}

		float topAlpha = getTopAlpha();
		float bottomAlpha = getBottomAlpha();

		MultiColor bedrockOutline = getOutlineColor(true);
		MultiColor bedrockFill = getFillColor(true);
		MultiColor obsidianOutline = getOutlineColor(false);
		MultiColor obsidianFill = getFillColor(false);

		BoxColors bedrockOutlineColors = BoxColors.horizontalTransparentGradient(
				config.getCosmetics().get().bundle(),
				bedrockOutline,
				bottomAlpha,
				topAlpha
		);

		BoxColors bedrockFillColors = BoxColors.horizontalTransparentGradient(
				config.getCosmetics().get().bundle(),
				bedrockFill,
				bottomAlpha,
				topAlpha
		);

		BoxColors obsidianOutlineColors = BoxColors.horizontalTransparentGradient(
				config.getCosmetics().get().bundle(),
				obsidianOutline,
				bottomAlpha,
				topAlpha
		);

		BoxColors obsidianFillColors = BoxColors.horizontalTransparentGradient(
				config.getCosmetics().get().bundle(),
				obsidianFill,
				bottomAlpha,
				topAlpha
		);

		double rangeXZ = MathUtils.square(config.getRangeXZ().get());
		double rangeY = MathUtils.square(config.getRangeY().get());

		holes.forEach(hole -> {
			if (shouldRender(hole, rangeXZ, rangeY)) {
				Box box = getHoleBox(hole, rangeXZ);

				if (hole.bedrock) {
					render(matrices, box, bedrockOutline, bedrockFill, bedrockOutlineColors, bedrockFillColors);
				}
				else {
					render(matrices, box, obsidianOutline, obsidianFill, obsidianOutlineColors, obsidianFillColors);
				}
			}
		});
	}

	private Box getHoleBox(Hole hole, double rangeXZ) {
		Box box = getRenderBox(hole.box);

		if (!config.getAnimation().get()) {
			return box;
		}

		double animation = getAnimationFactor(hole, rangeXZ);
		return PositionMath.shrinkBox(box.offset(0, getOffsetAnimation(animation), 0), animation);
	}

	private double getOffsetAnimation(double factor) {
		if (mc.player == null) {
			return 0;
		}
		return config.getAnimationOffset().get() * factor;
	}

	private double getAnimationFactor(Hole hole, double range) {
		if (mc.player == null) {
			return 0;
		}

		Vec3d playerPos = RenderUtils.getEntityPos(mc.player);
		double distance = PositionMath.squareDistanceHorizontal(playerPos, hole.middle);

		double animRadius = range * (1f - config.getAnimationRadius().fromPercent());

		if (distance <= animRadius) {
			return 0.0;
		}
		else if (distance >= range) {
			return 1.0;
		}
		else {
			return MathHelper.square((distance - animRadius) / (range - animRadius));
		}
	}

	private boolean shouldRender(Hole hole, double rangeXZ, double rangeY) {
		if (mc.player == null) {
			return false;
		}
		Vec3d playerPos = mc.player.getPos();

		double distanceXZ = PositionMath.squareDistanceHorizontal(mc.player.getPos(), hole.middle);

		if (distanceXZ > rangeXZ) {
			return false;
		}

		double distanceY = PositionMath.squareDistanceVertical(playerPos, hole.middle);
		return distanceY <= rangeY;
	}

	private MultiColor getOutlineColor(boolean bedrock) {
		return getColor(false, bedrock);
	}

	private MultiColor getFillColor(boolean bedrock) {
		return getColor(true, bedrock);
	}

	private MultiColor getColor(boolean side, boolean bedrock) {
		MultiColor color;

		if (side) {
			color = bedrock ? config.getBedrockSideColor().get() : config.getObsidianSideColor().get();
		}
		else {
			color = bedrock ? config.getBedrockLineColor().get() : config.getObsidianLineColor().get();
		}

		return color;
	}

	private void render(
			MatrixStack matrices,
			Box box,
			MultiColor outlineColor,
			MultiColor fillColor,
			BoxColors outlineColors,
			BoxColors fillColors
	) {
		if (config.getGradient().get()) {
			BoxRenderer.drawBox(
					matrices,
					box,
					outlineColors,
					fillColors,
					config.getCosmetics().get().bundle()
			);
		} else {
			BoxRenderer.drawBox(
					matrices,
					box,
					outlineColor,
					fillColor,
					config.getCosmetics().get().bundle()
			);
		}
	}

	public float getBottomAlpha() {
		return getAlpha(isInvertedAlpha());
	}

	public float getTopAlpha() {
		return getAlpha(!isInvertedAlpha());
	}

	private float getAlpha(boolean top) {
		if (top) {
			return config.getGradient().get() ? 0f : 1f;
		}
		else {
			return 1f;
		}
	}

	private boolean isInvertedAlpha() {
		return config.getRenderHeight().getFloat() < 0f;
	}

	private Box getRenderBox(Box box) {
		float height = config.getRenderHeight().getFloat() - 1.0f;
		return new Box(box.minX, box.minY, box.minZ, box.maxX, box.maxY + height, box.maxZ);
	}
}
