package com.perfect.client.functional.function.misc.autoWither;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.functional.function.misc.autoWither.data.Block;
import com.perfect.client.functional.function.misc.autoWither.data.PlaceInfo;
import com.perfect.client.functional.function.misc.autoWither.data.WitherInfo;
import com.perfect.client.functional.function.misc.autoWither.helpers.PositionHelper;
import net.minecraft.util.math.Box;

import java.awt.*;

public class AutoWither extends Function {

	private final AutoWitherConfig config = new AutoWitherConfig(this);

	private final PositionHelper positionHelper = new PositionHelper(this, config);

	private WitherInfo witherInfo;

	public AutoWither() {
		super("auto-wither", "Auto Wither", Category.MISC);
	}

	@Override
	public void onRotate() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		witherInfo = positionHelper.getWitherInfo();
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (witherInfo != null) {
			for (PlaceInfo placeInfo : witherInfo.getBlocks()) {

				if (placeInfo.getBlock() == Block.AIR) {
					continue;
				}

				BoxRenderer.drawBox(
						event.getMatrices(),
						new Box(placeInfo.getPos()),
						new MultiColor(Color.WHITE),
						new MultiColor(new Color(255, 255, 255, 0)),
						Cosmetics.createEmpty()
				);
			}
		}
	}
}
