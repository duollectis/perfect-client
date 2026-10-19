package com.perfect.client.functional.function.visuals.popChams;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.visuals.popChams.utils.TotemPopEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class PopChams extends Function {

	private final PopChamsConfig config = new PopChamsConfig(this);
	private final List<TotemPopEntity> totemPopEntities = new ArrayList<>();

	public PopChams() {
		super("pop-chams", "Pop Chams", Category.VISUALS);
	}

	@Override
	public void onDisable() {
		synchronized (totemPopEntities) {
			totemPopEntities.clear();
		}
	}

	@Override
	public void onPlayerTotemPop(PlayerEvent event) {
		PlayerEntity player = event.getPlayer();

		if (config.getIgnoreSelf().get() && player == mc.player) {
			return;
		}

		if (config.getIgnoreFriends().get() && Managers.FRIEND.isFriend(player)) {
			return;
		}

		synchronized (totemPopEntities) {
			totemPopEntities.add(new TotemPopEntity(config, event.getPlayer()));
		}
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		synchronized (totemPopEntities) {
			int listSize = totemPopEntities.size();
			if (listSize > config.getMaxChamsCount().get()) {
				totemPopEntities.removeFirst();
			}

			totemPopEntities.removeIf(ghostPlayer -> ghostPlayer.updateAndRender(event));
		}
	}
}
