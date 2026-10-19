package com.perfect.client.functional.function.visuals.holeEsp.data;

import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.hole.Hole;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class ChunkCache {

	private boolean checked = false;

	private final List<Hole> holes = new ArrayList<>();
	private final List<BlockPos> blocksForUpdate = new ArrayList<>();

	public void add(Hole hole) {
		Utils.add(holes, hole);
	}

	public List<BlockPos> getBlocksForUpdate(HoleChunk chunk) {
		if (!checked) {
			return chunk.getBlocks();
		}
		else {
			return blocksForUpdate;
		}
	}

	public void onUpdatePost() {
		blocksForUpdate.clear();
		checked = true;
	}

	public void onBlockUpdate(BlockPos pos) {
		blocksForUpdate.add(pos);
		removeHoleAt(pos);
	}

	private void removeHoleAt(BlockPos pos) {
		Hole toRemove = null;

		for (Hole hole : holes) {
			if (hole.containsPosition(pos)) {
				toRemove = hole;
				break;
			}
		}

		if (toRemove != null) {
			holes.remove(toRemove);
		}
	}

	public void copyHolesTo(List<Hole> to) {
		if (!checked) {
			return;
		}
		to.addAll(holes);
	}
}
