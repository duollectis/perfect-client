package com.perfect.client.gui.widgets.settings.elements.list;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.impl.list.BlockColorSetting;
import com.perfect.client.core.utils.BlockColor;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.list.blockColor.BlockColorOpeningWidget;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.windows.ListWindowWidget;
import net.minecraft.block.Block;
import org.joml.Matrix3x2fStack;

import java.util.Map;

public class BlockColorWidget extends SettingWidget<Map<Block, BlockColor>, BlockColorSetting> {

	private static final float HEIGHT = 4.4f;

	public BlockColorWidget(BlockColorSetting setting) {
		super(setting);
	}

	@Override
	public float getBorderedWidth() {
		return 0.0f;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		height = HEIGHT;
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY)) {
			openWindow();
		}
	}

	private void openWindow() {
		Widget widget = new BlockColorOpeningWidget(setting);
		container.get().addWindow(new ListWindowWidget(widget));
	}
}
