package com.perfect.client.functional.function.player.illegalDisconnect;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.game.world.WorldUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;


public class IllegalDisconnect extends Function {

	private final IllegalDisconnectConfig config = new IllegalDisconnectConfig(this);

	public IllegalDisconnect() {
		super("illegal-disconnect", "Illegal Disconnect", Category.PLAYER, "KT Leave");
	}

	
	@Override
	public void onEnable() {
		if (mc.world == null || mc.player == null) {
			disable();
			return;
		}

		switch (config.getMode().get()) {
			case INVENTORY -> sendPacket(new UpdateSelectedSlotC2SPacket(-1));
			case LOOK -> sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(Float.NaN, Float.NaN, true, false));
		}

		if (config.getLeave().get()) {
			WorldUtils.disconnect();
		}

		disable();
	}

	public enum Mode {
		INVENTORY,
		LOOK
	}
}
