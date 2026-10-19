package com.perfect.client.gui.widgets.list;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.gui.widgets.list.common.ListItem;
import com.perfect.client.gui.widgets.list.common.ListItemWidget;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;

public class ItemStackListItemWidget extends ListItemWidget {

	private final ItemStack stack;

	public ItemStackListItemWidget(Block block, ListItem listItem) {
		this(block.asItem(), listItem);
	}

	public ItemStackListItemWidget(Item item, ListItem listItem) {
		this(item.getDefaultStack(), listItem);
	}

	public ItemStackListItemWidget(ItemIdentifier itemIdentifier, ListItem listItem) {
		this(itemIdentifier.getDefaultStack(), listItem);
	}

	public ItemStackListItemWidget(ItemStack stack, ListItem listItem) {
		super(listItem);
		this.stack = stack;
	}

	@Override
	protected void drawContent(Matrix3x2fStack matrices) {
		Renderer2D.drawItem(
				matrices,
				stack,
				drawX + width / 2.0f,
				drawY + height / 2.0f,
				6.8f
		);
	}
}
