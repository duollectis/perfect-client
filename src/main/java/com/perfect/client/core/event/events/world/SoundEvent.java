package com.perfect.client.core.event.events.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.sound.SoundInstance;

@Getter
@AllArgsConstructor
public class SoundEvent {

	private final SoundInstance sound;

	public boolean isOf(net.minecraft.sound.SoundEvent sound) {
		return this.sound.getId().equals(sound.id());
	}
}
