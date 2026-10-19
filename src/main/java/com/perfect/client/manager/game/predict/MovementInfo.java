package com.perfect.client.manager.game.predict;

import com.perfect.client.core.utils.game.entity.EntityUtils;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import static com.perfect.client.PerfectClient.mc;

@Getter

public class MovementInfo {

	private float stepHeight = 0.6f;

	private float fastFallHeight = 0f;

	private int jumpingTicks = 0;
	private int fallTicks = 0;

	private Vec3d lastVelocity = null;
	private boolean lastOnGround = false;

	private Vec3d lastGroundPos;

	
	public void update(PlayerEntity player) {
		if (mc.world == null) {
			return;
		}

		boolean falling = false;
		boolean jumping = false;

		Vec3d pos = player.getPos();
		Vec3d velocity = EntityUtils.getVelocity(player);

		boolean onGround = player.isOnGround();

		if (!onGround) {
			if (velocity.y > 0) {
				jumpingTicks++;
				jumping = true;
			}
			else if (velocity.y < 0) {
				fallTicks++;
				falling = true;
			}
		}

		if (onGround && lastGroundPos != null) {
			// Если yDiff > fallTicks/jumpingTicks,
			// то скорость падения/подъема на блок больше 1 блока в секунду,
			// и мы будем считать это FastFall/Step.
			//
			// yDiff < 5 для избежания непредвиденных значений.

			if (jumpingTicks == 0) {
				double yDiff = lastGroundPos.getY() - pos.getY();

				if (yDiff > fallTicks && yDiff > 0 && yDiff < 5) {
					fastFallHeight = (float) Math.max(fastFallHeight, yDiff);
				}
			}

			if (fallTicks == 0) {
				double yDiff = pos.getY() - lastGroundPos.getY();

				if (yDiff > jumpingTicks && yDiff > 0 && yDiff < 5) {
					stepHeight = (float) Math.max(stepHeight, yDiff);
				}
			}
		}

		if (!falling && !jumping) {
			fallTicks = 0;
			jumpingTicks = 0;
		}

		if (onGround) {
			lastGroundPos = player.getPos();
		}
		lastOnGround = onGround;
		lastVelocity = velocity;
	}
}