package com.perfect.client.core.manager;

import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.manager.core.*;
import com.perfect.client.manager.game.*;
import com.perfect.client.manager.game.item.ItemManager;
import com.perfect.client.manager.game.mining.BreakInfoManager;
import com.perfect.client.manager.game.neuro.NeuroManager;
import com.perfect.client.manager.game.ping.PingManager;
import com.perfect.client.manager.game.predict.PredictManager;
import com.perfect.client.manager.game.render.AnimationManager;
import com.perfect.client.manager.game.render.ParticleManager;
import com.perfect.client.manager.game.render.RenderManager;
import com.perfect.client.manager.game.rotation.RotationManager;
import com.perfect.client.manager.game.rotation.SensitivityManager;
import com.perfect.client.manager.game.screen.OverlayManager;
import com.perfect.client.manager.game.targetManager.TargetManager;
import com.perfect.client.manager.system.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class Managers {

	@Getter
	private static final List<ISystem<?>> generalSystems = new ArrayList<>();

	public static final NeuroManager NEURO = new NeuroManager();
	public static final BindManager BIND = new BindManager();
	public static final PingManager PING = new PingManager();
	public static final KeyStateManager KEY_STATE = new KeyStateManager();
	public static final MouseManager MOUSE = new MouseManager();
	public static final SoundManager SOUND = new SoundManager();
	public static final OverlayManager OVERLAY = new OverlayManager();
	public static final ItemManager ITEM = new ItemManager();
	public static final FriendManager FRIEND = new FriendManager();

	public static final ClientConfigManager CLIENT_CONFIG = addSavable(new ClientConfigManager());
	public static final FunctionManager FUNCTION = addSavable(new FunctionManager());
	public static final HudManager HUD = addSavable(new HudManager());
	public static final ColorManager COLOR = addSavable(new ColorManager());
	public static final UIManager UI = addSavable(new UIManager());

	public static final RenderManager RENDER = new RenderManager();
	public static final CommandManager COMMAND = new CommandManager();
	public static final PlayerManager PLAYER = new PlayerManager();
	public static final AsyncManager ASYNC = new AsyncManager();
	public static final RotationManager ROTATION = new RotationManager();
	public static final EntityManager ENTITY = new EntityManager();
	public static final TickRateManager TICKRATE = new TickRateManager();
	public static final PendingManager PENDING = new PendingManager();
	public static final CrystalManager CRYSTAL = new CrystalManager();
	public static final NetworkManager NETWORK = new NetworkManager();
	public static final ParticleManager PARTICLE = new ParticleManager();
	public static final AntiCheatManager ANTI_CHEAT = new AntiCheatManager();
	public static final BreakInfoManager BREAK_INFO = new BreakInfoManager();
	public static final PredictManager PREDICT = new PredictManager();
	public static final AnimationManager ANIMATION = new AnimationManager();
	public static final SensitivityManager SENSITIVITY = new SensitivityManager();
	public static final TargetManager TARGET = new TargetManager();
	public static final ProjectileManager PROJECTILE = new ProjectileManager();

	/**
	 * Удаление данного метода карается смертной казнью.
	 */
	public static void init() {
		
	}

	public static <T extends ISystem<T>> T addSavable(T manager) {
		generalSystems.add(manager);
		return manager;
	}

}
