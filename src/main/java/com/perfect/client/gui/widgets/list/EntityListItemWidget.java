package com.perfect.client.gui.widgets.list;

import com.perfect.client.gui.widgets.list.common.ListItem;
import com.perfect.client.gui.widgets.list.common.ListItemWidget;
import net.minecraft.entity.EntityType;
import org.joml.Matrix3x2fStack;

public class EntityListItemWidget extends ListItemWidget {

	private final EntityType<?> entityType;

	public EntityListItemWidget(EntityType<?> entityType, ListItem item) {
		super(item);
		this.entityType = entityType;
	}

	@Override
	protected void drawContent(Matrix3x2fStack matrices) {

	}
}
