package com.perfect.client.functional.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.perfect.client.core.functional.command.Argument;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Collection;
import java.util.stream.Stream;

import static com.perfect.client.PerfectClient.mc;

public class PlayerNameArgumentType extends Argument<String> {

	@Override
	public String parse(String argument) throws CommandSyntaxException {
		return argument;
	}

	@Override
	protected Stream<String> getCandidates() {
		if (mc.player == null || mc.player.networkHandler == null) {
			return Stream.empty();
		}

		Collection<PlayerListEntry> entries = mc.player.networkHandler.getPlayerList();
		return entries.stream().map(playerListEntry -> playerListEntry.getProfile().getName());
	}
}
