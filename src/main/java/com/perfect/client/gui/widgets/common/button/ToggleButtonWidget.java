package com.perfect.client.gui.widgets.common.button;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.utils.Coloring;
import lombok.Setter;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ToggleButtonWidget extends ButtonWidget {

	@Setter
	private boolean buttonActive;

	private final Animation activeAnimation = new Animation(Easing.IN_OUT_SINE, 300, false).finish();

	private float activeAnim;

	private final Consumer<Boolean> toggleConsumer;

	@Setter
	private Function<Float, Color> outlineColorFunction = activeAnim -> MathUtils.interpolateColor(
			Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE),
			Managers.COLOR.get(Managers.COLOR.MAIN),
			activeAnim
	);

	@Setter
	private Function<Float, Coloring> backgroundColorFunction = activeAnim -> Coloring.solid(MathUtils.interpolateColor(
			Managers.COLOR.get(Managers.COLOR.FUNCTION_BACKGROUND),
			Managers.COLOR.get(Managers.COLOR.MAIN),
			Math.max(activeAnim, hoveredAnim)
	));

	public ToggleButtonWidget(Consumer<Boolean> toggleConsumer) {
		super(null);

		setAction(this::onClick);

		this.toggleConsumer = toggleConsumer;

		setOutlineColorSupplier(() -> outlineColorFunction.apply(activeAnim));
		setBackgroundColorSupplier(() -> backgroundColorFunction.apply(activeAnim));

		setOutlineSize(0.6f);

		setAlphaSupplier(() -> activeAnim * 0.15f + hoveredAnim * 0.18f);
	}

	public ToggleButtonWidget active() {
		buttonActive = true;
		return this;
	}

	@Override
	public void onOpen() {
		activeAnim = -1f;
	}

	@Override
	public void onAnimationTick() {
		super.onAnimationTick();
		activeAnim = activeAnimation.changeDirectionAndGet(buttonActive);
	}

	private void onClick() {
		buttonActive = !buttonActive;

		if (toggleConsumer != null) {
			toggleConsumer.accept(buttonActive);
		}
	}
}
