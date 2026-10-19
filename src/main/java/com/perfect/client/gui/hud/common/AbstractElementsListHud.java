package com.perfect.client.gui.hud.common;

import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.hud.base.HudWidget;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.perfect.client.PerfectClient.hudSettings;

public abstract class AbstractElementsListHud extends HudWidget {
	private static final float BRACKETS_OFFSET = 0.3f;
	protected final List<ElementEntry> entries = new ArrayList<>();
	private final boolean needSort;

	private boolean rightAligned;

	private float cachedBracketSpaceWidth = -1;
	private float cachedEmptyBracketsWidth = -1;

	public AbstractElementsListHud(String id, String name, float defaultX, float defaultY, boolean needSort) {
		super(id, name, defaultX, defaultY);
		this.needSort = needSort;
	}

	protected abstract void updateElements();

	@Override
	public void onPreTick() {
		super.onPreTick();

		if (cachedEmptyBracketsWidth == -1) {
			cachedEmptyBracketsWidth = font.getTextWidth("[ ]");
			cachedBracketSpaceWidth = font.getTextWidth(" [");
		}

		rightAligned = scaleCenterX.get() > DrawConstants.getScreenWidth() / 2.0f;
		boolean bottomAligned = scaleCenterY.get() > DrawConstants.getScreenHeight() / 2.0f;

		updateElements();

		if (needSort) {
			entries.sort(Comparator.comparingDouble(ElementEntry::totalWidth));
		}
		if (!bottomAligned) {
			Collections.reverse(entries);
		}
	}

	protected ElementEntry createEntry(String prefix, String suffix) {
		return new ElementEntry(
				prefix,
				suffix,
				font.getTextWidth(prefix),
				font.getTextWidth(suffix),
				cachedEmptyBracketsWidth
		);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		width = (float) entries.stream().mapToDouble(ElementEntry::totalWidth).max().orElse(100.0) + EXPAND * 2.0f;
		height = entries.size() * font.getFontHeight();
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		if (entries.isEmpty()) {
			return;
		}

		float y = drawY;
		float spread = hudSettings.getColorSpread().getFloat() / 1000f;
		Color bracketsColor = hudSettings.getBracketsColor().get();
		MultiColor textColor = hudSettings.getTextColor().get();
		Color valueTextColor = hudSettings.getValueTextColor().get();
		float fontHeight = font.getFontHeight();

		for (int i = 0; i < entries.size(); i++) {
			ElementEntry entry = entries.get(i);
			float x = drawX + (rightAligned ? (width - entry.totalWidth() - EXPAND * 2.0f) : 0);

			drawRect(matrices, x, y, entry.totalWidth() + EXPAND * 2.0f, fontHeight);

			float baseOffset = i * 0.1f;
			font.draw(matrices, entry.prefix(), x + EXPAND, y, textColor, baseOffset, spread);

			if (!entry.suffix().isEmpty()) {
				float suffixStartX = x + entry.prefixWidth() + EXPAND;
				font.draw(matrices, " [", suffixStartX, y - BRACKETS_OFFSET, bracketsColor);
				font.draw(matrices, entry.suffix(), suffixStartX + cachedBracketSpaceWidth, y, valueTextColor);
				font.draw(
						matrices,
						"]",
						suffixStartX + cachedBracketSpaceWidth + entry.suffixWidth(),
						y - BRACKETS_OFFSET,
						bracketsColor
				);
			}
			y += fontHeight;
		}
	}

	public record ElementEntry(
			String prefix,
			String suffix,
			float prefixWidth,
			float suffixWidth,
			float additionalWidth
	) {

		public float totalWidth() {return prefixWidth + suffixWidth + (suffixWidth == 0 ? 0 : additionalWidth);}
	}
}
