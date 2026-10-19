package com.perfect.client.functional.function.combat.autoMine.data;

import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@Getter
public class TrapInfo {

	private List<BlockPos> surround;
	private List<BlockPos> trap;
	private List<BlockPos> burrow;

	private boolean surrounded;
	private boolean trapped;

	public void fill(LivingEntity target) {
		surround = TrapUtils.getTrap(target, TrapType.SURROUND_WITHOUT_FLOOR, false);
		trap = TrapUtils.getTrap(target, TrapType.TRAP, false);
		burrow = EntityUtils.getIntersectedFeet(target);
		
		surrounded = TrapUtils.isTrapped(surround);
		trapped = TrapUtils.isTrapped(trap);
	}
}