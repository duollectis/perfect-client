package com.perfect.client.functional.function.combat.autoCrystal;

import com.google.common.collect.Lists;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.collection.EvictingQueue;
import com.perfect.client.core.utils.collection.UpdatingCollection;
import com.perfect.client.core.utils.functions.Strict;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.combat.autoCrystal.data.BreakInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.PlaceInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.State;
import com.perfect.client.functional.function.combat.autoCrystal.data.TargetInfo;
import com.perfect.client.functional.function.combat.autoCrystal.handlers.*;
import com.perfect.client.functional.function.combat.autoCrystal.helpers.*;
import com.perfect.client.manager.game.targetManager.ITarget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.core.utils.game.world.BlockUtils.isReplaceable;

@Getter
@Setter

public class AutoCrystal extends Function implements ITarget {

	public static final float MIN_FACE_PLACE_DAMAGE = 1.5f;

	private final AutoCrystalConfig config = new AutoCrystalConfig(this);

	private final PlaceHelper placeHelper = new PlaceHelper(this, config);
	private final BreakHelper breakHelper = new BreakHelper(this, config);
	private final RotationHelper rotationHelper = new RotationHelper(this, config);
	private final TimerHelper timerHelper = new TimerHelper(this, config);
	private final TargetHelper targetHelper = new TargetHelper(this, config);
	private final DamageHelper damageHelper = new DamageHelper(this, config);

	private boolean pauseRender;

	private final PlaceRenderer renderer = new PlaceRenderer(
			() -> config.getLineColor().get(),
			() -> config.getSideColor().get(),
			() -> config.getCosmetics().get().bundle()
	).pauseSupplier(() -> pauseRender);

	private final UpdatingCollection<EndCrystalEntity> breakLimitedCrystals = new UpdatingCollection<>();

	private final ConcurrentHashMap<EndCrystalEntity, Pair<Integer, Long>> clickedCrystals = new ConcurrentHashMap<>();

	private volatile EndCrystalEntity brokenOnSpawn;

	private boolean placedOnSpawn;

	private final TimerUtils timer = new TimerUtils();
	private final TimerUtils supportTimer = new TimerUtils();
	private final TimerUtils lastRenderTimer = new TimerUtils();

	private volatile int crystalAgeMillis;

	private LivingEntity renderTarget;
	private List<LivingEntity> targets;

	private final HashMap<LivingEntity, TargetInfo> targetsInfo = new HashMap<>();

	private PlaceInfo placeInfo;
	private BreakInfo breakInfo;

	private final Deque<Long> attackLatency = new EvictingQueue<>(20);
	private final Map<Integer, Long> attackPackets = Collections.synchronizedMap(new ConcurrentHashMap<>());

	private double crystalsPerSecond = 0.0;

	public AutoCrystal() {
		super("auto-crystal", "Auto Crystal", Category.COMBAT, "Crystal Aura");

		addHandler(new EntityRemovedHandler(this, config));
		addHandler(new EntitySpawnHandler(this, config));
		addHandler(new RotateHandler(this, config));
		addHandler(new TickHandler(this, config));
		addHandler(new BlockBreakHandler(this, config));
		addHandler(new InputHandler(this, config));
		addHandler(new CrystalBreakHandler(this, config));
	}

	@Override
	public void onEnable() {
		timer.reset();
		targets = new ArrayList<>();
	}

	@Override
	public void onDisable() {
		timer.reset();
		supportTimer.reset();

		targets = null;
		renderTarget = null;
		placeInfo = null;
		breakInfo = null;
		breakLimitedCrystals.clear();
		clickedCrystals.clear();
		placedOnSpawn = false;
		targetsInfo.clear();
		attackLatency.clear();
		attackPackets.clear();

		pauseRender = false;
	}

	@Override
	public LivingEntity getTarget() {
		return renderTarget;
	}

	@Override
	public String getStatus() {
		// [Artik, 10.5, 18]

		if (renderTarget == null || mc.world == null || mc.player == null) {
			return "None, 0.0m, 0";
		}

		String targetName = renderTarget.getType().getName().getString();
		if (renderTarget instanceof PlayerEntity player) {
			targetName = player.getGameProfile().getName();
		}

		return String.format(Locale.US, "%s, %.1fm, %d",
				targetName,
				mc.player.distanceTo(renderTarget),
				(int) crystalsPerSecond
		);
	}

	public long getTime() {
		return timer.getMillis();
	}

	public void render(BlockPos pos) {
		renderer.clear();

		if (config.getRender().get()) {
			renderer.add(pos);
			lastRenderTimer.reset();
		}
	}

	
	public void autoCrystalTick() {
		if (mc.world == null || mc.player == null || mc.getNetworkHandler() == null) {
			return;
		}

		updateTargets();

		if (targets.isEmpty() || shouldPause()) {
			breakInfo = null;
			placeInfo = null;
			pauseRender = false;
			renderTarget = null;
			return;
		}

		updateTargetsInfo();

		breakInfo = findBreakInfo();
		placeInfo = findPlaceInfo(canSupport());

		if (placeInfo != null) {
			renderTarget = placeInfo.getTarget();
		}
		else {
			renderTarget = null;
		}

		pauseRender = needPauseRender();
	}

	private boolean needPauseRender() {
		return placeInfo != null && placeHelper.findSwap() != null;
	}

	public void updateTargets() {
		targets = TargetUtils.getTargetsInRange(config.getTargetRange().get(), true, this::isGoodTarget);
	}

