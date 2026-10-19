package com.perfect.client;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.event.system.EventListener;
import com.perfect.client.core.event.system.EventManager;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.render.animation.world.scan.ScanAnimation;
import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.postprocess.PostProcessManager;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.texture.TextureLoader;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.functional.ClientConfig;
import com.perfect.client.gui.hud.HudSettings;
import com.perfect.client.gui.widgets.ClickGuiScreen;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class PerfectClient {

	public static final String NAME = "Perfect Client";

	public static final String DISCORD = "https://discord.gg/eQbYN5VKeR";

	public static final List<String> AUTHORS = Util.make(
			new ArrayList<>(), list -> {
				list.add("c0nsoleee");
				list.add("Applee453");
				list.add("Artik");
			}
	);

	public static final String MOD_ID = "perfect-client";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final File FOLDER = FabricLoader.getInstance().getGameDir().resolve(MOD_ID).toFile();

	public static DecimalFormat decimalFormat = new DecimalFormat("#0.00");

	public static MinecraftClient mc;

	public static String prefix = "%";

	public static ClientConfig clientConfig;

	public static HudSettings hudSettings;

	public static final TextureLoader TEXTURE_LOADER = new TextureLoader();

	public static final EventManager EVENT_MANAGER = new EventManager();

	public static ClickGuiScreen clickGuiScreen;

	@Setter
	@Getter
	private static boolean initialized;


	public static void onInitializeGraphics() {
		Renderer2D.init();
		Fonts.init();
		Textures.init();
		ScanAnimation.init();
	}


	public static void onInitializePostProcess() {
		PostProcessManager.init();
	}


	public static void onInit() {
		new PerfectClientEventListener();
	}


	public static InputStream getResource(String name) {
		String resourcePath = "/assets/%s/%s".formatted(MOD_ID, name);
		return PerfectClient.class.getResourceAsStream(resourcePath);
	}

	public static Identifier getIdentifier(String name) {
		return Identifier.of(MOD_ID, name);
	}

	public static class PerfectClientEventListener extends EventListener {

		@Override
		public void onKey(KeyEvent event) {
			if (!clientConfig.getGuiBind().isSet()) {
				clientConfig.getGuiBindSetting().set(Bind.fromKey(GLFW.GLFW_KEY_RIGHT_ALT));
			}

			handleKeyOpen(event, true);
		}

		@Override
		public void onMouseButton(MouseButtonEvent event) {
			handleKeyOpen(event, false);
		}

		private void handleKeyOpen(InputEvent event, boolean key) {
			if (event.isPress()
					&& clientConfig.getGuiBind().matchesWithMod(key, event.getValue())
					&& canOpenGui()
			) {
				mc.setScreen(clickGuiScreen);
			}
		}

		private boolean canOpenGui() {
			return mc.currentScreen == null || mc.world == null;
		}
	}
}