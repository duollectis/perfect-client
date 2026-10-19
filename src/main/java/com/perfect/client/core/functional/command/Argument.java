package com.perfect.client.core.functional.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.command.CommandSource.suggestMatching;

public abstract class Argument<T> implements ArgumentType<T> {
    protected static final DynamicCommandExceptionType NO_SUCH_VALUE = new DynamicCommandExceptionType(name ->
            Text.literal("Value " + name + " doesn't exist.")
    );

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        return parse(reader.readString());
    }

    protected abstract T parse(String argument) throws CommandSyntaxException;

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return suggestMatching(getCandidates(), builder);
    }

    protected Stream<String> getCandidates() {
        return Stream.empty();
    }
}
