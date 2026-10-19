package com.perfect.client.functional.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.perfect.client.core.functional.command.Command;
import com.perfect.client.functional.command.arguments.EnumArgumentType;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.perfect.client.manager.system.FunctionManager.fakePlayer;


public class FakePlayerCommand extends Command {

	public FakePlayerCommand() {
		super("fakeplayer");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			fakePlayer.spawnOrRemove();
			return SINGLE_SUCCESS;
		});

		builder.then(argument("action", new EnumArgumentType<>(Action.values())).executes(context -> {
			Action action = context.getArgument("action", Action.class);

			if (action == null) {
				return SINGLE_SUCCESS;
			}

			doAction(action);

			return SINGLE_SUCCESS;
		}));
	}

	private void doAction(Action action) {
		switch (action) {
			case SPAWN -> fakePlayer.spawn();
			case REMOVE -> fakePlayer.remove();
			case RECORD -> fakePlayer.record();
			case STOP -> fakePlayer.stop();
			case PLAY -> fakePlayer.play();
			case HEAL -> fakePlayer.heal();
			case SETPOS -> fakePlayer.setPos();
		}
	}

	public enum Action {
		SPAWN,
		REMOVE,
		RECORD,
		STOP,
		PLAY,
		HEAL,
		SETPOS
	}
}
