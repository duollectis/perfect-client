package com.perfect.client.functional.function.combat.attackAura.rotate;

import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import com.perfect.client.functional.function.combat.attackAura.data.AttackInfo;
import com.perfect.client.functional.function.combat.attackAura.utils.RotateHandler;


public class NoneRotate extends RotateHandler {

	public NoneRotate(AttackAura function, AttackAuraConfig config) {
		super(function, config);
	}

	
	@Override
	public boolean rotate(AttackInfo attackInfo) {
		return !PositionMath.isOutOfRange(attackInfo.getAttackVec3d(), config.getAttackRange().get());
	}
}
