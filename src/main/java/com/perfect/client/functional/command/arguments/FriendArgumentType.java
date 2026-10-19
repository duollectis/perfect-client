package com.perfect.client.functional.command.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.perfect.client.core.functional.command.Argument;
import com.perfect.client.core.manager.Managers;

import java.util.stream.Stream;

public class FriendArgumentType extends Argument<String> {

	@Override
	public String parse(String argument) throws CommandSyntaxException {
		return argument;
	}

	@Override
	protected Stream<String> getCandidates() {
		return Streams.stream(Managers.FRIEND);
	}
}
