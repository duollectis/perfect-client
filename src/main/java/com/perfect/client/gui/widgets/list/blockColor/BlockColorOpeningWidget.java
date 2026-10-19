package com.perfect.client.gui.widgets.list.blockColor;

import com.perfect.client.core.functional.setting.impl.list.BlockColorSetting;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import com.perfect.client.gui.widgets.list.ItemStackListItemWidget;
import com.perfect.client.gui.widgets.list.common.ListWidget;
import net.minecraft.block.Block;
import org.joml.Matrix3x2fStack;

public class BlockColorOpeningWidget extends HorizontalListWidget {

	private final BlockColorPickerWidget picker;

	public BlockColorOpeningWidget(BlockColorSetting setting) {
		addChild(new ListWidget<>(setting, ItemStackListItemWidget::new, this::pickBlock));

		picker = addChild(new BlockColorPickerWidget(setting));
	}

	private void pickBlock(Block block) {
		picker.setPickedBlock(block);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(2.5f);
		picker.setWidth(70.0f);

		super.onSizeUpdate(matrices);

		picker.setHeight(height);
	}
}
