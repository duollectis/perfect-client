package com.perfect.client.functional.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.command.Command;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.command.arguments.EnumArgumentType;
import com.perfect.client.functional.command.arguments.FunctionArgumentType;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;


public class BindCommand extends Command {

	public BindCommand() {
		super("bind");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("function", new FunctionArgumentType())
				.then(argument("mode", new EnumArgumentType<>(Bind.Mode.values())).executes(context -> {
					Function function = context.getArgument("function", Function.class);
					Bind.Mode mode = context.getArgument("mode", Bind.Mode.class);

					function.getBindListener().setBinding(true);
					function.getBindListener().setBoundMessageAction(bind -> function.info(
							"Bound to %s with mode %s".formatted(bind.toString(), bind.getMode().getName())
					));
					function.getBindListener().setUnboundMessageAction(() -> function.info("Bind removed"));

					Managers.BIND.setWaitEnterRelease(true);
					Managers.BIND.setSetFlags(true);
					Managers.BIND.setSetMode(mode);

					function.info("Press key to bind");

					return SINGLE_SUCCESS;
				}))
				.executes(context -> {
					Function function = context.getArgument("function", Function.class);
					function.getBindListener().setBinding(true);
					function.getBindListener().setBoundMessageAction(bind -> function.info("Bound to %s".formatted(bind)));
					function.getBindListener().setUnboundMessageAction(() -> function.info("Bind removed"));

					Managers.BIND.setWaitEnterRelease(true);
					function.info("Press key to bind");
					return SINGLE_SUCCESS;
				}));
	}
}
