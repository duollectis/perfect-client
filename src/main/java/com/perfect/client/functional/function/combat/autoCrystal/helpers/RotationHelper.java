package com.perfect.client.functional.function.combat.autoCrystal.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.rotation.Angle;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import com.perfect.client.functional.function.combat.autoCrystal.data.BreakInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.PlaceInfo;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class RotationHelper extends FunctionHelper<AutoCrystal, AutoCrystalConfig> {

	public RotationHelper(AutoCrystal function, AutoCrystalConfig config) {
        super(function, config);
    }

    public void updateRotation(PlaceInfo placeInfo, BreakInfo breakInfo) {
        Vec3d rotationVec3d = getRotationVec3d(placeInfo, breakInfo);

        if (rotationVec3d == null) {
            return;
        }

	    Angle rotation = new Angle(rotationVec3d);

        Managers.ROTATION.setRotateYaw(rotation.getYaw());
        Managers.ROTATION.setRotatePitch(rotation.getPitch());
    }

    private Vec3d getRotationVec3d(PlaceInfo placeInfo, BreakInfo breakInfo) {
        return switch (config.getRotate().get()) {
            case NONE -> null;
            case FREE -> getFreeVec3d(placeInfo, breakInfo);
            case STRICT -> getStrictVec3d(placeInfo, breakInfo);
        };
    }

    
    private Vec3d getFreeVec3d(PlaceInfo placeInfo, BreakInfo breakInfo) {
        if (mc.player == null) {
            return null;
        }

        if (breakInfo != null && breakInfo.getCrystal() != null) {
            return getRotationPosByCrystal(breakInfo.getCrystal()).toCenterPos().add(0, 0.5, 0);
        }

        if (placeInfo != null && placeInfo.getPos() != null) {
            Vec3d rayTraceVec = placeInfo.getRayTraceVec();

            if (rayTraceVec != null) {
                return rayTraceVec;
            }

            return placeInfo.getPos().toCenterPos().add(0, 0.5, 0);
        }

        return null;
    }

    
    private Vec3d getStrictVec3d(PlaceInfo placeInfo, BreakInfo breakInfo) {
        if (breakInfo != null && breakInfo.getCrystal() != null) {
            return breakInfo.getCrystal().getPos();
        }

        if (placeInfo != null && placeInfo.getCrystalPlaceResult() != null) {
            Vec3d rayTraceVec = placeInfo.getRayTraceVec();

            if (rayTraceVec != null) {
                return rayTraceVec;
            }

            return placeInfo.getCrystalPlaceResult().getPos();
        }

        return null;
    }

    private BlockPos getRotationPosByCrystal(EndCrystalEntity crystal) {
        return crystal.getBlockPos().down();
    }
}
