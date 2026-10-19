package com.perfect.client.functional.function.combat.surround;

import com.perfect.client.core.event.events.entity.FinishUsingItemEvent;
import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunction;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;


public class Surround extends PlaceFunction {

	private final SurroundConfig config = new SurroundConfig(this);

	private int lastY;

	public Surround() {
		super("surround", "Surround", Category.COMBAT, true, true, "Feet Trap");
	}

	@Override
	public void onEnable() {
		super.onEnable();

		if (mc.player != null) {
			lastY = (int) Math.round(mc.player.getY());
		}
	}

	@Override
	public void onPrePlayerMove(PlayerMoveEvent event) {
		if (mc.player == null) {
			return;
		}
		if (config.getPauseStep().get()) {
			PlayerUtils.setStep(0.6f);
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (mc.player != null && event.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
			lastY = (int) Math.round(packet.change().position().getY());
		}
	}

	@Override
	public void onFinishUsingItem(FinishUsingItemEvent event) {
		if (config.getToggleOnChorus().get() && event.getStack().getItem() == Items.CHORUS_FRUIT) {
			disable();
		}
	}

	@Override
	protected List<BlockPos> getTrap() {
		return TrapUtils.getTrap(
				mc.player,
				TrapType.SURROUND,
				BedBlock.class,
				TntBlock.class,
				AbstractTorchBlock.class,
				SlimeBlock.class,
				HoneyBlock.class
		);
	}

	@Override
	protected EndCrystalEntity getCrystal(List<BlockPos> positions) {
		for (BlockPos pos : positions) {
			if (!BlockUtils.isReplaceable(pos))
				continue;
			List<Entity> entities = BlockUtils.getEntities(pos, entity -> entity instanceof EndCrystalEntity);
			if (!entities.isEmpty() && entities.getFirst() instanceof EndCrystalEntity crystal)
				return crystal;
		}
		return null;
	}

	@Override
	protected PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal) {
		for (PlaceInfo placeInfo : new ArrayList<>(places)) {
			if (new Box(placeInfo.getPosition()).intersects(crystal.getBoundingBox()))
				return placeInfo;
		}
		return null;
	}

	@Override
	protected SurroundConfig getConfig() {
		return config;
	}

	@Override
	protected void deactivateCheck() {
		if (mc.player == null) {
			return;
		}

		int y = (int) Math.round(mc.player.getY());
		if (config.getYToggle().get() && isJump(y)) {
			disable();
		}
		lastY = y;
	}

	private boolean isJump(float y) {
		return y - lastY > 0.2;
	}
}
