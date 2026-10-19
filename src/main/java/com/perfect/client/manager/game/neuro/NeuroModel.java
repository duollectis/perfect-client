package com.perfect.client.manager.game.neuro;

import org.tensorflow.SavedModelBundle;


public class NeuroModel {

	protected SavedModelBundle model;

	public NeuroModel(String path) {
		model = SavedModelBundle.load(path, "serve");
	}
}
