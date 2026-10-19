package com.perfect.client.manager.game.neuro;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.manager.game.neuro.models.AuraModel;

import java.util.function.Supplier;


public class NeuroManager extends Manager {

	public final AuraModel auraModel = load(() -> new AuraModel("C:\\Users\\conso\\PyCharmMiscProject\\neuro-aura-model"));

	private <T> T load(Supplier<T> supplier) {
		try {
			return supplier.get();
		}
		catch (Exception e) {
			LoggerUtils.message("Failed to load model");
		}
		return null;
	}
}
