package com.perfect.client.functional.function.movement.tickShift.helpers;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.system.IEventListener;
import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.functional.function.movement.tickShift.TickShift;
import com.perfect.client.functional.function.movement.tickShift.TickShiftConfig;
import lombok.Getter;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Getter
public class TimerCharge extends FunctionHelper<TickShift, TickShiftConfig> implements IEventListener {

	private double value = 0;

	private boolean sent;

	public TimerCharge(TickShift function, TickShiftConfig config) {
		super(function, config);
		PerfectClient.EVENT_MANAGER.subscribe(this);
	}

	@Override
	public void onPreTick() {
		sent = false;
	}

	@Override
	public void onPostTick() {
		if (!sent && !PlayerUtils.isTickUpdateLock()) {
			addCharge();
		}
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		if (isMovePacket(event.getPacket()) && !function.isInfinite()) {
			if (!function.getConfig().getKeepPackets().get() || function.isEnabled()) {
				value = Math.max(0, value - 1);
			}
		}
		if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			sent = true;
		}
	}

	@Override
	public void onGameJoin() {
		if (function.isInfinite()) {
			setFullCharge();
		}
		else {
			value = 0;
		}
	}

	private boolean isMovePacket(Packet<?> p) {
		if (!(p instanceof PlayerMoveC2SPacket packet)) {
			return false;
		}
		if (PlayerUtils.getSpeedInBPS().horizontalLength() > 0) {
			return true;
		}
		return config.getLegit().get() && (packet.changesPosition() || packet.changesLook());
	}

	public void addCharge() {
		if (function.isInfinite()) {
			setFullCharge();
		}
		else {
			value = Math.min(config.getMaxPackets().get(), value + config.getChargeSpeed().get());
		}
	}

	public void setFullCharge() {
		value = config.getMaxPackets().get();
	}
}
