package com.perfect.client.functional.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.perfect.client.core.functional.command.Argument;

import java.util.Arrays;
import java.util.stream.Stream;

public class EnumArgumentType<T extends Enum<?>> extends Argument<T> {

    private final T[] values;

    public EnumArgumentType(T[] values) {
        this.values = values;
    }

    @Override
    public T parse(String argument) throws CommandSyntaxException {
        for (T possible : values) {
            if (argument.equalsIgnoreCase(possible.toString())) {
                return possible;
            }
        }

        throw NO_SUCH_VALUE.create(argument);
    }

    @Override
    protected Stream<String> getCandidates() {
        return Arrays.stream(values).map(value -> value.toString().toLowerCase());
    }
}
