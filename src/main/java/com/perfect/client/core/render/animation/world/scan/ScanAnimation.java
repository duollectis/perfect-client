package com.perfect.client.core.render.animation.world.scan;

import com.perfect.client.core.render.animation.world.IWorldAnimation;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.color.Color4f;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class ScanAnimation implements IWorldAnimation {

	private static final float SIZE_OFFSET = 0.03f;
	private static final float POSITION_OFFSET = 0.025f;
	private static final float POSITION_ANIMATION = 0.5f;
	private static final int MAX_ALLOWED_RADIUS = 20;

	private static final Map<Integer, List<Vec3i>> radiusCache = new HashMap<>();
	private static final Set<Vec3i> visitedCache = new HashSet<>();

	private static int maxCachedRadius = 0;

	private final MultiColor lineColor;
	private final MultiColor sideColor;
	private final int maxRadius;
	private final int lifetime;
	private final boolean isTotemAnimation;
	private final boolean waveMode;

	private final List<ScanBlock> trackedBlocks = new ArrayList<>();

	private final Set<BlockPos> visited = new HashSet<>();

	private final BlockPos center;

	private int currentRadius = 1;

	private boolean skipTick;

	public ScanAnimation(
			BlockPos center,
			MultiColor lineColor,
			MultiColor sideColor,
			int maxRadius,
			int lifetime,
			boolean isTotemAnimation,
			boolean waveMode
	) {
		this.lineColor = lineColor;
		this.sideColor = sideColor;
		this.lifetime = lifetime;
		this.isTotemAnimation = isTotemAnimation;
		this.waveMode = waveMode;
		this.maxRadius = Math.min(maxRadius, MAX_ALLOWED_RADIUS);

		this.center = center;

		addTracked(center, getColor());

		skipTick = true;
	}

	public static void init() {
		getSphere(MAX_ALLOWED_RADIUS);
	}

	
	private static List<Vec3i> getSphere(int radius) {
		if (radiusCache.containsKey(radius)) {
			return radiusCache.get(radius);
		}

		for (int rad = maxCachedRadius + 1; rad <= radius; rad++) {
			List<Vec3i> radiusList = new ArrayList<>();

			radiusCache.put(rad, radiusList);

			// Генерация только 1 четверти сферы с отражением

			int radSquared = rad * rad - 1;

			for (int x = 0; x <= rad; x++) {
				for (int y = 0; y <= rad; y++) {
					for (int z = 0; z <= rad; z++) {
						if (x * x + y * y + z * z <= radSquared) {
							add(radiusList, new Vec3i(x, y, z));

							add(radiusList, new Vec3i(-x, y, z));
							add(radiusList, new Vec3i(x, -y, z));
							add(radiusList, new Vec3i(x, y, -z));

							add(radiusList, new Vec3i(-x, -y, z));
							add(radiusList, new Vec3i(x, -y, -z));
							add(radiusList, new Vec3i(-x, y, -z));

							add(radiusList, new Vec3i(-x, -y, -z));
						}
					}
				}
			}
		}

		maxCachedRadius = radius;

		// Вернет кешированное значение
		return getSphere(radius);
	}

	private static void add(List<Vec3i> list, Vec3i pos) {
		if (!visitedCache.contains(pos)) {
			Utils.add(list, pos);
			visitedCache.add(pos);
		}
	}

	private Color[] getColor() {
		Color renderLineColor = lineColor.getColor();
		Color renderSideColor = sideColor.getColor();

		if (isTotemAnimation) {
			Color totemColor = ColorUtils.generateTotemColor(1.0f);

			renderLineColor = totemColor;
			renderSideColor = ColorUtils.multiplyAlpha(totemColor, 0.35f);
		}

		return new Color[]{renderLineColor, renderSideColor};
	}

	public boolean update() {
		if (skipTick) {
			skipTick = false;
			return false;
		}

		if (currentRadius <= maxRadius) {
			for (Vec3i distanceFromCenter : getSphere(currentRadius)) {
				BlockPos pos = center.add(distanceFromCenter);
				addTracked(pos, getColor());
			}

			currentRadius++;
		}

		trackedBlocks.removeIf(ScanBlock::isPassed);

		return currentRadius == maxRadius && trackedBlocks.isEmpty();
	}

	private void addTracked(BlockPos pos, Color[] colors) {
		if (!visited.contains(pos) && !BlockUtils.isReplaceable(pos)) {
			trackedBlocks.add(new ScanBlock(
					pos,
					lifetime,
					(float) currentRadius / (float) maxRadius,
					colors[0],
					colors[1]
			));
			visited.add(pos);
		}
	}

	public void draw(MatrixStack matrices) {
		Renderer3D.preRender();
		Render.setShader(ShaderPrograms.POSITION_COLOR);
		Render.enableDepthTest();

		VertexBuilder faceBuilder = new VertexBuilder(VertexFormats.POSITION_COLOR, VertexDrawMode.QUADS);
		VertexBuilder lineBuilder = new VertexBuilder(VertexFormats.POSITION_COLOR, VertexDrawMode.LINES);

		Matrix4f matrix = matrices.peek().getPositionMatrix();

		trackedBlocks.forEach(scanBlock -> {
			float positionAnimation = scanBlock.getPositionAnimation();
			float opacityAnimation = scanBlock.getOpacityAnimation();
			float rangeAnimation = scanBlock.getRangeAnimation();

			float positionOffset = waveMode
			                       ? POSITION_ANIMATION * (1.0f - positionAnimation) * rangeAnimation
			                       : 0;

			float opacity = (1.0f - opacityAnimation) * (1.0f - Math.min(rangeAnimation, 0.5f));

			Vec3d camera = mc.gameRenderer.getCamera().getPos();

			BlockPos pos = scanBlock.getPos();

			float x1 = (float) (pos.getX() - camera.getX());
			float y1 = (float) (pos.getY() - camera.getY());
			float z1 = (float) (pos.getZ() - camera.getZ());
			float x2 = (float) (pos.getX() + 1.0 - camera.getX());
			float y2 = (float) (pos.getY() + 1.0 - camera.getY());
			float z2 = (float) (pos.getZ() + 1.0 - camera.getZ());

			FloatBox positionBox = new FloatBox(x1, y1, z1, x2, y2, z2);
			FloatBox sizeBox = positionBox.expand(-SIZE_OFFSET);

			Color4f line = new Color4f(ColorUtils.multiplyAlpha(scanBlock.getLineColor(), opacity));
			Color4f side = new Color4f(ColorUtils.multiplyAlpha(scanBlock.getSideColor(), opacity));

			scanBlock.forEachDirection(direction -> direction.draw(
					matrix,
					positionBox, sizeBox,
					POSITION_OFFSET + positionOffset,
					faceBuilder, lineBuilder,
					line, side
			));
		});

		faceBuilder.draw();
		lineBuilder.draw();

		Renderer3D.postRender();
	}
}