package com.perfect.client.functional.function.misc.testSounds;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.CosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;

public class TestSounds extends Function {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final IntSetting testId = sgGeneral.add(new IntSetting
			.Builder()
			.id("test-id")
			.defaultValue(1)
			.sliderRange(1, 6)
			.build());

	private final BoolSetting anim = sgGeneral.add(new BoolSetting
			.Builder()
			.id("anim")
			.defaultValue(false)
			.build());

	private final IntSetting scanRange = sgGeneral.add(new IntSetting
			.Builder()
			.id("scan-range")
			.defaultValue(6)
			.sliderRange(1, 10)
			.suffix(Suffix.BLOCKS)
			.build());

	private final IntSetting lifeTime = sgGeneral.add(new IntSetting
			.Builder()
			.id("life-time")
			.defaultValue(50)
			.sliderRange(50, 500)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final BundleSetting<CosmeticsBundle> cosmetics = sgGeneral.add(new BundleSetting
			.Builder<CosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new CosmeticsBundle())
			.build());

	private final MultiColorSetting lineColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.build());

	private final MultiColorSetting sideColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.build());

	public TestSounds() {
		super("test-sounds", "Test Sounds", Category.MISC);
	}
}
