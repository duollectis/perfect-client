package com.perfect.client.manager.core;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.sounds.Sounds;
import net.minecraft.client.sound.PositionedSoundInstance;

public class SoundManager extends Manager {

	public void playSound(Sounds.SoundType soundType) {
		if (soundType == null || soundType.getEvent() == null || mc.getSoundManager() == null) {
			return;
		}

		mc.getSoundManager().play(PositionedSoundInstance.master(soundType.getEvent(), 1, 1));
	}

	public void playSound(Sounds.SoundType soundType, float volume, float pitch) {
		if (soundType == null || soundType.getEvent() == null || mc.getSoundManager() == null) {
			return;
		}

		mc.getSoundManager().play(PositionedSoundInstance.master(soundType.getEvent(), volume, pitch));
	}
}