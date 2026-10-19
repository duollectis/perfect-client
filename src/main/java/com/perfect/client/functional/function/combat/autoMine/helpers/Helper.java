package com.perfect.client.functional.function.combat.autoMine.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.autoMine.AutoMine;
import com.perfect.client.functional.function.combat.autoMine.AutoMineConfig;
import com.perfect.client.functional.function.combat.autoMine.data.BlockCheckResult;
import com.perfect.client.functional.function.combat.autoMine.data.PositionInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.core.utils.game.world.BlockUtils.isBreakable;
import static com.perfect.client.core.utils.game.world.BlockUtils.isReplaceable;
import static com.perfect.client.manager.system.FunctionManager.autoCrystal;


public class Helper extends FunctionHelper<AutoMine, AutoMineConfig> {

	public Helper(AutoMine autoMine, AutoMineConfig config) {
		super(autoMine, config);
	}

	public static boolean isCev(AutoMineType type) {
		return type == AutoMineType.TRAP_CEV || type == AutoMineType.FAR_CEV;
	}

	public List<Entity> getTargets(Box box) {
		if (mc.world == null) {
			return new ArrayList<>();
		}

		return WorldUtils.getEntities(box, this::isGoodTarget);
	}

	private boolean isGoodTarget(Entity entity) {
		if (entity.equals(mc.player)) {
			return false;
		}
		if (!(entity instanceof PlayerEntity player)) {
			return false;
		}
		if (Managers.FRIEND.isFriend(entity)) {
			return false;
		}

		return player.getHealth() > 0;
	}

	public BlockCheckResult isValidPlace(
			AutoMineType type,
			BlockPos minePos,
			BlockPos crystalPos,
			boolean supportEnabled
	) {
		boolean support = canSupport(supportEnabled);

		if (!canMineBlock(minePos, supportEnabled)) {
			return BlockCheckResult.INVALID;
		}
		if (!checkSupportIfNeed(type, minePos, support)) {
			return BlockCheckResult.INVALID;
		}

		boolean canPlaceCrystal = canPlaceCrystal(crystalPos, support);

		if (!canPlaceCrystal && type != AutoMineType.BURROW) {
			return BlockCheckResult.INVALID;
		}

		return new BlockCheckResult(
				!PositionMath.isOutOfRange(minePos.toCenterPos(), config.getRange().get()),
				canPlaceCrystal
		);
	}

	private boolean canPlaceCrystal(BlockPos crystalPos, boolean support) {
		if (autoCrystal.getPlaceHelper().isInvalidBlock(crystalPos, support)) {
			return false;
		}
		return !BlockUtils.hasEntity(crystalPos.up(), Helper::isObstructiveForCrystal);
	}

	private boolean checkSupportIfNeed(AutoMineType type, BlockPos minePos, boolean support) {
		if (!needSupport(type, support)) {
			return true;
		}
		return !BlockUtils.hasEntity(minePos, this::isObstructiveForBlock);
	}

	private boolean needSupport(AutoMineType type, boolean support) {
		if (!support) {
			return false;
		}
		return isCev(type);
	}

	private boolean isObstructiveForBlock(Entity entity) {
		if (entity.intersectionChecked) {
			return true;
		}
		if (entity instanceof EndCrystalEntity) {
			return !config.getBreakObstructive().get();
		}
		return false;
	}

	private static boolean isObstructiveForCrystal(Entity entity) {
		if (entity instanceof EndCrystalEntity) {
			return false;
		}

		if (entity instanceof ItemEntity) {
			return entity.age > 5;
		}
		if (entity instanceof ProjectileEntity) {
			double velocity = EntityUtils.getVelocity(entity).length();
			return !(velocity > 0);
		}
		return true;
	}

	private boolean canMineBlock(BlockPos pos, boolean support) {
		if (!isBreakable(pos)) {
			return false;
		}
		if (isReplaceable(pos)) {
			return support;
		}
		return true;
	}

	private boolean canSupport(boolean supportEnabled) {
		if (!supportEnabled) {
			return false;
		}
		FindItemResult obsidian = InvUtils.find(Items.OBSIDIAN);
		int slot = obsidian.slot();
		Swap swap = config.getSwap().get();
		return swap.isSlotNormal(slot);
	}

	public static void sortByProperty(List<PositionInfo> list, AutoMine.SortProperty property) {
		Utils.keepHigherValues(list, positionInfo -> getProperty(positionInfo, property));
	}

	public static double getProperty(PositionInfo position, AutoMine.SortProperty property) {
		return switch (property) {
			case DAMAGE -> position.getDamage();
			case PRIORITY -> position.getPriority();
		};
	}
}