package com.perfect.client.gui.utils;

public class SearchUtils {
    private static final float MAX_DIFF = 0.15f;

    public static String standard(String text) {
        return text.toLowerCase().replace(" ", "").replace("-", "");
    }

    public static boolean search(String text, String filter) {
        return countDiff(standard(text), standard(filter)) < MAX_DIFF;
    }

    private static float countDiff(String text, String filter) {
        if (filter.isEmpty()) {
            return 0f;
        }

        int matches = 0;

        for (int i = 0; i < filter.length(); i++) {
            for (int k = 0; k < text.length(); k++) {
                char filterChar = filter.charAt(i);
                char textChar = text.charAt(k);

                if (filterChar == textChar) {
                    matches++;
                    break;
                }
            }
        }

        return (float) (filter.length() - matches) / filter.length();
    }
}
