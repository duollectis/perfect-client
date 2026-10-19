package com.perfect.client.inject.mixin.module.fastFall;

import com.perfect.client.core.utils.mixin.MixinUtils;
import com.perfect.client.functional.function.movement.fastFall.FastFallConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.manager.system.FunctionManager.fastFall;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public boolean verticalCollision;

	@Shadow
	public boolean groundCollision;

	@Shadow
	public abstract World getWorld();

	@Shadow
	public abstract Box getBoundingBox();

	@Shadow
	public abstract void setMovement(boolean onGround, Vec3d movement);

	@Inject(
			method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
			at = @At("HEAD")
	)
	private void perfect$adjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
		if (mc.player == null || !MixinUtils.isClientPlayer(this)) {
			return;
		}

		FastFallConfig config = fastFall.getConfig();

		if (!fastFall.isEnabled() || !config.getInstantFall().get()) {
			return;
		}

		if (config.getOnlyHole().get() && !fastFall.isHole()) {
			return;
		}

		Box box = getBoundingBox();

		List<VoxelShape> list = getWorld().getEntityCollisions(mc.player, box.stretch(movement));

		Vec3d vec3d = movement.lengthSquared() == 0.0d ? movement : Entity.adjustMovementForCollisions(
				mc.player,
				movement,
				box,
				mc.player.getWorld(),
				list
		);

		verticalCollision = movement.y != vec3d.y;
		groundCollision = verticalCollision && movement.y < 0.0d;
		setMovement(groundCollision, vec3d);
	}
}
