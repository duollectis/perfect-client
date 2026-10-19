package com.perfect.client.core.utils.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfigIdentifier {

	public static final ConfigIdentifier UNKNOWN = new ConfigIdentifier("Unknown", "");

	private final String author;
	private final String date;
}
