package com.perfect.client.inject.mixin.impl;

import com.mojang.authlib.GameProfile;
import com.perfect.client.inject.mixinInterface.IChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.Visible.class)
public class ChatHudLineVisibleMixin implements IChatHudLine {

	@Shadow
	@Final
	private OrderedText content;

	@Unique
	private int perfect$id;

	@Unique
	private GameProfile sender;

	@Unique
	private boolean startOfEntry;

	@Unique
	private float x;

	@Override
	public int perfect$getId() {
		return perfect$id;
	}

	@Override
	public void perfect$setId(int id) {
		this.perfect$id = id;
	}

	@Override
	public String perfect$getText() {
		StringBuilder sb = new StringBuilder();

		content.accept((index, style, codePoint) -> {
			sb.appendCodePoint(codePoint);
			return true;
		});

		return sb.toString();
	}

	@Override
	public GameProfile perfect$getSender() {
		return sender;
	}

	@Override
	public void perfect$setSender(GameProfile profile) {
		sender = profile;
	}

	@Override
	public boolean perfect$isStartOfEntry() {
		return startOfEntry;
	}

	@Override
	public void perfect$setStartOfEntry(boolean start) {
		startOfEntry = start;
	}


	@Override
	public float perfect$getX() {
		return x;
	}

	@Override
	public void perfect$setX(float x) {
		this.x = x;
	}
}
