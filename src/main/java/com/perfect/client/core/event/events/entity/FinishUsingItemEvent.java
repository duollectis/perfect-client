package com.perfect.client.core.event.events.entity;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Getter
@Setter
@AllArgsConstructor

public class FinishUsingItemEvent extends Cancellable {

    private final LivingEntity user;
    private ItemStack stack;
}
