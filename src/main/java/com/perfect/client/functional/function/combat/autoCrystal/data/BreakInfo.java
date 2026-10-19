package com.perfect.client.functional.function.combat.autoCrystal.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.decoration.EndCrystalEntity;

@Getter
@Setter

public class BreakInfo {

    private EndCrystalEntity crystal;
    private boolean facePlace;

    public BreakInfo() {
    }
}