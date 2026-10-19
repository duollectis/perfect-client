package com.perfect.client.functional.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.perfect.client.core.functional.command.Command;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.functional.command.arguments.FriendArgumentType;
import com.perfect.client.functional.command.arguments.PlayerNameArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import java.util.Set;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;


public class FriendCommand extends Command {

	public FriendCommand() {
		super("friends");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("add")
				.then(argument("player", new PlayerNameArgumentType())
						.executes(context -> {
							String friend = context.getArgument("player", String.class);

							if (Managers.FRIEND.add(friend)) {
								sendMessage(friend, "added");
							}
							else {
								sendMessage(friend, "already-added");
							}

							return SINGLE_SUCCESS;
						})
				)
		);

		builder.then(literal("remove")
				.then(argument("friend", new FriendArgumentType())
						.executes(context -> {
							String friend = context.getArgument("friend", String.class);
							if (friend == null) {
								return SINGLE_SUCCESS;
							}

							if (Managers.FRIEND.remove(friend)) {
								sendMessage(friend, "removed");
							}

							return SINGLE_SUCCESS;
						})
				)
		);

		builder.then(literal("list").executes(context -> {
					Set<String> friends = Managers.FRIEND.getFriends();

					if (friends.isEmpty()) {
						ChatUtils.sendMsg(translation("empty-list"));
					}
					else {
						ChatUtils.sendMsg("%s%s".formatted(Formatting.AQUA, translation("list")));

						for (String friend : friends) {
							ChatUtils.sendMsg(friend);
						}

						ChatUtils.sendMsg("%s%s".formatted(Formatting.AQUA, "---------------------"));
					}

					return SINGLE_SUCCESS;
				})
		);

		builder.then(literal("clear").executes(context -> {
			Managers.FRIEND.clear();
			return SINGLE_SUCCESS;
		}));
	}

	private void sendMessage(String friend, String translationId) {
		ChatUtils.sendMsg(
				friend.hashCode(),
				"%s%s %s%s".formatted(
						Formatting.WHITE,
						friend,
						Formatting.GRAY,
						translation(translationId)
				)
		);
	}
}
