package com.perfect.client.core.utils.server.funtime;

import com.perfect.client.core.utils.server.ScoreboardParser;
import com.perfect.client.core.utils.text.StringUtils;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

import static com.perfect.client.PerfectClient.mc;

public class FunTimeScoreboardParser implements ScoreboardParser {

	@Override
	public int getAnarchy() {
		if (mc.player == null) {
			return 0;
		}

		try {
			Scoreboard scoreboard = mc.player.getScoreboard();
			ScoreboardObjective objective = scoreboard.getObjectives().toArray(new ScoreboardObjective[0])[0];

			return Integer.parseInt(objective.getDisplayName().getString().substring(10));
		} catch (Exception exception) {
			return 0;
		}
	}

	@Override
	public String getBalanceName() {
		return "Монет";
	}

	@Override
	public double parse(String string) {
		String[] parts = string.split(" ");
		String money = parts[parts.length - 1].replace(",", "");

		return StringUtils.parseDouble(money).orElse(0.0);
	}
}
