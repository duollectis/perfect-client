package com.perfect.client.functional.function.combat.autoCrystal.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Setter
@Getter

public class PlaceInfo {

    private BlockPos pos;
    private LivingEntity target;
    private EndCrystalEntity obstructive;
    private Vec3d rayTraceVec;

    private BlockHitResult crystalPlaceResult;
    private BlockHitResult supportResult;

    private boolean facePlace = false;
    private boolean surround = false;
    private boolean surroundFacePlace = false;

    public PlaceInfo() {
    }

    public Vec3d getRayTraceVec() {
        return rayTraceVec != null ? rayTraceVec : pos.toCenterPos().add(0, 0.5, 0);
    }
}