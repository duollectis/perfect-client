package com.perfect.client.core.functional.setting.bundles;

import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.costemics.AnimatedCosmetics;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.render.animation.easing.EasingMode;

public class AnimatedCosmeticsBundle extends CosmeticsBundle {

	private final SettingGroup sgAnimation = settings.createGroup();
	private final SettingGroup sgAnimationSettings = settings.createGroup();

	private final BoolSetting opacityAnimation = sgAnimation.add(new BoolSetting
			.Builder()
			.id("opacity-animation")
			.defaultValue(true)
			.build());

	private final BoolSetting scaleAnimation = sgAnimation.add(new BoolSetting
			.Builder()
			.id("scale-animation")
			.defaultValue(true)
			.build());

	private final EnumSetting<EasingMode> easing = sgAnimationSettings.add(new EnumSetting
			.Builder<>(EasingMode.values())
			.id("animation")
			.visible(this::isAnimationEnabled)
			.defaultValue(EasingMode.CUBIC)
			.build());

	private final DoubleSetting animationTime = sgAnimationSettings.add(new DoubleSetting
			.Builder()
			.id("animation-time")
			.defaultValue(0.8)
			.range(0.1, 5)
			.visible(this::isAnimationEnabled)
			.build());

	public boolean isAnimationEnabled() {
		return opacityAnimation.get() || scaleAnimation.get();
	}

	public AnimatedCosmetics bundle() {
		return new AnimatedCosmetics(
				super.bundle(),
				opacityAnimation.get(),
				scaleAnimation.get(),
				easing.get().impl(),
				animationTime.fromSeconds()
		);
	}
}
