package com.perfect.client.core.render.font.core.type;

import lombok.Getter;

@Getter
public enum FontType {
	MONTSERRAT("montserrat", FontStyle.REGULAR, FontStyle.ITALIC, FontStyle.MEDIUM, FontStyle.SEMI_BOLD),
	MINECRAFT("minecraft", FontStyle.REGULAR, FontStyle.ITALIC, FontStyle.BOLD, FontStyle.BOLD_ITALIC),
	VERDANA("verdana", FontStyle.REGULAR, FontStyle.ITALIC, FontStyle.BOLD, FontStyle.BOLD_ITALIC),
	ARIAL("arial", FontStyle.REGULAR, FontStyle.ITALIC, FontStyle.BOLD, FontStyle.BOLD_ITALIC);

	private final String fileName;
	private final FontStyle[] styles;

	FontType(String fileName, FontStyle... styles) {
		this.fileName = fileName;
		this.styles = styles;
	}
}
