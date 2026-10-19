package com.perfect.client.inject.mixin.module.antiPacketCrash;

import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.thread.ThreadExecutor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.antiPacketCrash;

@Mixin(NetworkThreadUtils.class)
public class NetworkThreadUtilsMixin {

	@Shadow
	@Final
	private static Logger LOGGER;

	@Unique
	private static boolean updateLock;

	@Inject(
			method = "forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private static <T extends PacketListener> void perfect$forceMainThread(
			Packet<T> packet,
			T listener,
			ThreadExecutor<?> engine,
			CallbackInfo ci
	) throws OffThreadException {
		if (!antiPacketCrash.isEnabled() || updateLock) {
			updateLock = false;
			return;
		}

		ci.cancel();

		if (!engine.isOnThread()) {
			engine.executeSync(() -> {
				if (listener.accepts(packet)) {
					try {
						packet.apply(listener);
					}
					catch (Exception var4) {
						if (var4 instanceof CrashException crashException) {
							if (crashException.getCause() instanceof OutOfMemoryError) {
								throw NetworkThreadUtils.createCrashException(var4, packet, listener);
							}
						}
					}
				}
				else {
					LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
				}
			});

			throw OffThreadException.INSTANCE;
		}
	}
}
