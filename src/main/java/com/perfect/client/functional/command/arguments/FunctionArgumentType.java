package com.perfect.client.functional.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.perfect.client.core.functional.command.Argument;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;

import java.util.stream.Stream;

public class FunctionArgumentType extends Argument<Function> {

	@Override
	protected Function parse(String argument) throws CommandSyntaxException {
		Function function = Managers.FUNCTION.getByName(argument);

		if (function != null) {
			return function;
		}
		else {
			throw NO_SUCH_VALUE.create(argument);
		}
	}

	@Override
	protected Stream<String> getCandidates() {
		return Managers.FUNCTION.getAll().stream().map(function -> function.getName(false));
	}
}
