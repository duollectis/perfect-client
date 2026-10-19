package com.perfect.client.core.render.texture;

import static com.perfect.client.PerfectClient.TEXTURE_LOADER;


public class Textures {

	public static LoadedTexture ARROW;
	public static LoadedTexture FIREFLY;
	public static LoadedTexture CIRCLE;
	public static LoadedTexture STAR;
	public static LoadedTexture MAGNIFIER;
	public static LoadedTexture KEYBOARD;
	public static LoadedTexture CHECK;
	public static LoadedTexture PLUS;
	public static LoadedTexture MINUS;
	public static LoadedTexture DRAGGABLE;

	
	public static void init() {
		ARROW = load("arrow");
		FIREFLY = load("particles/firefly2");
		CIRCLE = load("particles/circle");
		STAR = load("particles/star");
		MAGNIFIER = load("ui/magnifier");
		KEYBOARD = load("ui/keyboard");
		CHECK = load("ui/check");
		PLUS = load("ui/plus");
		MINUS = load("ui/minus");
		DRAGGABLE = load("ui/draggable");
	}

	private static LoadedTexture load(String name) {
		return TEXTURE_LOADER.loadTexture("textures/%s.png".formatted(name));
	}
}
