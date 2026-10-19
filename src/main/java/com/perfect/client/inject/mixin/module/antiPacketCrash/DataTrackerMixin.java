package com.perfect.client.inject.mixin.module.antiPacketCrash;

import com.perfect.client.core.utils.LoggerUtils;
import net.minecraft.entity.data.DataTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.antiPacketCrash;

@Mixin(DataTracker.class)
public abstract class DataTrackerMixin {

	@Unique
	private boolean updateLock;

	@Shadow
	public abstract void writeUpdatedEntries(List<DataTracker.SerializedEntry<?>> entries);

	@Inject(
			method = "writeUpdatedEntries",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$writeUpdatedEntries(List<DataTracker.SerializedEntry<?>> entries, CallbackInfo ci) {
		if (!antiPacketCrash.isEnabled() || updateLock) {
			updateLock = false;
			return;
		}

		ci.cancel();

		try {
			updateLock = true;
			writeUpdatedEntries(entries);
			updateLock = false;
		}
		catch (IllegalStateException exception) {
			LoggerUtils.warning(exception);
		}
	}
}
