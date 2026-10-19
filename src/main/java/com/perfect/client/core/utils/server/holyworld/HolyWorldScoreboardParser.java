package com.perfect.client.core.utils.server.holyworld;

import com.perfect.client.core.utils.server.ScoreboardParser;
import com.perfect.client.core.utils.text.StringUtils;

public class HolyWorldScoreboardParser implements ScoreboardParser {

	@Override
	public int getAnarchy() {
		return 0;
	}

	@Override
	public String getBalanceName() {
		return "Монеток";
	}

	@Override
	public double parse(String string) {
		String[] parts = string.split(" ");

		if (parts.length < 2) {
			return 0.0;
		}

		String money = parts[parts.length - 2].replace(",", "");

		return StringUtils.parseDouble(money).orElse(0.0);
	}
}
