package client.utils;


import client.Main;

import java.util.prefs.Preferences;

public class LanguagePreference {
    private static final String LANGUAGE_KEY = "language_option";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(LanguagePreference.class);

    /**
     * Save language locally to restore preference when app is opened again.
     * @param language The language to save.
     */
    public static void saveLanguage(Main.Language language) {
        String lang = switch (language) {
            case EN -> "English";
            case NL -> "Dutch";
            case ES -> "Spanish";
        };
        PREFERENCES.put(LANGUAGE_KEY, lang);
    }

    /**
     * Restore language from storage to use in app when re-opened.
     * @return The stored language or the default english.
     */
    public static Main.Language getLanguage() {
        String lang = PREFERENCES.get(LANGUAGE_KEY, "English");
        return switch (lang) {
            case "Dutch" -> Main.Language.NL;
            case "Spanish" -> Main.Language.ES;
            default -> Main.Language.EN;
        };
    }

}
