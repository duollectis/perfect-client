package com.perfect.client.manager.system;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.setting.BaseSetting;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.functional.function.combat.aimBot.AimBot;
import com.perfect.client.functional.function.combat.anchorAura.AnchorAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.autoArmor.AutoArmor;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoMine.AutoMine;
import com.perfect.client.functional.function.combat.autoTrap.AutoTrap;
import com.perfect.client.functional.function.combat.autoWeb.AutoWeb;
import com.perfect.client.functional.function.combat.backtrack.Backtrack;
import com.perfect.client.functional.function.combat.blocker.Blocker;
import com.perfect.client.functional.function.combat.bowRelease.BowRelease;
import com.perfect.client.functional.function.combat.burrow.Burrow;
import com.perfect.client.functional.function.combat.crawlTrap.CrawlTrap;
import com.perfect.client.functional.function.combat.criticals.Criticals;
import com.perfect.client.functional.function.combat.crystalOptimizer.CrystalOptimizer;
import com.perfect.client.functional.function.combat.holeFill.HoleFill;
import com.perfect.client.functional.function.combat.maceKill.MaceKill;
import com.perfect.client.functional.function.combat.offhand.Offhand;
import com.perfect.client.functional.function.combat.pistonPush.PistonPush;
import com.perfect.client.functional.function.combat.selfTrap.SelfTrap;
import com.perfect.client.functional.function.combat.suicide.Suicide;
import com.perfect.client.functional.function.combat.surround.Surround;
import com.perfect.client.functional.function.combat.tntAura.TntAura;
import com.perfect.client.functional.function.combat.triggerBot.TriggerBot;
import com.perfect.client.functional.function.misc.TestFunction;
import com.perfect.client.functional.function.misc.antiAfk.AntiAFK;
import com.perfect.client.functional.function.misc.antiBot.AntiBot;
import com.perfect.client.functional.function.misc.antiPacketCrash.AntiPacketCrash;
import com.perfect.client.functional.function.misc.autoEz.AutoEz;
import com.perfect.client.functional.function.misc.autoOminous.AutoOminous;
import com.perfect.client.functional.function.misc.autoWither.AutoWither;
import com.perfect.client.functional.function.misc.betterChat.BetterChat;
import com.perfect.client.functional.function.misc.betterTab.BetterTab;
import com.perfect.client.functional.function.misc.discordPresence.DiscordPresence;
import com.perfect.client.functional.function.misc.enderChestFarm.EnderChestFarm;
import com.perfect.client.functional.function.misc.fakePlayer.FakePlayer;
import com.perfect.client.functional.function.misc.fastLatency.FastLatency;
import com.perfect.client.functional.function.misc.minigameHelper.MinigameHelper;
import com.perfect.client.functional.function.misc.noInteract.NoInteract;
import com.perfect.client.functional.function.misc.notifications.Notifications;
import com.perfect.client.functional.function.misc.packetSounds.PacketSounds;
import com.perfect.client.functional.function.misc.pingSpoof.PingSpoof;
import com.perfect.client.functional.function.misc.spammer.Spammer;
import com.perfect.client.functional.function.misc.testSounds.TestSounds;
import com.perfect.client.functional.function.movement.autoSprint.AutoSprint;
import com.perfect.client.functional.function.movement.elytraFly.ElytraFly;
import com.perfect.client.functional.function.movement.fakeLag.FakeLag;
import com.perfect.client.functional.function.movement.fastFall.FastFall;
import com.perfect.client.functional.function.movement.guiMove.GuiMove;
import com.perfect.client.functional.function.movement.holeSnap.HoleSnap;
import com.perfect.client.functional.function.movement.legacySneak.LegacySneak;
import com.perfect.client.functional.function.movement.noSlow.NoSlow;
import com.perfect.client.functional.function.movement.phase.Phase;
import com.perfect.client.functional.function.movement.safeWalk.SafeWalk;
import com.perfect.client.functional.function.movement.speed.Speed;
import com.perfect.client.functional.function.movement.step.Step;
import com.perfect.client.functional.function.movement.tickShift.TickShift;
import com.perfect.client.functional.function.movement.velocity.Velocity;
import com.perfect.client.functional.function.movement.waterSpeed.WaterSpeed;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetect;
import com.perfect.client.functional.function.player.antiCrawl.AntiCrawl;
import com.perfect.client.functional.function.player.autoBuy.AutoBuy;
import com.perfect.client.functional.function.player.autoExp.AutoExp;
import com.perfect.client.functional.function.player.autoKit.AutoKit;
import com.perfect.client.functional.function.player.autoRespawn.AutoRespawn;
import com.perfect.client.functional.function.player.autoSwap.AutoSwap;
import com.perfect.client.functional.function.player.autoTool.AutoTool;
import com.perfect.client.functional.function.player.betterInventory.BetterInventory;
import com.perfect.client.functional.function.player.elytraSwap.ElytraSwap;
import com.perfect.client.functional.function.player.hitboxDesync.HitboxDesync;
import com.perfect.client.functional.function.player.hotbarRefill.HotbarRefill;
import com.perfect.client.functional.function.player.illegalDisconnect.IllegalDisconnect;
import com.perfect.client.functional.function.player.infiniteElytra.InfiniteElytra;
import com.perfect.client.functional.function.player.middleClick.MiddleClick;
import com.perfect.client.functional.function.player.noDelay.NoDelay;
import com.perfect.client.functional.function.player.noServerRotate.NoServerRotate;
import com.perfect.client.functional.function.player.packetMine.PacketMine;
import com.perfect.client.functional.function.player.rotation.Rotation;
import com.perfect.client.functional.function.player.selfBow.SelfBow;
import com.perfect.client.functional.function.visuals.ambience.Ambience;
import com.perfect.client.functional.function.visuals.animations.Animations;
import com.perfect.client.functional.function.visuals.arrows.Arrows;
import com.perfect.client.functional.function.visuals.aspectRatio.AspectRatio;
import com.perfect.client.functional.function.visuals.auctionHelper.AuctionHelper;
import com.perfect.client.functional.function.visuals.blockEsp.BlockESP;
import com.perfect.client.functional.function.visuals.blockSelection.BlockSelection;
import com.perfect.client.functional.function.visuals.breakEsp.BreakESP;
import com.perfect.client.functional.function.visuals.chams.Chams;
import com.perfect.client.functional.function.visuals.chinaHat.ChinaHat;
import com.perfect.client.functional.function.visuals.entityEsp.EntityESP;
import com.perfect.client.functional.function.visuals.fieldOfView.FieldOfView;
import com.perfect.client.functional.function.visuals.firefly.FireFly;
import com.perfect.client.functional.function.visuals.freecam.Freecam;
import com.perfect.client.functional.function.visuals.fullBright.FullBright;
import com.perfect.client.functional.function.visuals.hands.Hands;
import com.perfect.client.functional.function.visuals.holeEsp.HoleESP;
import com.perfect.client.functional.function.visuals.imageFilter.ImageFilter;
import com.perfect.client.functional.function.visuals.jumpCircles.JumpCircles;
import com.perfect.client.functional.function.visuals.killeffect.KillEffect;
import com.perfect.client.functional.function.visuals.legacyPotions.LegacyPotions;
import com.perfect.client.functional.function.visuals.logoutspots.LogoutSpots;
import com.perfect.client.functional.function.visuals.nameProtect.NameProtect;
import com.perfect.client.functional.function.visuals.nametags.Nametags;
import com.perfect.client.functional.function.visuals.noRender.NoRender;
import com.perfect.client.functional.function.visuals.particles.Particles;
import com.perfect.client.functional.function.visuals.phaseEsp.PhaseESP;
import com.perfect.client.functional.function.visuals.popChams.PopChams;
import com.perfect.client.functional.function.visuals.prediction.Prediction;
import com.perfect.client.functional.function.visuals.targetEsp.TargetESP;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.util.*;

