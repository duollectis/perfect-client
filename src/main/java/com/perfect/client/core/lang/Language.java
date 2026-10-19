package com.perfect.client.core.lang;

import com.perfect.client.core.utils.text.INameable;
import lombok.Getter;

@Getter
public enum Language implements INameable {

	ENGLISH("English", "en"),
	RUSSIAN("Русский", "ru");

	private final String name;
	private final String fileName;

	Language(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}
}
