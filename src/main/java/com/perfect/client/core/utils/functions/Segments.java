package com.perfect.client.core.utils.functions;

import com.perfect.client.core.utils.text.ITranslatable;
import lombok.Getter;

public enum Segments implements ITranslatable {
    TWO(2),
    FOUR(4),
    EIGHT(8);

    @Getter
    private final int segmentsValue;
    Segments(int segmentsValue) {
        this.segmentsValue = segmentsValue;
    }

    @Override
    public String getTranslationId() {
        return "segments";
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}