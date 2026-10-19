package com.perfect.client.functional.function.combat.autoCrystal.data;

import com.perfect.client.core.utils.collection.UpdatingCollection;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter

public class State {
    private final long workTimeMs;

    private final PlaceInfo placeInfo;
    private final BreakInfo breakInfo;
    private final List<LivingEntity> targets;

    private final HashMap<LivingEntity, TargetInfo> targetsInfo;

    private final UpdatingCollection<EndCrystalEntity> completedCrystals;
    private final ConcurrentHashMap<EndCrystalEntity, Pair<Integer, Long>> existedCrystals;

    public State(
            long workTimeMs,
            PlaceInfo placeInfo,
            BreakInfo breakInfo,
            List<LivingEntity> targets,
            HashMap<LivingEntity, TargetInfo> targetsInfo,
            UpdatingCollection<EndCrystalEntity> completedCrystals,
            ConcurrentHashMap<EndCrystalEntity, Pair<Integer, Long>> existedCrystals
    ) {
        this.workTimeMs = workTimeMs;

        this.placeInfo = placeInfo;
        this.breakInfo = breakInfo;
        this.targets = targets;

        this.targetsInfo = new HashMap<>(targetsInfo);

        this.completedCrystals = new UpdatingCollection<>(completedCrystals);
        this.existedCrystals = new ConcurrentHashMap<>(existedCrystals);
    }
}