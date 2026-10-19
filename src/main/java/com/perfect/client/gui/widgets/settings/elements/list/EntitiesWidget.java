package com.perfect.client.gui.widgets.settings.elements.list;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.setting.impl.list.EntitiesSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.IBoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.enums.IEnumListSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.core.utils.game.entity.EntityGroup;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.list.EntityListItemWidget;
import com.perfect.client.gui.widgets.list.common.ListWidget;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.enums.impl.EnumListWidget;
import com.perfect.client.gui.widgets.windows.ListWindowWidget;
import net.minecraft.entity.EntityType;
import org.joml.Matrix3x2fStack;

import java.util.HashSet;
import java.util.Set;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public class EntitiesWidget extends SettingWidget<Set<EntityType<?>>, EntitiesSetting> {

	private final EnumListWidget<EntityGroup> groupWidget;

	private float buttonHeight;
	private float textWidth;

	private String sizeText;

	private boolean buttonHovered;

	public EntitiesWidget(EntitiesSetting setting) {
		super(setting);

		groupWidget = addChild(new EnumListWidget<>(new EntityGroupSetting(setting)));
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		sizeText = Integer.toString(setting.getActiveSize());
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		buttonHeight = 5f;
		textWidth = MONTSERRAT_MEDIUM.getTextWidth(sizeText, 3.5f);

		groupWidget.setWidth(width);

		height = groupWidget.getHeight();
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		ScissorStack.pushScissor(matrices, drawX, drawY, width, height);

		MONTSERRAT_MEDIUM.drawYCentered(
				matrices,
				sizeText,
				3.5f,
				drawX + width - textWidth,
				drawY + buttonHeight / 2f,
				Managers.COLOR.get(Managers.COLOR.MAIN)
		);

		ScissorStack.popScissor(matrices);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (buttonHovered && event.isLeft() && event.isPress()) {
			ListWidget<EntityType<?>> listWidget = new ListWidget<>(setting, EntityListItemWidget::new);
			container.get().addWindow(new ListWindowWidget(listWidget));
		}
	}

	@Override
	protected void onUpdateHover() {
		super.onUpdateHover();
		buttonHovered = isHovered() && UiUtils.isHovered(mouseX, mouseY, drawX, drawY, width, buttonHeight);
	}

	@Override
	public float getBorderedWidth() {
		return textWidth;
	}

	@Override
	public void onDisableHover() {
		super.onDisableHover();
		buttonHovered = false;
	}

	public record EntityGroupSetting(EntitiesSetting setting) implements IEnumListSetting<Set<EntityGroup>, EntityGroup> {

		@Override
		public EntityGroup[] getValues() {
			return setting.getVisibleGroups();
		}

		@Override
		public boolean get(EntityGroup value) {
			return setting.getActiveGroups().contains(value);
		}

		@Override
		public String getName() {
			return setting.getName();
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getFunctionId() {
			return null;
		}

		@Override
		public String getHudWidgetId() {
			return null;
		}

		@Override
		public Set<EntityGroup> get() {
			return setting.getActiveGroups();
		}

		@Override
		public Set<EntityGroup> getDefaultValue() {
			return new HashSet<>();
		}

		@Override
		public boolean set(Set<EntityGroup> value) {
			return false;
		}

		@Override
		public void reset() {}

		@Override
		public IBoolSetting getToggleable() {
			return null;
		}

		@Override
		public boolean isVisible() {
			return true;
		}

		@Override
		public void add(EntityGroup value) {
			setting.addActiveGroup(value);
		}

		@Override
		public void remove(EntityGroup value) {
			setting.removeActiveGroup(value);
		}
	}
}
