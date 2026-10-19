package com.perfect.client.core.utils.server;

import com.perfect.client.core.utils.server.funtime.FunTimeScoreboardParser;
import com.perfect.client.core.utils.server.holyworld.HolyWorldScoreboardParser;
import com.perfect.client.core.utils.text.INameable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Servers implements INameable {
	FUN_TIME("FunTime", new Server(new FunTimeScoreboardParser())),
	HOLY_WORLD("HolyWorld", new Server(new HolyWorldScoreboardParser()));

	private final String name;
	private final Server server;

	@Override
	public String getName() {
		return name;
	}
}
