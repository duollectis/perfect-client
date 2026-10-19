package com.perfect.client.functional.function.misc.packetSounds;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class PacketSounds extends Function {

	private final PacketSoundsConfig config = new PacketSoundsConfig(this);

	public PacketSounds() {
		super("packet-sounds", "Packet Sounds", Category.MISC);
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		if (mc.world == null) {
			return;
		}

		BlockPos pos = event.getPos();

		if (config.getPlaceSound().get() && event.isPlace()) {
			BlockSoundGroup group = event.getNewState().getSoundGroup();

			mc.world.playSound(
					mc.player,
					pos,
					group.getPlaceSound(),
					SoundCategory.BLOCKS,
					(group.getVolume() + 1.0f) / 2.0f,
					group.getPitch() * 0.8F
			);
		}

		if (event.isBreak()) {
			if (config.getBreakSound().get()) {
				BlockSoundGroup group = event.getOldState().getSoundGroup();

				mc.world.playSoundAtBlockCenterClient(
						pos,
						group.getBreakSound(),
						SoundCategory.BLOCKS,
						(group.getVolume() + 1.0f) / 2.0f,
						group.getPitch() * 0.8F,
						false
				);
			}
		}
	}

	public boolean disableClientPlaceSound() {
		return isEnabled() && config.getPlaceSound().get();
	}

	public boolean disableClientBreakSound() {
		return isEnabled() && config.getBreakSound().get();
	}
}
