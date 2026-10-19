package com.perfect.client.core.utils.server;

import lombok.AllArgsConstructor;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;

import static com.perfect.client.PerfectClient.mc;

@AllArgsConstructor
public class Server {

	private final ScoreboardParser scoreboardParser;

	public boolean isOnAnarchy() {
		return getAnarchy() != 0;
	}

	public int getAnarchy() {
		return scoreboardParser.getAnarchy();
	}

	public double getBalance() {
		return parseScoreboardString(scoreboardParser.getBalanceName());
	}

	public double parseScoreboardString(String string) {
		if (mc.world == null) {
			return 0.0;
		}

		Scoreboard scoreboard = mc.world.getScoreboard();

		ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);

		if (objective == null) {
			return 0.0;
		}

		for (ScoreboardEntry entry : scoreboard.getScoreboardEntries(objective)) {
			Team team = scoreboard.getScoreHolderTeam(entry.owner());

			if (team != null) {
				Text nameText = team.getPrefix();

				if (nameText != null) {
					String scoreboardValue = nameText.getString();

					if (scoreboardValue.toLowerCase().contains(string.toLowerCase())) {
						return scoreboardParser.parse(scoreboardValue);
					}
				}
			}
		}

		return 0.0;
	}
}
