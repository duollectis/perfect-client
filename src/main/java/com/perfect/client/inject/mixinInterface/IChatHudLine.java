package com.perfect.client.inject.mixinInterface;

import com.mojang.authlib.GameProfile;

public interface IChatHudLine {

	int perfect$getId();

	void perfect$setId(int id);

	GameProfile perfect$getSender();

	void perfect$setSender(GameProfile sender);

	String perfect$getText();

	boolean perfect$isStartOfEntry();

	void perfect$setStartOfEntry(boolean start);

	float perfect$getX();

	void perfect$setX(float x);
}