	public void updateTargetsInfo() {
		targetHelper.fillTargetsInfo(targets);
	}

	public PlaceInfo findPlaceInfo(boolean support) {
		return placeHelper.getPlacePos(createState(), support);
	}

	public BreakInfo findBreakInfo() {
		return breakHelper.getBreak(createState());
	}

	private boolean isGoodTarget(LivingEntity entity) {
		if (!entity.isAlive() || entity.isDead()) {
			return false;
		}

		if (!config.getTargets().contains(entity.getType())) {
			return false;
		}

		if (!(entity instanceof PlayerEntity player)) {
			return true;
		}

		if (config.getIgnoreNaked().get() && EntityUtils.isNaked(player)) {
			return false;
		}

		if (!TrapUtils.isTrapped(entity, TrapType.TRAP, false)) {
			return true;
		}

		return !TrapUtils.isTrapped(entity, TrapType.SURROUND, false);
	}

	
	public void sendActions() {
		if (placeInfo == null && breakInfo == null) {
			placedOnSpawn = false;
			brokenOnSpawn = null;
			return;
		}

		boolean attacked = false;

		if (canSupportThisTick()) {
			InteractUtils.placeBlock(
					placeInfo.getSupportResult(),
					InvUtils.findInHotbar(Items.OBSIDIAN),
					com.perfect.client.core.utils.functions.interact.Rotate.NONE
			);
			supportTimer.reset();
		}

		if (canBreakThisTick()) {
			breakHelper.attackCrystal(breakInfo);
			attacked = true;

			registerCrystalAttack();
		}

		if (canPlaceThisTick(attacked)) {
			placeHelper.placeCrystal(placeInfo);
		}

		placedOnSpawn = false;
		brokenOnSpawn = null;
	}

	private boolean canSupportThisTick() {
		if (placeInfo == null) {
			return false;
		}
		if (!config.getSupport().get()) {
			return false;
		}
		if (Managers.PENDING.contains(placeInfo.getPos())) {
			return false;
		}
		if (!supportTimer.passedMillis(config.getSupportDelay().get())) {
			return false;
		}
		return isReplaceable(placeInfo.getPos());
	}

	private boolean canBreakThisTick() {
		if (breakInfo == null) {
			return false;
		}
		if (breakInfo.getCrystal() == brokenOnSpawn) {
			return false;
		}
		if (config.getCrystalAge().get() > 0) {
			if (Managers.CRYSTAL.getAge(breakInfo.getCrystal()) + getAwaitMillis() < config.getCrystalAge().get()) {
				return false;
			}
		}
		return timerHelper.isBreakTimerPassed();
	}

	private boolean canPlaceThisTick(boolean attackedThisTick) {
		if (attackedThisTick && !config.getSequential().get()) {
			return false;
		}
		if (placeInfo == null) {
			return false;
		}
		if (placedOnSpawn) {
			return false;
		}
		return timerHelper.isPlaceTimerPassed();
	}

	public boolean isStrictTiming() {
		if (mc.player == null) {
			return false;
		}
		if (!clientConfig.getStrictMode().isStrict()) {
			return false;
		}

		if (clientConfig.getStrictMode() == Strict.FULL) {
			return true;
		}
		else if (clientConfig.getStrictMode() == Strict.GRIM) {
			return needGrimLegit();
		}
		return false;
	}

	private boolean needGrimLegit() {
		return PlayerUtils.isMoving();
	}

	private boolean canSupport() {
		if (!config.getSupport().get()) {
			return false;
		}
		if (!supportTimer.passedMillis(config.getSupportDelay().get())) {
			return false;
		}
		return InvUtils.findInHotbar(Items.OBSIDIAN).found();
	}

	public boolean shouldPauseOther() {
		return isEnabled() && placeInfo != null;
	}

	public boolean shouldPause() {
		if (config.getAntiResourcesExplosion().get() && isStandingInResources()) {
			return true;
		}

		return PlayerUtils.shouldPause(
				config.getPauseOnEat().get(),
				false,
				config.getPauseOnAutoExp().get(),
				config.getPauseOnBlink().get()
		);
	}

	private boolean isStandingInResources() {
		assert mc.player != null;

		Box box = new Box(mc.player.getBlockPos()).expand(1);

		return !WorldUtils.getEntities(box, entity -> entity instanceof ItemEntity).isEmpty();
	}

	public State createState() {
		return new State(
				getTime(),
				placeInfo,
				breakInfo,
				targets,
				targetsInfo,
				breakLimitedCrystals,
				clickedCrystals
		);
	}

	public int getAwaitMillis() {
		if (!config.getAwait().get() || attackLatency.isEmpty()) {
			return 0;
		}

		float avg = 0.0f;

		ArrayList<Long> attackLatency = Lists.newArrayList(this.attackLatency);

		if (!attackLatency.isEmpty()) {
			for (float time : attackLatency) {
				avg += time;
			}
			avg /= attackLatency.size();
		}

		return (int) avg;
	}

	private void registerCrystalAttack() {
		long now = System.currentTimeMillis();
		attackLatency.addLast(now);

		while (!attackLatency.isEmpty() && now - attackLatency.peekFirst() > 1000) {
			attackLatency.pollFirst();
		}

		crystalsPerSecond = attackLatency.size();
	}

	public enum Rotate {
		NONE,
		FREE,
		STRICT
	}

	public enum Slowness {
		PLACE,
		BREAK
	}

	public enum RayTraceMode {
		NONE,
		CENTER,
		EDGES,
		STRICT
	}
}