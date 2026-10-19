package com.perfect.client.functional.function.movement.fakeLag;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.utils.game.entity.CopyPositionEntity;
import com.perfect.client.functional.function.movement.fakeLag.modes.NormalMode;
import com.perfect.client.functional.function.movement.fakeLag.modes.TogglingMode;
import com.perfect.client.functional.function.movement.fakeLag.utils.FakeLagMode;
import lombok.Getter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class FakeLag extends Function {

	private final FakeLagConfig config = new FakeLagConfig(this);

	@Getter
	private final ModeContainer<Mode, FakeLagMode> modeContainer = createModeContainer(config::getMode)
			.add(Mode.NORMAL, new NormalMode(this, config))
			.add(Mode.TOGGLING, new TogglingMode(this, config));

	private final List<PlayerMoveC2SPacket> packets = new CopyOnWriteArrayList<>();
	private CopyPositionEntity originalModel;

	private Vec3d startVec3d = null;

	public FakeLag() {
		super("fake-lag", "Fake Lag", Category.MOVEMENT);
	}

	public void cancel() {
		packets.clear();
		disable();

		if (mc.player != null) {
			mc.player.setPos(startVec3d.x, startVec3d.y, startVec3d.z);
		}
	}

	@Override
	public void onEnable() {
		startFakeLag();
	}

	@Override
	public void onDisable() {
		endFakeLag();
	}

	public void startFakeLag() {
		if (mc.player == null) {
			return;
		}

		originalModel = new CopyPositionEntity(mc.player, mc.player.getGameProfile().getName());
		modeContainer.getCurrentMode().reset();
		startVec3d = mc.player.getPos();
	}

	public void endFakeLag() {
		dumpPackets();
		originalModel = null;
	}

	private void dumpPackets() {
		if (mc.player == null) {
			return;
		}

		for (PlayerMoveC2SPacket packet : packets) {
			mc.player.networkHandler.sendPacket(packet);
		}

		packets.clear();
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null || mc.player == null || originalModel == null) {
			return;
		}

		if (config.getRender().get()) {
			/*ModelRenderer.draw(
					event,
					originalModel,
					config.getOutlineColor().get(),
					config.getFillColor().get(),
					false
			);*/
		}
	}

	@Override
	public void onGameJoin() {
		packets.clear();
		disable();
	}

	@Override
	public void onGameLeft() {
		packets.clear();
		disable();
	}

	@Override
	public void onPreTick() {
		modeContainer.getCurrentMode().onTick();
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (!(event.getPacket() instanceof PlayerMoveC2SPacket p)) {
			return;
		}

		event.cancel();

		PlayerMoveC2SPacket prev = packets.isEmpty() ? null : packets.getLast();

		if (prev != null && p.isOnGround() == prev.isOnGround() && p.getX(-1) == prev.getX(-1)
				&& p.getY(-1) == prev.getY(-1) && p.getZ(-1) == prev.getZ(-1)) {
			return;
		}

		packets.add(p);
	}

	public enum Mode {
		NORMAL,
		TOGGLING
	}
}
