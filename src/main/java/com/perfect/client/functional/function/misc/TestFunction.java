package com.perfect.client.functional.function.misc;

import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumOrderSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.tensorflow.SavedModelBundle;

import java.awt.*;

@Slf4j
public class TestFunction extends Function {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgAnimation = settings.createGroup();

	public final BindSetting bindStartFallFlying = sgGeneral.add(new BindSetting
			.Builder()
			.id("bind-fall-flying")
			.defaultValue(Bind.NONE)
			.action(() -> sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING)))
			.build());

	public final BindSetting bindStartGliding = sgGeneral.add(new BindSetting
			.Builder()
			.id("bind-start-gliding")
			.defaultValue(Bind.NONE)
			.action(() -> mc.player.startGliding())
			.build());

	public final BoolSetting needReset = sgGeneral.add(new BoolSetting
			.Builder()
			.id("need-optimize")
			.defaultValue(false)
			.onChanged(value -> {
				ChatUtils.error(0, "Current: " + value);
				LoggerUtils.warning("Current: " + value);
			})
			.build());

	public final BoolSetting disableScissor = sgGeneral.add(new BoolSetting
			.Builder()
			.id("disable-scissor")
			.defaultValue(false)
			.build());

	public final BoolSetting devUtil = sgGeneral.add(new BoolSetting
			.Builder()
			.id("dev-util")
			.defaultValue(false)
			.build());

	public final IntSetting time = sgGeneral.add(new IntSetting
			.Builder()
			.id("time")
			.defaultValue(200)
			.build());

	public final IntSetting time1 = sgGeneral.add(new IntSetting
			.Builder()
			.id("time-one")
			.defaultValue(200)
			.build());

	public final IntSetting time2 = sgGeneral.add(new IntSetting
			.Builder()
			.id("time-two")
			.defaultValue(200)
			.build());

	public final IntSetting i1 = sgGeneral.add(new IntSetting
			.Builder()
			.id("i-one")
			.defaultValue(0)
			.build());

	public final IntSetting i2 = sgGeneral.add(new IntSetting
			.Builder()
			.id("i-two")
			.defaultValue(2)
			.build());

	public final DoubleSetting height = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("height")
			.defaultValue(5)
			.build());

	public final DoubleSetting xDepth = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("x-depth")
			.defaultValue(5)
			.build());

	public final DoubleSetting rounding = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("rounding")
			.defaultValue(2)
			.build());

	public final DoubleSetting alpha = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("alpha")
			.range(0, 1)
			.defaultValue(0.9)
			.build());

	public final DoubleSetting text = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("text")
			.defaultValue(4f)
			.build());

	public final DoubleSetting posAnim = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("pos-anim")
			.range(0, 1)
			.defaultValue(0.9)
			.build());

	// Animation

	public final IntSetting msaa = sgAnimation.add(new IntSetting.Builder()
			.id("msaa")
			.defaultValue(1)
			.sliderRange(0, 16)
			.range(0, 16)
			.build());

	public final EnumOrderSetting<Rotate> order = sgAnimation.add(new EnumOrderSetting
			.Builder<>(Rotate.values())
			.id("order")
			.build());

	public final ColorSetting color = sgGeneral.add(new ColorSetting
			.Builder()
			.id("color")
			.defaultValue(new Color(20, 20, 20, 255))
			.toggleable(true)
			.build());

	private SavedModelBundle model;
	public static boolean needCancel;

	public TestFunction() {
		super("test-function", "Test Function", Category.MISC);
	}

	@Override
	public void onEnable() {
		if (mc.player == null || mc.world == null) {
			return;
		}
	}
}
