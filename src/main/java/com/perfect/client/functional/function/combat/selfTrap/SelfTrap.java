package com.perfect.client.functional.function.combat.selfTrap;

import com.perfect.client.core.event.events.entity.FinishUsingItemEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunction;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.core.utils.game.world.BlockUtils.getEntities;
import static com.perfect.client.core.utils.game.world.BlockUtils.isReplaceable;


public class SelfTrap extends PlaceFunction {

	private final SelfTrapConfig config = new SelfTrapConfig(this);

	private final List<BlockPos> cevPositions = new ArrayList<>();
	private List<BlockPos> trap;

	public SelfTrap() {
		super("self-trap", "Self Trap", Category.COMBAT, true, true);
	}

	@Override
	public void onFinishUsingItem(FinishUsingItemEvent event) {
		if (config.getToggleOnChorus().get() && event.getStack().getItem() == Items.CHORUS_FRUIT) {
			disable();
		}
	}

	@Override
	protected List<BlockPos> getTrap() {
		return TrapUtils.getTrap(
				mc.player,
				config.getMode().get().getType(),
				BedBlock.class,
				TntBlock.class,
				AbstractTorchBlock.class,
				SlimeBlock.class,
				HoneyBlock.class
		);
	}

	
	@Override
	protected EndCrystalEntity getCrystal(List<BlockPos> positions) {
		for (BlockPos pos : positions) {
			if (!isReplaceable(pos))
				continue;

			List<Entity> entities = getEntities(pos, entity -> entity instanceof EndCrystalEntity);

			if (!entities.isEmpty()) {
				return (EndCrystalEntity) entities.getFirst();
			}
		}

		if (trap == null || !config.getAntiCev().get()) {
			return null;
		}

		for (BlockPos pos : trap) {
			List<Entity> entities = getEntities(pos.up(), entity -> isCevCrystal(entity, pos));

			if (!entities.isEmpty()) {
				return (EndCrystalEntity) entities.getFirst();
			}
		}

		return null;
	}

	
	private boolean isCevCrystal(Entity entity, BlockPos trapPos) {
		if (!(entity instanceof EndCrystalEntity)) {
			return false;
		}
		BlockPos crystalPos = entity.getBlockPos();
		return crystalPos.equals(trapPos.up()) || crystalPos.equals(trapPos);
	}

	
	@Override
	protected PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal) {
		if (config.getAntiCev().get()) {
			for (BlockPos pos : trap) {
				BlockPos protectPos = pos.up();
				if (crystal.getBlockPos().equals(protectPos)) {
					BlockHitResult hitResult = HitResultUtils.getPlaceResult(protectPos);

					if (hitResult == null) {
						continue;
					}
					return new PlaceInfo(pos, hitResult);
				}
			}
		}

		for (PlaceInfo placeInfo : new ArrayList<>(places)) {
			if (new Box(placeInfo.getPosition()).intersects(crystal.getBoundingBox()))
				return placeInfo;
		}
		return null;
	}

	@Override
	protected PlaceFunctionConfig<SelfTrap> getConfig() {
		return config;
	}

	
	@Override
	protected void trapHook(List<BlockPos> positions) {
		trap = new ArrayList<>(positions);

		cevPositions.removeIf(cevPos -> !trap.contains(cevPos.down()));

		if (config.getAntiCev().get()) {
			for (BlockPos pos : trap) {
				BlockPos cevPos = pos.up();

				if (cevPositions.contains(cevPos)) {
					positions.add(cevPos);
				}
				else if (hasCrystal(cevPos)) {
					positions.add(cevPos);
					cevPositions.add(cevPos);
				}
			}
		}
	}

	private boolean hasCrystal(BlockPos pos) {
		List<Entity> crystals = getCrystals(pos);
		return !crystals.isEmpty();
	}

	private List<Entity> getCrystals(BlockPos pos) {
		return getEntities(pos, entity -> isCrystal(entity, pos));
	}

	private boolean isCrystal(Entity entity, BlockPos pos) {
		if (!(entity instanceof EndCrystalEntity)) {
			return false;
		}
		return entity.getBlockPos().equals(pos);
	}
}
