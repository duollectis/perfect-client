package com.perfect.client.core.lang;

import java.util.HashMap;
import java.util.Map;

public class Translations {

	private static final String LANG_PATH = "language/%s.lang";

	private static final Map<String, String> STANDARD_TRANSLATIONS = new HashMap<>();
	private static final Map<String, String> LOCAL_TRANSLATIONS = new HashMap<>();

	public static void init() {
		loadLang(STANDARD_TRANSLATIONS, Language.ENGLISH);
	}

	public static String get(String path) {
		return get(path, path, true);
	}

	public static String get(String path, String defaultValue, boolean useStandard) {
		String translation = LOCAL_TRANSLATIONS.get(path);

		if (translation != null) {
			return translation;
		}

		if (useStandard) {
			translation = STANDARD_TRANSLATIONS.get(path);

			if (translation != null) {
				return translation;
			}
		}

		return defaultValue;
	}

	public static void loadLocalLanguage(Language language) {
		loadLang(LOCAL_TRANSLATIONS, language);
	}

	public static void loadLang(Map<String, String> translations, Language language) {
		Map<String, String> _translations = loadTranslations(language);

		if (_translations != null) {
			translations.clear();
			translations.putAll(_translations);
		}
	}

	private static Map<String, String> loadTranslations(Language language) {
		return LangUtils.parseAsMap(LANG_PATH.formatted(language.getFileName()));
	}
}
