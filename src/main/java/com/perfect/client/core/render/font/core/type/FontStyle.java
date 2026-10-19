package com.perfect.client.core.render.font.core.type;

import lombok.Getter;

@Getter
public enum FontStyle {

	REGULAR("regular"),
	ITALIC("italic"),
	MEDIUM("medium"),
	SEMI_BOLD("semi-bold"),
	BOLD("bold"),
	BOLD_ITALIC("bold-italic");

	private final String fileName;

	FontStyle(String fileName) {
		this.fileName = fileName;
	}
}
