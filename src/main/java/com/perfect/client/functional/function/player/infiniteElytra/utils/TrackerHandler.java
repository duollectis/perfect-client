package com.perfect.client.functional.function.player.infiniteElytra.utils;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.player.infiniteElytra.InfiniteElytra;
import com.perfect.client.functional.function.player.infiniteElytra.InfiniteElytraConfig;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

import java.util.ArrayList;
import java.util.List;


public class TrackerHandler extends FunctionHandler<InfiniteElytra, InfiniteElytraConfig> {

	private static final int FALL_FLYING_INDEX = 7;
	private static final int FLYING_BIT = 1 << FALL_FLYING_INDEX;

	private final List<DataTracker.SerializedEntry<?>> trackedValues = new ArrayList<>();

	private boolean waitPose;
	private boolean waitFlags;

	@Getter
	private boolean fallFlying;

	private boolean flyingDisabled;

	public TrackerHandler(InfiniteElytra function, InfiniteElytraConfig config) {
		super(function, config);
	}

	public boolean shouldSkipFlyingSwitch() {
		boolean _flyingDisabled = flyingDisabled;
		flyingDisabled = false;

		return _flyingDisabled;
	}

	public void waitPacket() {
		waitPose = true;
		waitFlags = true;
	}

	public void reset() {
		fallFlying = false;
		waitPose = false;
		waitFlags = false;
		flyingDisabled = false;
		trackedValues.clear();
	}

	private DataTracker.SerializedEntry<?> handlePose(DataTracker.SerializedEntry<?> value) {
		EntityPose pose = (EntityPose) value.value();

		if (waitPose && config.getSilent().get() && (pose == EntityPose.STANDING || pose == EntityPose.CROUCHING)) {
			waitPose = false;

			return new DataTracker.SerializedEntry<>(
					value.id(),
					TrackedDataHandlerRegistry.ENTITY_POSE,
					EntityPose.GLIDING
			);
		}

		return null;
	}

	private DataTracker.SerializedEntry<?> handleFlags(DataTracker.SerializedEntry<?> value) {
		assert mc.player != null;

		byte flags = (byte) value.value();

		fallFlying = getFallFlyingBit(flags);

		if (waitFlags) {

			if (!fallFlying) {
				flyingDisabled = true;
			}

			if (config.getSilent().get()) {
				waitFlags = false;

				return new DataTracker.SerializedEntry<>(
						value.id(),
						TrackedDataHandlerRegistry.BYTE,
						withFallFlyingBit(flags)
				);
			}
		}


		return null;
	}

	private void handleValue(
			DataTracker tracker,
			DataTracker.SerializedEntry<?> value,
			DataTracker.Entry<?> entry
	) {
		assert mc.player != null;

		DataTracker.SerializedEntry<?> modifiedValue = null;

		if (Entity.FLAGS.equals(entry.getData())) {
			modifiedValue = handleFlags(value);
		}
		else if (Entity.POSE.equals(entry.getData())) {
			modifiedValue = handlePose(value);
		}

		if (modifiedValue == null) {
			return;
		}

		trackedValues.add(modifiedValue);

		tracker.copyToFrom(entry, modifiedValue);
		mc.player.onTrackedDataSet(entry.getData());
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (mc.player == null || mc.player.isOnGround()) {
			return;
		}

		DataTracker tracker = mc.player.getDataTracker();
		DataTracker.Entry<?>[] entries = tracker.entries;

		if (event.getPacket() instanceof EntityTrackerUpdateS2CPacket packet && packet.id() == mc.player.getId()) {
			for (DataTracker.SerializedEntry<?> value : packet.trackedValues()) {
				handleValue(tracker, value, entries[value.id()]);
			}

			if (!trackedValues.isEmpty()) {
				event.cancel();
				mc.player.onDataTrackerUpdate(trackedValues);

				trackedValues.clear();
			}
		}
	}

	private static byte withFallFlyingBit(byte flags) {
		return (byte) (flags | FLYING_BIT);
	}

	private static boolean getFallFlyingBit(byte flags) {
		return (flags & FLYING_BIT) != 0;
	}
}
