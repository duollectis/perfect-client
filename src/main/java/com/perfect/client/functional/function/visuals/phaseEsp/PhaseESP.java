package com.perfect.client.functional.function.visuals.phaseEsp;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.movement.phase.utils.PhaseFindResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.phase;

public class PhaseESP extends Function {

	private final PhaseEspConfig config = new PhaseEspConfig(this);

	private PhaseFindResult phaseResult;

	public PhaseESP() {
		super("phase-esp", "Phase ESP", Category.VISUALS);
	}

	@Override
	public void onPostTick() {
		phaseResult = null;

		if (mc.world == null || mc.player == null) {
			return;
		}

		if (phase.getPearlMode().shouldDisable()) {
			return;
		}

		phaseResult = phase.getPearlMode().getPhaseResult();
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null || mc.player == null) {
			return;
		}

		if (phaseResult == null || phaseResult.pos() == null) {
			return;
		}

		for (BlockPos pos : getPhaseBlocks(phaseResult.vec3d())) {
			Box box = getRenderBox(pos);

			BoxRenderer.drawBox(
					event.getMatrices(),
					box,
					config.getLineColor().get(),
					config.getFillColor().get(),
					config.getCosmetics().get().bundle()
			);
		}
	}

	private List<BlockPos> getPhaseBlocks(Vec3d phasePos) {
		List<BlockPos> posList = new ArrayList<>();

		for (BlockPos pos : getPhaseIntersected(phasePos)) {
			if (BlockUtils.isCollidable(pos)) {
				posList.add(pos);
			}
		}

		return posList;
	}

	private List<BlockPos> getPhaseIntersected(Vec3d phasePos) {
		assert mc.player != null;
		Box phaseHitbox = EntityUtils.getBoxAt(mc.player, phasePos);
		return PositionMath.getBoxIntersectedMinimum(new ArrayList<>(), phaseHitbox);
	}

	private Box getRenderBox(BlockPos pos) {
		Box b = new Box(pos);

		float height = config.getRenderHeight().getFloat() - 1f;
		return new Box(b.minX, b.minY, b.minZ, b.maxX, b.maxY + height, b.maxZ);
	}
}
