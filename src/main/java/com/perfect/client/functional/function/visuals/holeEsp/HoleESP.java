package com.perfect.client.functional.function.visuals.holeEsp;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.event.events.block.ServerBlockUpdateEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.hole.Hole;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.ChunkUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.visuals.holeEsp.data.ChunkCache;
import com.perfect.client.functional.function.visuals.holeEsp.data.FindResult;
import com.perfect.client.functional.function.visuals.holeEsp.data.HoleChunk;
import com.perfect.client.functional.function.visuals.holeEsp.helpers.ChunkHelper;
import com.perfect.client.functional.function.visuals.holeEsp.helpers.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HoleESP extends Function {

	private static final int INSTANT_UPDATE_RANGE = 20;

	private final HoleEspConfig config = new HoleEspConfig(this);

	private final ChunkHelper chunkHelper = new ChunkHelper(this, config);
	private final RenderHelper renderer = new RenderHelper(this, config);

	private List<Hole> holes = new ArrayList<>();
	private HashMap<HoleChunk, ChunkCache> cache = new HashMap<>();

	
	public HoleESP() {
		super("hole-esp", "Hole ESP", Category.VISUALS);
	}

	@Override
	public void onEnable() {
		cache.clear();
	}

	@Override
	public void onDisable() {
		cache.clear();
	}

	@Override
	public void onGameJoin() {
		cache.clear();
	}

	@Override
	public void onGameLeft() {
		cache.clear();
	}

	@Override
	public void onPostTick() {
		FindResult findResult = getPositions();
		holes = findResult.holes();
		cache = findResult.cache();
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		renderer.render(event.getMatrices(), holes);
	}

	@Override
	public void onServerBlockUpdate(ServerBlockUpdateEvent event) {

		// ServerBlockUpdateEvent добавлен вместо ClientBlockUpdateEvent
		// Так как он, в отличие от ClientBlockUpdateEvent не срабатывает при добавлении новых чанков
		// из-за чего не вызывает лагов.

		onBlockUpdate(event.getPos());
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		if (isInInstantUpdateRange(event.getPos())) {

			// ServerBlockUpdateEvent обновляет блоки с задержкой ping
			// Поэтому добавлен дополнительный ClientBlockUpdateEvent на маленьком расстоянии
			// Чтобы у пользователя не возникало лишних вопросов

			onBlockUpdate(event.getPos());
		}
	}

	@Override
	public String getStatus() {
		return String.valueOf(holes.size());
	}

	private boolean isInInstantUpdateRange(BlockPos pos) {
		if (mc.player == null) {
			return false;
		}
		return PositionMath.simpleDistance(pos, mc.player.getBlockPos()) <= INSTANT_UPDATE_RANGE;
	}

	private void onBlockUpdate(BlockPos center) {
		for (BlockPos pos : PositionMath.getCube(center, 3, 3)) {
			ChunkCache chunkCache = getChunkCacheAt(pos);

			if (chunkCache != null) {
				chunkCache.onBlockUpdate(pos);
			}
		}
	}

	private ChunkCache getChunkCacheAt(BlockPos pos) {
		Vec3i chunkPos = ChunkUtils.blockPosToChunkPos(pos);
		HoleChunk holeChunk = new HoleChunk(chunkPos);

		return cache.get(holeChunk);
	}

	
	private FindResult getPositions() {
		List<Hole> holes = new ArrayList<>();
		HashMap<HoleChunk, ChunkCache> newCache = new HashMap<>();

		if (mc.player == null || mc.world == null) {
			return new FindResult(holes, newCache);
		}

		for (HoleChunk chunk : chunkHelper.getChunks()) {
			ChunkCache chunkCache = cache.get(chunk);

			if (chunkCache == null) {
				chunkCache = new ChunkCache();
			}
			else {
				chunkCache.copyHolesTo(holes);
			}

			for (BlockPos pos : chunkCache.getBlocksForUpdate(chunk)) {
				Hole hole = HoleUtils.getHole(pos, true, true, true, 3, true);

				if (hole != null) {
					Utils.add(holes, hole);
					chunkCache.add(hole);
				}
			}

			chunkCache.onUpdatePost();
			newCache.put(chunk, chunkCache);
		}

		addHolesWithPlayers(holes);

		return new FindResult(holes, newCache);
	}

	private void addHolesWithPlayers(List<Hole> holes) {
		if (mc.world == null) {
			return;
		}

		for (PlayerEntity entity : mc.world.getPlayers()) {
			if (isEntityOutOfRange(entity)) {
				continue;
			}

			for (BlockPos pos : EntityUtils.getIntersectedFeet(entity)) {
				Hole hole = HoleUtils.getHole(pos, true, true, true, 1, true);

				if (hole != null) {
					Utils.add(holes, hole);
				}
			}
		}
	}

	private boolean isEntityOutOfRange(Entity entity) {
		if (mc.player == null) {
			return false;
		}
		Vec3d center = mc.player.getPos();
		Vec3d pos = entity.getPos();
		return PositionMath.isOutOfRange(center, pos, config.getRangeXZ().get());
	}
}
