package com.perfect.client.functional.function.combat.attackAura.rotate;

import com.perfect.client.core.functional.function.combatVersion.impl.NewCombatVersion;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import com.perfect.client.functional.function.combat.attackAura.data.AttackInfo;
import com.perfect.client.functional.function.combat.attackAura.utils.RotateHandler;
import net.minecraft.util.math.Vec3d;


public class SnapRotate extends RotateHandler {

	public SnapRotate(
			AttackAura function,
			AttackAuraConfig config
	) {
		super(function, config);
	}

	
	@Override
	public boolean rotate(AttackInfo attackInfo) {
		if (function.getCombatVersion().getCurrentMode() instanceof NewCombatVersion<?, ?> version) {
			if (version.getTicksToAttack() <= config.getSnapTime().get()) {
				Vec3d attackVec3d = attackInfo.getAttackVec3d();

				float targetYaw = RotationUtils.getYaw(attackVec3d);
				float targetPitch = RotationUtils.getPitch(attackVec3d);

				float yaw = RotationUtils.angleStep(
						Managers.ROTATION.getSentYaw(),
						targetYaw,
						config.getYawStep().getFloat()
				);

				float pitch = RotationUtils.angleStep(
						Managers.ROTATION.getSentPitch(),
						targetPitch,
						config.getPitchStep().getFloat()
				);

				Managers.ROTATION.setRotateYaw(Managers.SENSITIVITY.gcdYaw(yaw));
				Managers.ROTATION.setRotatePitch(Managers.SENSITIVITY.gcdPitch(pitch));

				return function.getRotateHelper().isRotated(attackInfo.getEntity());
			}
		}
		return false;
	}
}
