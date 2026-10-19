package com.perfect.client.core.utils.functions;

import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class CrystalUtils {

	
	public static List<Entity> getCrystalsInRange(double range) {
		if (mc.player == null || mc.world == null) {
			return Collections.emptyList();
		}

		Vec3d vec3d = mc.player.getEyePos();

		Box box = PositionMath.boxFromCenter(
				range + 1,
				vec3d.x,
				vec3d.y,
				vec3d.z
		);

		return WorldUtils.getEntities(box, entity -> entity instanceof EndCrystalEntity);
	}
}
