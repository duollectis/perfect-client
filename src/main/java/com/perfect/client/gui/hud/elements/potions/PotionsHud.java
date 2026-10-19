package com.perfect.client.gui.hud.elements.potions;

import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.hud.base.HudWidget;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.util.StringHelper;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.perfect.client.PerfectClient.hudSettings;
import static com.perfect.client.PerfectClient.mc;

public class PotionsHud extends HudWidget {

	private static final float PADDING = 0.9f;

	private static final PotionInfo[] PLACEHOLDER_POTIONS = {
			new PotionInfo("Strength 2", "1:20 ", Color.RED),
			new PotionInfo("Speed 2", "3:55 ", Color.BLUE),
			new PotionInfo("Absorption 4", "0:10 ", Color.ORANGE),
			new PotionInfo("Saturation", "1:00 ", Color.MAGENTA)
	};

	private final Map<StatusEffect, String> cachedEffectNames = new Reference2ObjectOpenHashMap<>(16);
	private final List<PotionEntry> entries = new ArrayList<>();

	private final PotionsHudConfig config = new PotionsHudConfig(this);

	private float lineHeight;
	private boolean rightAligned;

	public PotionsHud() {
		super("potions-hud", "Potions", 0.0f, 0.34f);
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		entries.clear();

		rightAligned = scaleCenterX.get() > DrawConstants.getScreenWidth() / 2.0f;
		boolean bottomAligned = scaleCenterY.get() > DrawConstants.getScreenHeight() / 2.0f;

		if (mc.player != null && mc.world != null && !mc.player.getStatusEffects().isEmpty()) {
			for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
				entries.add(createEntry(effect));
			}
		}
		else if (isHudEditorScreen()) {
			for (PotionInfo info : PLACEHOLDER_POTIONS) {
				entries.add(new PotionEntry(
						info,
						font.getTextWidth(info.text())
				));
			}
		}

		entries.sort(Comparator.comparingDouble(PotionEntry::width));

		if (!bottomAligned) {
			Collections.reverse(entries);
		}
	}

	private PotionEntry createEntry(StatusEffectInstance effect) {
		assert mc.world != null;

		String name = cachedEffectNames.computeIfAbsent(
				effect.getEffectType().value(),
				effect_ -> StringHelper.stripTextFormat(I18n.translate(effect_.getTranslationKey()))
		);
		String duration = StatusEffectUtil.getDurationText(
				effect,
				1,
				mc.world.getTickManager().getTickRate()
		).getString();

		String text = String.format("%s %d", name, effect.getAmplifier() + 1);

		PotionInfo info = new PotionInfo(text, duration, new Color(effect.getEffectType().value().getColor()));

		return new PotionEntry(info, font.getTextWidth(text + " " + duration));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		lineHeight = font.getFontHeight() + PADDING * 2.0f;

		width = (float) entries.stream().mapToDouble(PotionEntry::width).max().orElse(100.0) + EXPAND * 2.0f;
		height = entries.size() * lineHeight;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		super.onDraw(matrices);

		if (entries.isEmpty()) {
			return;
		}

		float y = drawY;

		float spread = hudSettings.getColorSpread().getFloat() / 1000f;

		for (int i = 0; i < entries.size(); i++) {
			PotionEntry entry = entries.get(i);
			float x = drawX;

			if (rightAligned) {
				x += width - entry.width() - EXPAND * 2.0f;
			}

			drawRect(
					matrices,
					x,
					y,
					entry.width() + EXPAND * 2.0f,
					lineHeight,
					getBackgroundColor(entry)
			);

			float width = font.getTextWidth(entry.info().effectName() + " ");
			float baseOffset = i * 0.1f;

			font.drawYCentered(
					matrices,
					entry.info().effectName(),
					x + EXPAND,
					y + lineHeight / 2.0f,
					getTextColor(entry),
					baseOffset,
					spread
			);

			font.drawYCentered(
					matrices,
					entry.info().effectDuration(),
					x + width + EXPAND,
					y + lineHeight / 2.0f,
					hudSettings.getValueTextColor().get()
			);

			y += lineHeight;
		}
	}

	public Color getBackgroundColor(PotionEntry entry) {
		Color bgColor;

		if (config.getBackgroundColorMode().get(ColorMode.POTIONS)) {
			bgColor = entry.info().color().darker();
		} else {
			bgColor = hudSettings.getBackgroundColor().get();
		}

		return ColorUtils.applyAlpha(bgColor, hudSettings.getBackgroundColor().get().getAlpha());
	}

	public MultiColor getTextColor(PotionEntry entry) {
		MultiColor textColor;

		if (config.getTextColorMode().get(ColorMode.POTIONS)) {
			textColor = new MultiColor(entry.info().color().brighter());
		} else {
			textColor = hudSettings.getTextColor().get();
		}

		textColor.setAlpha(hudSettings.getTextColor().get().getAlpha());

		return textColor;
	}

	public enum ColorMode {
		CLIENT,
		POTIONS
	}

	public record PotionEntry(
			PotionInfo info,
			float width
	) {}

	public record PotionInfo(
			String effectName,
			String effectDuration,
			Color color
	) {
		public String text() {
			return effectName() + effectDuration();
		}
	}
}