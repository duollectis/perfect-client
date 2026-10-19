package com.perfect.client.functional.function.visuals.logoutspots.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class Spot {
    public final UUID uuid;
    public final String name;
    public final PlayerEntity entity;
    public final Vec3d pos;
    public final float health;
    public final long timestamp;

    public Spot(PlayerEntity entity) {
        this.entity = entity;
        this.uuid = entity.getUuid();
        this.name = entity.getGameProfile().getName();
        this.pos = entity.getPos();
        this.health = entity.getHealth() + entity.getAbsorptionAmount();
        this.timestamp = System.currentTimeMillis();
    }

    public String getTimePassed() {
        long seconds = (System.currentTimeMillis() - timestamp) / 1000;
        return seconds + "s";
    }
}