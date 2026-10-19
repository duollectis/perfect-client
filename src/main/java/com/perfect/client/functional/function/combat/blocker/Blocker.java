package com.perfect.client.functional.function.combat.blocker;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunction;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.combat.blocker.helpers.BlockerMode;
import com.perfect.client.functional.function.combat.blocker.modes.blind.BlindMode;
import com.perfect.client.functional.function.combat.blocker.modes.normal.NormalMode;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

@Getter

public class Blocker extends PlaceFunction {

	private final BlockerConfig config = new BlockerConfig(this);

	private final ModeContainer<Mode, BlockerMode> modeContainer = createModeContainer(config::getMode)
			.add(Mode.NORMAL, new NormalMode(this, config))
			.add(Mode.BLIND, new BlindMode(this, config));

	private List<BlockPos> selfTrap = new ArrayList<>();
	private List<BlockPos> surround = new ArrayList<>();

	private final List<BlockPos> protectPositions = new ArrayList<>();

	private boolean forceSurround;
	private boolean forceSelfTrap;

	public Blocker() {
		super("blocker", "Blocker", Category.COMBAT, true, false);
	}

	public void onSurroundBind() {
		forceSurround = !forceSurround;
	}

	public void onSelfTrapBind() {
		forceSelfTrap = !forceSelfTrap;
	}

	@Override
	public void onDisable() {
		forceSurround = false;
		forceSelfTrap = false;
	}

	@Override
	public void onPostTick() {
		selfTrap = TrapUtils.getTrap(mc.player, TrapType.TRAP);
		surround = TrapUtils.getTrap(mc.player, TrapType.SURROUND);

		modeContainer.getCurrentMode().onTick();
	}

	@Override
	protected List<BlockPos> getTrap() {
		protectPositions.clear();

		modeContainer.getCurrentMode().addTrap(protectPositions);
		handleForceBlocker();

		return protectPositions;
	}

	private void handleForceBlocker() {
		if (forceSelfTrap) {
			forceSelfTrap();
		}
		if (forceSurround) {
			forceSurround();
		}
	}

	private void forceSelfTrap() {
		for (BlockPos pos : selfTrap) {
			if (!canPlaceAt(pos)) {
				continue;
			}
			addInList(protectPositions, pos.up());
		}
	}

	private void forceSurround() {
		for (BlockPos pos : surround) {
			if (!canPlaceAt(pos)) {
				continue;
			}

			for (BlockPos protectPos : PositionMath.getSurroundingBlocks(pos)) {
				addInList(protectPositions, protectPos);
			}
		}
	}

	@Override
	protected EndCrystalEntity getCrystal(List<BlockPos> positions) {
		for (BlockPos pos : protectPositions) {
			for (Entity entity : BlockUtils.getEntities(pos)) {
				if (entity instanceof EndCrystalEntity crystal) {
					return crystal;
				}
			}
		}
		return null;
	}

	@Override
	protected PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal) {
		return null;
	}

	
	public void addBlockProtect(List<BlockPos> positions, ProtectMode mode, BlockPos pos) {
		if (mode == ProtectMode.SELF_TRAP) {
			addInList(positions, pos.up());
		}
		else if (mode == ProtectMode.SURROUND) {
			for (Direction direction : Direction.values()) {
				addInList(positions, pos.offset(direction));
			}
		}
	}

	public void addInList(List<BlockPos> list, BlockPos item) {
		if (canPlaceAt(item)) {
			Utils.add(list, item);
		}
	}

	public static boolean canPlaceAt(BlockPos pos) {
		return BlockUtils.isAir(pos) && !hasEntity(pos);
	}

	private static boolean hasEntity(BlockPos pos) {
		return BlockUtils.hasEntity(pos, Blocker::isObstructiveEntity);
	}

	private static boolean isObstructiveEntity(Entity entity) {
		if (entity instanceof EndCrystalEntity) {
			return false;
		}
		return entity.intersectionChecked;
	}

	@Override
	protected PlaceFunctionConfig<Blocker> getConfig() {
		return config;
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	public enum Mode {
		NORMAL,
		BLIND
	}

	public enum BlindProtectMode {
		NONE,
		PREDICTIVE,
		DELAYED
	}

	public enum ProtectMode {
		FULL,
		SURROUND,
		SELF_TRAP;

		public boolean surround() {
			return this == FULL || this == SURROUND;
		}

		public boolean selfTrap() {
			return this == FULL || this == SELF_TRAP;
		}
	}
}
