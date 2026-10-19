package com.perfect.client.manager.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.perfect.client.core.functional.command.Command;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.functional.command.*;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class CommandManager extends Manager {

	public final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
	public final CommandSource COMMAND_SOURCE = new ClientCommandSource(null, mc, true);
	public final List<Command> COMMANDS = new ArrayList<>();

	public CommandManager() {
		add(new BindCommand());
		add(new ConfigCommand());
		add(new FakePlayerCommand());
		add(new FriendCommand());
		add(new HudCommand());

		COMMANDS.sort(Comparator.comparing(Command::getId));
	}

	public void add(Command command) {
		COMMANDS.removeIf(existing -> existing.getId().equals(command.getId()));
		command.registerTo(DISPATCHER);
		COMMANDS.add(command);
	}

	public void dispatch(String message) throws CommandSyntaxException {
		DISPATCHER.execute(message, COMMAND_SOURCE);
	}
}
