package com.perfect.client.functional.function.combat.attackAura.rotate;

import com.perfect.client.core.functional.function.combatVersion.impl.NewCombatVersion;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.rotation.Angle;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import com.perfect.client.functional.function.combat.attackAura.data.AttackInfo;
import com.perfect.client.functional.function.combat.attackAura.utils.RotateHandler;
import net.minecraft.util.math.Vec3d;


public class LegitTrackRotate extends RotateHandler {

	public LegitTrackRotate(AttackAura function, AttackAuraConfig config) {
		super(function, config);
	}

	
	@Override
	public boolean rotate(AttackInfo attackInfo) {
		if (function.getCombatVersion().getCurrentMode() instanceof NewCombatVersion<?, ?> version) {
			int ticksToAttack = version.getTicksToAttack();

			Vec3d playerPosOnAttack = function.getRotateHelper().getRotationExtrapolation(mc.player, ticksToAttack);

			Vec3d attackVec3d = attackInfo.getAttackVec3d(function.getRotateHelper().getRotationExtrapolation(
					attackInfo.getEntity(),
					ticksToAttack
			));

			float targetYaw = RotationUtils.getYaw(
					playerPosOnAttack,
					attackVec3d
			);

			float targetPitch = RotationUtils.getPitch(
					playerPosOnAttack,
					attackVec3d
			);

			Angle transformed = function.getRotateHelper().transformRotation(
					targetYaw,
					targetPitch,
					version.getAttackCooldownProgress(0)
			);

			Managers.ROTATION.setRotateYaw(Managers.SENSITIVITY.gcdYaw(transformed.getYaw()));
			Managers.ROTATION.setRotatePitch(Managers.SENSITIVITY.gcdPitch(transformed.getPitch()));

			return function.getRotateHelper().isRotated(attackInfo.getEntity());
		}
		return true;
	}
}