@SuppressWarnings("unused")
public class FunctionManager extends Manager implements ISystem<FunctionManager> {

	private static final List<Category> categories = new ArrayList<>();
	private static final List<Function> functions = new ArrayList<>();

	private final Map<Category, List<Function>> groups = new Reference2ReferenceOpenHashMap<>();

	public static final AutoWither autoWither = add(new AutoWither());
	public static final Backtrack backtrack = add(new Backtrack());
	public static final SafeWalk safeWalk = add(new SafeWalk());
	public static final LegacySneak legacySneak = add(new LegacySneak());
	public static final TntAura tntAura = add(new TntAura());
	public static final PacketMine packetMine = add(new PacketMine());
	public static final BreakESP breakESP = add(new BreakESP());
	public static final BetterChat betterChat = add(new BetterChat());
	public static final BetterTab betterTab = add(new BetterTab());
	public static final AntiAcDetect antiAcDetect = add(new AntiAcDetect());
	public static final IllegalDisconnect illegalDisconnect = add(new IllegalDisconnect());
	public static final AutoWeb autoWeb = add(new AutoWeb());
	public static final PhaseESP phaseESP = add(new PhaseESP());
	public static final Phase phase = add(new Phase());
	public static final AntiCrawl antiCrawl = add(new AntiCrawl());
	public static final CrawlTrap crawlTrap = add(new CrawlTrap());
	public static final AttackAura attackAura = add(new AttackAura());
	public static final ImageFilter imageFilter = add(new ImageFilter());
	public static final HitboxDesync hitboxDesync = add(new HitboxDesync());
	public static final MiddleClick middleClick = add(new MiddleClick());
	public static final NoDelay noDelay = add(new NoDelay());
	public static final NoServerRotate noServerRotate = add(new NoServerRotate());
	public static final PingSpoof pingSpoof = add(new PingSpoof());
	public static final Rotation rotation = add(new Rotation());
	public static final Spammer spammer = add(new Spammer());
	public static final Prediction prediction = add(new Prediction());
	public static final AutoRespawn autoRespawn = add(new AutoRespawn());
	public static final CrystalOptimizer crystalOptimizer = add(new CrystalOptimizer());
	public static final Blocker blocker = add(new Blocker());
	public static final Suicide suicide = add(new Suicide());
	public static final EnderChestFarm enderChestFarm = add(new EnderChestFarm());
	public static final HoleFill holeFill = add(new HoleFill());
	public static final ChinaHat chinaHat = add(new ChinaHat());
	public static final AntiPacketCrash antiPacketCrash = add(new AntiPacketCrash());
	public static final NameProtect nameProtect = add(new NameProtect());
	public static final AutoSwap autoSwap = add(new AutoSwap());
	public static final AspectRatio aspectRatio = add(new AspectRatio());
	public static final JumpCircles JUMP_EFFECTS = add(new JumpCircles());
	public static final Particles particles = add(new Particles());
	public static final EntityESP entityESP = add(new EntityESP());
	public static final HoleESP holeESP = add(new HoleESP());
	public static final Ambience ambience = add(new Ambience());
	public static final Arrows arrows = add(new Arrows());
	public static final BlockSelection blockSelection = add(new BlockSelection());
	public static final Chams chams = add(new Chams());
	public static final FieldOfView fieldOfView = add(new FieldOfView());
	public static final Hands hands = add(new Hands());
	public static final Nametags nametags = add(new Nametags());
	public static final NoRender noRender = add(new NoRender());
	public static final LegacyPotions legacyPotions = add(new LegacyPotions());
	public static final AutoExp autoExp = add(new AutoExp());
	public static final HotbarRefill hotbarRefill = add(new HotbarRefill());
	public static final ElytraSwap elytraSwap = add(new ElytraSwap());
	public static final Freecam freecam = add(new Freecam());
	public static final AnchorAura anchorAura = add(new AnchorAura());
	public static final AutoCrystal autoCrystal = add(new AutoCrystal());
	public static final AutoMine autoMine = add(new AutoMine());
	public static final Offhand offhand = add(new Offhand());
	public static final AutoTrap autoTrap = add(new AutoTrap());
	public static final SelfTrap selfTrap = add(new SelfTrap());
	public static final Surround surround = add(new Surround());
	public static final Burrow burrow = add(new Burrow());
	public static final Criticals criticals = add(new Criticals());
	public static final PistonPush pistonPush = add(new PistonPush());
	public static final TriggerBot triggerBot = add(new TriggerBot());
	public static final FakePlayer fakePlayer = add(new FakePlayer());
	public static final FastLatency fastLatency = add(new FastLatency());
	public static final NoInteract noInteract = add(new NoInteract());
	public static final Notifications notifications = add(new Notifications());
	public static final PacketSounds packetSounds = add(new PacketSounds());
	public static final TestFunction testFunction = add(new TestFunction());
	public static final ElytraFly elytraFly = add(new ElytraFly());
	public static final AutoSprint autoSprint = add(new AutoSprint());
	public static final FakeLag fakeLag = add(new FakeLag());
	public static final FastFall fastFall = add(new FastFall());
	public static final HoleSnap holeSnap = add(new HoleSnap());
	public static final Speed speed = add(new Speed());
	public static final Step step = add(new Step());
	public static final TickShift tickShift = add(new TickShift());
	public static final Velocity velocity = add(new Velocity());
	public static final WaterSpeed waterSpeed = add(new WaterSpeed());
	public static final FullBright fullBright = add(new FullBright());
	public static final InfiniteElytra infiniteElytra = add(new InfiniteElytra());
	public static final AutoOminous autoOminous = add(new AutoOminous());
	public static final DiscordPresence discordPresence = add(new DiscordPresence());
	public static final NoSlow noSlow = add(new NoSlow());
	public static final FireFly fireFly = add(new FireFly());
	public static final AntiBot antiBot = add(new AntiBot());
	public static final AutoTool autoTool = add(new AutoTool());
	public static final KillEffect killEffect = add(new KillEffect());
	public static final TestSounds testSounds = add(new TestSounds());
	public static final Animations animations = add(new Animations());
	public static final MinigameHelper minigameHelper = add(new MinigameHelper());
	public static final AimBot aimBot = add(new AimBot());
	public static final TargetESP targetESP = add(new TargetESP());
	public static final AntiAFK antiAFK = add(new AntiAFK());
	public static final AuctionHelper auctionHelper = add(new AuctionHelper());
	public static final BlockESP blockESP = add(new BlockESP());
	public static final AutoBuy autoBuy = add(new AutoBuy());
	public static final GuiMove guiMove = add(new GuiMove());
	public static final BetterInventory betterInventory = add(new BetterInventory());
	public static final AutoKit autoKit = add(new AutoKit());
	public static final PopChams popChams = add(new PopChams());
	public static final LogoutSpots logoutSpots = add(new LogoutSpots());
	public static final BowRelease bowRelease = add(new BowRelease());
	public static final SelfBow selfBow = add(new SelfBow());
	public static final AutoArmor autoArmor = add(new AutoArmor());
	public static final AutoEz autoEz = add(new AutoEz());
	public static final MaceKill maceKill = add(new MaceKill());

