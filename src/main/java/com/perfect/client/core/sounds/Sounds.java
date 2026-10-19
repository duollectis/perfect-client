package com.perfect.client.core.sounds;

import com.perfect.client.PerfectClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Sounds {

	@Getter
	@RequiredArgsConstructor
	public enum SoundType {
		OPEN_GUI("opengui");

		@Setter
		public SoundEvent event;
		public final String id;
	}

	public static void init() {
		for (SoundType type : SoundType.values()) {
			Identifier identifier = PerfectClient.getIdentifier(type.id);
			SoundEvent soundEvent = SoundEvent.of(identifier);
			type.setEvent(soundEvent);
			Registry.register(Registries.SOUND_EVENT, identifier, soundEvent);
		}
	}
}