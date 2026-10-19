package com.perfect.client.core.functional.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.perfect.client.core.lang.Translations;
import lombok.Getter;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Command {
    private static final String TRANSLATIONS_PATH = "commands.%s.%s";

    @Getter
    private final String id;
    private final List<String> aliases = new ArrayList<>();

    public Command(String id, String... aliases) {
        this.id = id;
        Collections.addAll(this.aliases, aliases);
    }

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public final void registerTo(CommandDispatcher<CommandSource> dispatcher) {
        register(dispatcher, id);
        for (String alias : aliases) register(dispatcher, alias);
    }

    public void register(CommandDispatcher<CommandSource> dispatcher, String name) {
        LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(name);
        build(builder);
        dispatcher.register(builder);
    }

    public abstract void build(LiteralArgumentBuilder<CommandSource> builder);

    public String translation(String path) {
        return Translations.get(TRANSLATIONS_PATH.formatted(id, path));
    }

    @Override
    public String toString() {
        return id;
    }
}
