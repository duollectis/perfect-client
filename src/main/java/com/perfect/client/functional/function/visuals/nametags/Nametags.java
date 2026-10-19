package com.perfect.client.functional.function.visuals.nametags;

import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.visuals.nametags.utils.ColoredString;
import com.perfect.client.functional.function.visuals.nametags.utils.NametagsUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Nametags extends Function {

	private static final float COMPENSATE_FACTOR = 12.5f;

	private final static float ROUNDING = 2.2f;
	private final static float X_OFFSET = 1.5f;
	private final static float Y_OFFSET = 0.2f;
	private final static float HEIGHT = 7.6f;
	private final static float ITEM_INDENT = 6.5f;

	private final static float EQUIPMENT_Y_OFFSET = 7.5f;
	private final static float EQUIPMENT_SCALE = 0.4f;

	private final NametagsConfig config = new NametagsConfig(this);

	private final Vector3d pos = new Vector3d();
	private final List<Entity> entityList = new ArrayList<>();

	public Nametags() {
		super("nametags", "Nametags", Category.VISUALS);
	}

	@Override
	public void onPostTick() {
		if (mc.world == null) {
			return;
		}

		entityList.clear();

		Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();

		for (Entity entity : mc.world.getEntities()) {
			if (!shouldDraw(entity.getType())) {
				continue;
			}
			if (RenderUtils.shouldSkipEntityRender(entity)) {
				continue;
			}
			entityList.add(entity);
		}

		entityList.sort(Comparator.comparing(entity -> -entity.squaredDistanceTo(cameraPos)));
	}

	public boolean shouldDraw(EntityType<?> type) {
		return config.getEntities().contains(type);
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		for (Entity entity : entityList) {
			Utils.set(pos, entity, RenderUtils.getTickDelta());
			pos.add(0.0, getHeight(entity), 0.0);

			renderNameTag(event.getContext(), entity);
		}
	}

	
	private double getHeight(Entity entity) {
		double height = entity.getEyeHeight(entity.getPose());

		if (entity.getType() == EntityType.ITEM || entity.getType() == EntityType.ITEM_FRAME) {
			height += 0.2;
		}
		else {
			height += 0.5;
		}

		return height;
	}

	
	private void renderNameTag(DrawContext context, Entity entity) {
		if (mc.world == null || mc.player == null || mc.options.hudHidden || entity == null) {
			return;
		}

		Vec3d entityPos = RenderUtils.getEntityPos(entity);
		float scale = getScale(entity);

		Vec3d vector = entityPos.add(0.0, entity.getEyeHeight(entity.getPose()) + 0.4, 0.0);
		Vector4d position = null;

		vector = Renderer3D.worldSpaceToScreenSpace(vector);

		if (vector.z > 0.0 && vector.z < 1.0) {
			position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
			position.x = Math.min(vector.x, position.x);
			position.y = Math.min(vector.y, position.y);
			position.z = Math.max(vector.x, position.z);
		}

		ColoredString nameTagText = getNameTagText(entity);

		if (position == null) {
			return;
		}

		Matrix3x2fStack matrices = context.getMatrices();

		double posX = position.x;
		double posY = position.y;
		double endPosX = position.z;

		float diff = (float) (endPosX - posX) / 2.0f;
		float textWidth = nameTagText.getWidth();

		float tagX = (float) (posX + diff - textWidth / 2.0f);
		float tagY = (float) posY - HEIGHT - Y_OFFSET;

		float centerX = tagX + textWidth / 2f;

		matrices.pushMatrix();
		matrices.translate(centerX, (float) posY);
		matrices.scale(scale, scale);
		matrices.translate(-centerX, (float) -posY);

		if (config.getArmor().get() && entity instanceof PlayerEntity player) {
			List<ItemStack> equipment = getEquipment(player);

			float equipmentX = centerX - ITEM_INDENT * equipment.size() / 2.0f;
			float equipmentY = tagY - EQUIPMENT_Y_OFFSET;

			float itemOffset = 0.0f;

			for (ItemStack itemStack : equipment) {
				matrices.pushMatrix();
				matrices.translate(equipmentX + itemOffset, equipmentY);
				matrices.scale(EQUIPMENT_SCALE, EQUIPMENT_SCALE);

				context.drawItem(itemStack, 0, 0);
				context.drawStackOverlay(mc.textRenderer, itemStack, 0, 0);

				matrices.popMatrix();

				itemOffset += ITEM_INDENT;
			}
		}

		float boxX = tagX - X_OFFSET;
		float boxWidth = textWidth + X_OFFSET * 2.0f;

		if (config.getSimpleRect().get()) {
			Renderer2D.drawRect(matrices, boxX, tagY, boxWidth, HEIGHT, Managers.COLOR.get(Managers.COLOR.HUD_BACKGROUND));
		} else {
			Renderer2D.round()
			          .pos(boxX, tagY, boxWidth, HEIGHT)
			          .rounding(ROUNDING)
			          .thickness(0.6f)
			          .alpha(0.9f)
			          .outlineColor(Managers.COLOR.get(Managers.COLOR.HUD_OUTLINE))
			          .color(Managers.COLOR.get(Managers.COLOR.HUD_BACKGROUND))
			          .draw(matrices);
		}

		nameTagText.draw(matrices, boxX, tagY, boxWidth, HEIGHT);

		matrices.popMatrix();
	}

	
	private float getScale(Entity entity) {
		if (!config.getDistanceScale().get()) {
			return config.getScale().getFloat();
		}

		Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();

		Box entityBox = EntityUtils.getBoxAt(entity, RenderUtils.getEntityPos(entity));
		Vec3d entityPos = PositionMath.closestVec3d(cameraPos, entityBox);

		float distance = (float) PositionMath.sqrtDistance(cameraPos, entityPos);

		float scale = (config.getScale().getFloat() * COMPENSATE_FACTOR) / Math.max(0.1f, distance);

		return Math.max(scale, config.getMinScale().getFloat());
	}

	
	private ColoredString getNameTagText(Entity entity) {
		ColoredString nameTagText = new ColoredString();

		if (entity instanceof PlayerEntity player && config.getPing().get()) {
			nameTagText.add(EntityUtils.getPing(player) + "ms  ", Managers.COLOR.get(Managers.COLOR.MAIN));
		}

		NametagsUtils.addFormattedDisplayName(entity, nameTagText);

		if (entity instanceof LivingEntity living && config.getHealth().get()) {
			nameTagText.add(
					" " + Math.round((living.getHealth() + living.getAbsorptionAmount()) * 10.0f) / 10.0f,
					Managers.COLOR.get(Managers.COLOR.MAIN)
			);
		}

		if (config.getTotemPops().get()) {
			int pops = Managers.ENTITY.getPops(entity);

			if (pops > 0) {
				nameTagText.add("  -" + pops, Managers.COLOR.get(Managers.COLOR.MAIN));
			}
		}

		return nameTagText;
	}

	
	private List<ItemStack> getEquipment(PlayerEntity player) {
		ArrayList<ItemStack> stacks = new ArrayList<>();

		addIfNotEmpty(stacks, player.getOffHandStack());
		addIfNotEmpty(stacks, player.getEquippedStack(EquipmentSlot.HEAD));
		addIfNotEmpty(stacks, player.getEquippedStack(EquipmentSlot.CHEST));
		addIfNotEmpty(stacks, player.getEquippedStack(EquipmentSlot.LEGS));
		addIfNotEmpty(stacks, player.getEquippedStack(EquipmentSlot.FEET));
		addIfNotEmpty(stacks, player.getMainHandStack());

		return stacks;
	}

	
	private void addIfNotEmpty(List<ItemStack> list, ItemStack stack) {
		Utils.addIf(list, stack, itemStack -> !itemStack.isEmpty());
	}
}