	public FunctionManager() {
		super();

		initCategories();
		initFunctions();

		LoggerUtils.message(functions.size() + " functions has been initialized.");

		sort();
	}

	private static <T extends Function> T add(T function) {
		functions.add(function);
		return function;
	}

	private void initFunctions() {
		functions.forEach(function -> {
			Managers.BIND.add(function.getBindListener());

			getGroup(function.getCategory()).add(function);

			for (SettingGroup group : function.getSettings()) {
				for (BaseSetting setting : group) {
					setting.setFunction(function);
				}
			}
		});
	}

	@Override
	public String getId() {
		return "functions";
	}

	private static void initCategories() {
		for (Category category : Category.values()) {
			FunctionManager.registerCategory(category);
		}
	}

	public static Iterable<Category> loopCategories() {
		return categories;
	}

	private static void registerCategory(Category category) {
		categories.add(category);
	}

	public Function getById(String id) {
		for (Function function : functions) {
			if (function.getId().equalsIgnoreCase(id)) {
				return function;
			}
		}

		return null;
	}

	public Function getByName(String name) {
		for (Function function : functions) {
			if (function.getName().replaceAll(" ", "").equalsIgnoreCase(name)) {
				return function;
			}
		}

		return null;
	}

	public void sort() {
		for (List<Function> functions : groups.values()) {
			functions.sort(Comparator.comparing(function -> function.getName(false)));
		}

		functions.sort(Comparator.comparing(function -> function.getName(false)));
	}

	public List<Function> getGroup(Category category) {
		return groups.computeIfAbsent(category, c -> new ArrayList<>());
	}

	public Collection<Function> getAll() {
		return functions;
	}

	// ———————————————————————————————INIT———————————————————————————————— //

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		JsonArray functionListJson = JsonArray.of(getAll());

		json.add("function-list", functionListJson);
		return json;
	}

	@Override
	public FunctionManager fromJson(JsonObject json) {
		JsonArray functionListJson = json.getJsonArray("function-list");

		if (functionListJson == null) {
			return this;
		}

		for (JsonElement<?> element : functionListJson) {
			JsonObject functionJson = element.asJsonObject();
			Function function = getById(functionJson.getString("name"));

			if (function != null) {
				function.getSettings().forEachSetting(baseSetting -> {
					if (baseSetting instanceof Setting<?> setting) {
						setting.reset();
					}
				});
				try {
					function.fromJson(functionJson);
				}
				catch (Exception exception) {
					LoggerUtils.warning("Failed to load config for %s".formatted(function.getName()));
				}
			}
		}

		return this;
	}

	@Override
	public void reset() {
		for (Function function : functions) {
			function.resetOptions();
		}
	}
}
