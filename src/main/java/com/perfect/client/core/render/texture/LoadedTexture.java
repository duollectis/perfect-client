package com.perfect.client.core.render.texture;

import lombok.Getter;

@Getter
public class LoadedTexture {
    private final int glId;

    public LoadedTexture(int glId) {
        this.glId = glId;
    }
}
