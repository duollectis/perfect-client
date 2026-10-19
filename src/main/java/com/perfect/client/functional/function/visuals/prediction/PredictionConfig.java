package com.perfect.client.functional.function.visuals.prediction;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import lombok.Getter;

@Getter
public class PredictionConfig extends FunctionConfig<Prediction> {

	private final SettingGroup sgTrajectory = settings.createGroup();

	private final BoolSetting trajectory = sgTrajectory.add(new BoolSetting
			.Builder()
			.id("projectile-trajectory")
			.defaultValue(true)
			.build());

	private final BoolSetting pearls = sgTrajectory.add(new BoolSetting
			.Builder()
			.id("pearls")
			.defaultValue(true)
			.visible(trajectory::get)
			.build());

	private final BoolSetting arrows = sgTrajectory.add(new BoolSetting
			.Builder()
			.id("arrows")
			.defaultValue(true)
			.visible(trajectory::get)
			.build());

	private final BoolSetting tridents = sgTrajectory.add(new BoolSetting
			.Builder()
			.id("tridents")
			.defaultValue(true)
			.visible(trajectory::get)
			.build());

	private final BoolSetting xp = sgTrajectory.add(new BoolSetting
			.Builder()
			.id("xp-bottles")
			.defaultValue(false)
			.visible(trajectory::get)
			.build());

	private final BoolSetting potions = sgTrajectory.add(new BoolSetting
			.Builder()
			.id("potions")
			.defaultValue(false)
			.visible(trajectory::get)
			.build());

	private final MultiColorSetting trajectoryColor = sgTrajectory.add(new MultiColorSetting
			.Builder()
			.id("trajectory-color")
			.visible(trajectory::get)
			.defaultValue(MultiColor.client())
			.build());

	public PredictionConfig(Prediction function) {
		super(function);
	}
}
