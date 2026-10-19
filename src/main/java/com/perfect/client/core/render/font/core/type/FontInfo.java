package com.perfect.client.core.render.font.core.type;

import lombok.Getter;

@Getter
public class FontInfo {

	private final FontType fontType;
	private final FontStyle fontStyle;

	public FontInfo(FontType fontType, FontStyle fontStyle) {
		this.fontType = fontType;
		this.fontStyle = fontStyle;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FontInfo fontInfo)) {
			return false;
		}
		return fontType == fontInfo.fontType && fontStyle == fontInfo.fontStyle;
	}

	@Override
	public int hashCode() {
		return fontType.hashCode();
	}
}
