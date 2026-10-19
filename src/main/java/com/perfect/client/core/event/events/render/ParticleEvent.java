package com.perfect.client.core.event.events.render;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.particle.ParticleEffect;

@Getter
@AllArgsConstructor

public class ParticleEvent extends Cancellable {

	private final ParticleEffect particle;
}
