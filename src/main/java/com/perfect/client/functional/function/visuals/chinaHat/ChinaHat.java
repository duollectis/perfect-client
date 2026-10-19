package com.perfect.client.functional.function.visuals.chinaHat;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.color.Color4f;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;

import java.awt.*;
import java.util.function.Consumer;

import static com.perfect.client.manager.system.FunctionManager.freecam;
import static com.perfect.client.manager.system.FunctionManager.noRender;


public class ChinaHat extends Function {

	private final static int POINTS = 360;

	private final ChinaHatConfig config = new ChinaHatConfig(this);

	private final Color4f[] fillColors = new Color4f[POINTS + 1];
	private final Color4f[] outlineColors = new Color4f[POINTS + 1];

	private MultiColor prevFillColors, prevOutlineColor;

	public ChinaHat() {
		super("china-hat", "China Hat", Category.VISUALS);
	}

	public boolean shouldDraw(PlayerEntity player) {
		if (player == null || !isEnabled()) {
			return false;
		}

		if (player.equals(mc.player)) {
			// ебаные костыли, я знаю
			return (!mc.options.getPerspective().isFirstPerson() || freecam.isEnabled())
					&& !(mc.currentScreen instanceof InventoryScreen);
		}
		return config.getFriends().get() && Managers.FRIEND.isFriend(player);
	}

	
	public void drawChinaHat(MatrixStack matrices, PlayerEntity player) {
		if (mc.world == null) {
			return;
		}

		checkColorsChanged();

		double offset = isRenderingHelmet(player) ? 0.075 : 0.0;

		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
		matrices.translate(0, 0.43f + offset, 0);

		{
			Render.setShader(ShaderPrograms.POSITION_COLOR);
			VertexBuilder builder = new VertexBuilder(VertexFormats.POSITION_COLOR, VertexDrawMode.LINE_STRIP);

			Renderer3D.preRender();
			Render.enableDepthTest();

			forEachPoint(i -> {
				double rad = i * Math.PI / (POINTS / 2.0);
				float x = (float) (Math.cos(rad) * 0.55);
				float z = (float) (Math.sin(rad) * 0.55);

				Color4f col = outlineColors[i];

				builder.pos(matrices, x, 0.0f, z).color(col.r, col.g, col.b, col.a);
			});

			builder.draw();
			Renderer3D.postRender();
		}

		{
			Renderer3D.preRender();
			Renderer3D.disableSmooth();
			Render.depthMask(true);
			Render.enableDepthTest();

			Render.setShader(ShaderPrograms.POSITION_COLOR);
			VertexBuilder builder = new VertexBuilder(VertexFormats.POSITION_COLOR, VertexDrawMode.TRIANGLE_FAN);

			Color4f startCol = fillColors[0];

			builder.pos(matrices, 0.0f, 0.3f, 0.0f).color(startCol);

			// Окружность
			forEachPoint(i -> {
				double rad = i * Math.PI / (POINTS / 2.0);
				float x = (float) (Math.cos(rad) * 0.55);
				float z = (float) (Math.sin(rad) * 0.55);

				Color4f col = fillColors[i];

				builder.pos(matrices, x, 0.0f, z).color(col.r, col.g, col.b, col.a);
			});

			builder.draw();
			Renderer3D.postRender();
		}

		matrices.pop();
	}

	private boolean isRenderingHelmet(PlayerEntity player) {
		if (noRender.contains(Elements.ARMOR)) {
			return false;
		}
		return !player.getEquippedStack(EquipmentSlot.HEAD).isEmpty();
	}

	private void checkColorsChanged() {
		MultiColor fill = config.getFillColor().get();
		MultiColor outline = config.getOutlineColor().get();

		if (!fill.equals(prevFillColors)) {
			updateColors(fill, fillColors);
		}
		if (!outline.equals(prevOutlineColor)) {
			updateColors(outline, outlineColors);
		}

		prevFillColors = new MultiColor(fill);
		prevOutlineColor = new MultiColor(outline);
	}

	private void updateColors(MultiColor color, Color4f[] array) {
		float step = 1.0f / (POINTS + 1);

		forEachPoint(i -> {
			Color col = color.getOffsetColor(step * i);
			array[i] = new Color4f(col);
		});
	}

	private void forEachPoint(Consumer<Integer> pointConsumer) {
		for (int i = 0; i <= POINTS; i++) {
			pointConsumer.accept(i);
		}
	}
}
