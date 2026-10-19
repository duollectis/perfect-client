package com.perfect.client.functional.function.visuals.logoutspots;

import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.ModelRenderer;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.visuals.logoutspots.utils.Spot;
import com.perfect.client.functional.function.visuals.nametags.utils.ColoredString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3x2fStack;
import org.joml.Vector4d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class LogoutSpots extends Function {

	private static final float COMPENSATE_FACTOR = 12.5f;
	private final static float ROUNDING = 2.2f;
	private final static float X_OFFSET = 1.5f;
	private final static float Y_OFFSET = 0.2f;
	private final static float HEIGHT = 7.6f;

	private final LogoutSpotsConfig config = new LogoutSpotsConfig(this);

	private final List<Spot> spots = new CopyOnWriteArrayList<>();
	private final List<PlayerListEntry> lastPlayerList = new ArrayList<>();
	private final List<PlayerEntity> lastPlayers = new ArrayList<>();

	private RegistryKey<World> lastDimension;

	public LogoutSpots() {
		super("logout-spots", "Logout Spots", Category.VISUALS, "leave", "esp");
	}

	@Override
	public void onEnable() {
		if (mc.getNetworkHandler() != null) {
			lastPlayerList.addAll(mc.getNetworkHandler().getPlayerList());
		}

		updateLastPlayers();

		if (mc.world != null) {
			lastDimension = mc.world.getRegistryKey();
		}
	}

	@Override
	public void onDisable() {
		spots.clear();
		lastPlayerList.clear();
		lastPlayers.clear();
	}

	@Override
	public void onPostTick() {
		if (mc.world == null || mc.getNetworkHandler() == null) {
			return;
		}

		if (lastDimension != mc.world.getRegistryKey()) {
			spots.clear();
			lastDimension = mc.world.getRegistryKey();
		}

		for (Entity entity : mc.world.getEntities()) {
			if (entity instanceof PlayerEntity player) {
				spots.removeIf(spot -> {
					boolean shouldRemove = spot.uuid.equals(player.getUuid());
					if (shouldRemove && config.getMessageWhileLogBack().get()){
						String textMessage = "%s logged back in at %d, %d, %d!".formatted(spot.name, Math.round(spot.pos.x), Math.round(spot.pos.y), Math.round(spot.pos.z));

						ChatUtils.sendMsg(0, Text.of(Formatting.LIGHT_PURPLE + "[Logout Spots] "), textMessage);

						return true;
					}

					return shouldRemove;
				});
			}
		}

		if (mc.getNetworkHandler().getPlayerList().size() != lastPlayerList.size()) {
			for (PlayerListEntry entry : lastPlayerList) {
				boolean isStillOnline = mc.getNetworkHandler().getPlayerList().stream()
						.anyMatch(p -> p.getProfile().getId().equals(entry.getProfile().getId()));

				if (!isStillOnline) {
					for (PlayerEntity player : lastPlayers) {
						if (player.getUuid().equals(entry.getProfile().getId())) {
							if (config.getArmorCheck().get() && !EntityUtils.hasArmor(player)) {
								continue;
							}

							if (config.getIgnoreFriends().get() && Managers.FRIEND.isFriend(player)) {
								continue;
							}

							spots.removeIf(spot -> spot.uuid.equals(player.getUuid()));
							spots.add(new Spot(player));
						}
					}
				}
			}

			lastPlayerList.clear();
			lastPlayerList.addAll(mc.getNetworkHandler().getPlayerList());
			updateLastPlayers();
		}
	}

	
	private void updateLastPlayers() {
		if (mc.world == null) {
			return;
		}

		lastPlayers.clear();

		for (Entity entity : mc.world.getEntities()) {
			if (entity instanceof PlayerEntity player && player != mc.player && !isBot(player)) {
				lastPlayers.add(player);
			}
		}
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.player == null) {
			return;
		}

		for (Spot spot : spots) {
			if (mc.player.distanceTo(spot.entity) > 64) {
				continue;
			}

			ModelRenderer.render(event, 1f, spot.entity, config.getOutlineColor().get().getColor(), config.getFillColor().get().getColor());
		}
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		for (Spot spot : spots) {
			renderSpotText(event.getContext(), spot);
		}
	}

	
	private void renderSpotText(DrawContext context, Spot spot) {
		if (mc.options.hudHidden) {
			return;
		}

		Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
		float distance = (float) spot.pos.distanceTo(cameraPos);

		float scale = (config.getScale().getFloat() * COMPENSATE_FACTOR) / Math.max(0.1f, distance);
		scale = Math.max(scale, 1.0f);

		Vec3d vector = spot.pos.add(0.0, spot.entity.getEyeHeight(spot.entity.getPose()) + 0.4, 0.0);
		Vector4d position = null;

		vector = Renderer3D.worldSpaceToScreenSpace(vector);

		if (vector.z > 0.0 && vector.z < 1.0) {
			position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
		}

		if (position == null) {
			return;
		}

		ColoredString text = buildTextString(spot);
		if (text.getWidth() == 0) {
			return;
		}

		Matrix3x2fStack matrices = context.getMatrices();

		double posX = position.x;
		double posY = position.y;

		float textWidth = text.getWidth();
		float tagX = (float) posX - textWidth / 2.0f;
		float tagY = (float) posY - HEIGHT - Y_OFFSET;

		float centerX = (float) posX;

		matrices.pushMatrix();
		matrices.translate(centerX, (float) posY);
		matrices.scale(scale, scale);
		matrices.translate(-centerX, (float) -posY);

		float boxX = tagX - X_OFFSET;
		float boxWidth = textWidth + X_OFFSET * 2.0f;

		if (config.getBackground().get()) {
			Renderer2D.round()
					.pos(boxX, tagY, boxWidth, HEIGHT)
					.rounding(ROUNDING)
					.thickness(0.6f)
					.alpha(0.9f)
					.outlineColor(Managers.COLOR.get(Managers.COLOR.HUD_OUTLINE))
					.color(Managers.COLOR.get(Managers.COLOR.HUD_BACKGROUND))
					.draw(matrices);
		}

		text.draw(matrices, boxX, tagY, boxWidth, HEIGHT);

		matrices.popMatrix();
	}

	
	private ColoredString buildTextString(Spot spot) {
		ColoredString string = new ColoredString();

		if (config.getNameRender().get()) {
			string.add(spot.name, RenderUtils.getEntityColor(spot.entity, Color.WHITE));
		}

		if (config.getHealthRender().get()) {
			string.add(" " + Math.round((spot.health) * 10.0f) / 10.0f, Managers.COLOR.get(Managers.COLOR.MAIN));
		}

		if (config.getCoordRender().get()) {
			string.add(" (" + Math.round(spot.pos.x) + " " + Math.round(spot.pos.y) + " " + Math.round(spot.pos.z) + ")", Color.WHITE);
		}

		if (config.getTimePassed().get()) {
			string.add(" " + spot.getTimePassed(), Color.LIGHT_GRAY);
		}

		return string;
	}

	public boolean isBot(PlayerEntity player) {
		if (EntityUtils.getPing(player) <= 0) {
			return true;
		}

		if (EntityUtils.isCreative(player)) {
			return true;
		}

		if ( player.getUuid().equals(Uuids.getOfflinePlayerUuid(player.getName().getString()))) {
			return true;
		}

		if ((int) EntityUtils.getTotalHealth(player) / player.getMaxHealth() == 0) {
			return true;
		}

		return false;
	}
}