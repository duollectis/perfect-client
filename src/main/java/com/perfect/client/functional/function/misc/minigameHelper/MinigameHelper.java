package com.perfect.client.functional.function.misc.minigameHelper;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.color.ColorUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

import java.awt.*;

public class MinigameHelper extends Function {

	private final MinigameHelperConfig config = new MinigameHelperConfig(this);

	public MinigameHelper() {
		super("minigame-helper", "Minigame Helper", Category.MISC);
	}

	public Color getTeamColor(Entity entity) {
		if (!isEnabled() || !config.getTeamColors().get()) {
			return null;
		}

		if (!(entity instanceof PlayerEntity player)) {
			return null;
		}

		Team team = getTeam(player);

		if (team == null) {
			return null;
		}

		if (isInvalidTeam(team.getName())) {
			return null;
		}

		Formatting formatting = team.getColor();

		if (formatting == null) {
			return null;
		}

		Integer argb = formatting.getColorValue();

		if (argb == null) {
			return null;
		}

		return ColorUtils.applyAlpha(ColorUtils.fromArgb(argb), 1.0f);
	}

	public boolean isTeammateFriend(PlayerEntity player) {
		if (mc.player == null || !isEnabled() || !config.getTeamFriends().get()) {
			return false;
		}

		String team = getTeamName(mc.player);

		if (isInvalidTeam(team)) {
			return false;
		}

		return team.equals(getTeamName(player));
	}

	private String getTeamName(PlayerEntity player) {
		Team team = getTeam(player);

		if (team == null) {
			return null;
		}

		return team.getName();
	}

	private Team getTeam(PlayerEntity player) {
		if (mc.getNetworkHandler() == null) {
			return null;
		}

		PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());

		if (playerListEntry == null) {
			return null;
		}

		return playerListEntry.getScoreboardTeam();
	}

	private boolean isInvalidTeam(String team) {
		return team == null || team.contains("/");
	}
}
