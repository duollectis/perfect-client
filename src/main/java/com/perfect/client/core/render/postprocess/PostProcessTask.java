package com.perfect.client.core.render.postprocess;

import com.perfect.client.core.render.ScreenTexture;

import java.util.function.Consumer;


public class PostProcessTask {

	private final Consumer<ScreenTexture> outputConsumer;

	public PostProcessTask(Consumer<ScreenTexture> outputConsumer) {
		this.outputConsumer = outputConsumer;
	}

	public void accept(ScreenTexture output) {
		outputConsumer.accept(output);
	}
}
