package com.perfect.client.functional.function.visuals.targetEsp;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.combat.anchorAura.AnchorAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.visuals.targetEsp.modes.BoxMode;
import com.perfect.client.functional.function.visuals.targetEsp.modes.CircleMode;
import com.perfect.client.functional.function.visuals.targetEsp.modes.GhostsMode;
import com.perfect.client.functional.function.visuals.targetEsp.utils.RenderHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class TargetESP extends Function {

	private final TargetEspConfig config = new TargetEspConfig(this);

	@Getter
	private final ModeContainer<Mode, RenderHandler> modeContainer = createModeContainer(config::getMode)
			.add(Mode.BOX, new BoxMode(this, config))
			.add(Mode.CIRCLE, new CircleMode(this, config))
			.add(Mode.GHOSTS, new GhostsMode(this, config));

	private final FunctionTarget DISABLED_TARGET = new FunctionTarget(() -> false, -1);

	private final Map<Class<? extends Function>, FunctionTarget> targetMap = new HashMap<>() {{
		put(AutoCrystal.class, new FunctionTarget(() -> config.getAutoCrystal().get(), 100));
		put(AnchorAura.class, new FunctionTarget(() -> config.getAnchorAura().get(), 150));
		put(AttackAura.class, new FunctionTarget(() -> config.getAttackAura().get(), 200));
	}};

	private LivingEntity prevTarget;

	public TargetESP() {
		super("target-esp", "Target ESP", Category.VISUALS);
	}

	@Override
	public void onDisable() {
		prevTarget = null;
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		LivingEntity target = getTarget();

		if (target == null) {
			modeContainer.getCurrentMode().setVisible(false);

			if (prevTarget != null) {
				modeContainer.getCurrentMode().render(event.getMatrices(), prevTarget);

				prevTarget.lastX = prevTarget.getX();
				prevTarget.lastY = prevTarget.getY();
				prevTarget.lastZ = prevTarget.getZ();
			}
		}
		else {
			if (target != prevTarget) {
				modeContainer.getCurrentMode().reset();
			}

			modeContainer.getCurrentMode().setVisible(true);
			modeContainer.getCurrentMode().render(event.getMatrices(), target);

			prevTarget = target;
		}
	}

	private LivingEntity getTarget() {
		return Managers.TARGET.getTarget(this::getTargetPriority);
	}

	private int getTargetPriority(Class<? extends Function> function) {
		FunctionTarget functionTarget = targetMap.getOrDefault(function, DISABLED_TARGET);

		if (functionTarget.getEnabledSupplier().get()) {
			return functionTarget.getPriority();
		}
		else {
			return -1;
		}
	}

	public enum Mode {
		BOX,
		CIRCLE,
		GHOSTS
	}

	@Getter
	@AllArgsConstructor
	public static class FunctionTarget {

		private final Supplier<Boolean> enabledSupplier;
		private final int priority;
	}
}
