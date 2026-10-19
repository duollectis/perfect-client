package com.perfect.client.manager.game.predict;

import com.perfect.client.core.utils.game.entity.EntityUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PredictState {

	private final List<ExtrapolationResult> list = new ArrayList<>();

	private final Vec3d startVelocity;

	private Vec3d pos;
	private Vec3d velocity;

	private boolean onGround;
	private boolean lastOnGround;

	public PredictState(Vec3d pos, Vec3d startVelocity, Vec3d velocity, boolean onGround, boolean lastOnGround) {
		this.pos = pos;
		this.velocity = velocity;
		this.startVelocity = startVelocity;
		this.onGround = onGround;
		this.lastOnGround = lastOnGround;
	}

	public void add(Entity entity, Vec3d pos) {
		list.add(new ExtrapolationResult(pos, EntityUtils.getBoxAt(entity, pos)));
	}

	public void add(ExtrapolationResult box) {
		list.add(box);
	}

	public void move(Vec3d movement) {
		pos = pos.add(movement);
	}
}
