package com.perfect.client.core.utils.game.world;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.perfect.client.PerfectClient.mc;


public class WorldUtils {

	public static final int TICKS_PER_SECOND = 20;

	public static List<Entity> getEntities(Box box) {
		return getEntities(box, EntityPredicates.EXCEPT_SPECTATOR);
	}

	public static List<Entity> getEntities(Box box, Predicate<? super Entity> predicate) {
		List<Entity> list = new ArrayList<>();

		if (mc.world != null) {
			Lists.newArrayList(mc.world.getEntities()).forEach(entity -> {
				if (entity != null && box.intersects(entity.getBoundingBox()) && predicate.test(entity)) {
					list.add(entity);
				}
			});
		}

		return list;
	}

	public static Dimension getDimension() {
		if (mc.world == null) {
			return Dimension.OVERWORLD;
		}

		return switch (mc.world.getRegistryKey().getValue().getPath()) {
			case "the_nether" -> Dimension.NETHER;
			case "the_end" -> Dimension.END;
			default -> Dimension.OVERWORLD;
		};
	}

	public static void disconnect() {
		boolean single = mc.isInSingleplayer();
		ServerInfo serverInfo = mc.getCurrentServerEntry();

		if (mc.world != null) {
			mc.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
		}

		if (single) {
			mc.disconnectWithSavingScreen();
		}
		else {
			mc.disconnectWithProgressScreen();
		}

		TitleScreen titleScreen = new TitleScreen();

		if (single) {
			mc.setScreen(titleScreen);
		}
		else if (serverInfo != null && serverInfo.isRealm()) {
			mc.setScreen(new RealmsMainScreen(titleScreen));
		}
		else {
			mc.setScreen(new MultiplayerScreen(titleScreen));
		}
	}
}
