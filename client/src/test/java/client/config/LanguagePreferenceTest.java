package client.config;

import client.utils.Language;
import com.google.inject.Key;
import org.junit.jupiter.api.Test;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

class LanguagePreferenceTest {

    private static final String LANGUAGE_KEY = "language_option";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(LanguagePreference.class);


    @Test
    void saveLanguageSpanish() {
        // store
        String lang = PREFERENCES.get(LANGUAGE_KEY, "English");

        // check
        LanguagePreference.saveLanguage(Language.ES);
        assertEquals("Spanish", PREFERENCES.get(LANGUAGE_KEY, "English"));

        // cleanup
        PREFERENCES.put(LANGUAGE_KEY, lang);
    }

    @Test
    void saveLanguageDutch() {
        // store
        String lang = PREFERENCES.get(LANGUAGE_KEY, "English");

        // check
        LanguagePreference.saveLanguage(Language.NL);
        assertEquals("Dutch", PREFERENCES.get(LANGUAGE_KEY, "English"));

        // cleanup
        PREFERENCES.put(LANGUAGE_KEY, lang);
    }

    @Test
    void saveLanguageEnglish() {
        // store
        String lang = PREFERENCES.get(LANGUAGE_KEY, "English");

        // check
        LanguagePreference.saveLanguage(Language.EN);
        assertEquals("English", PREFERENCES.get(LANGUAGE_KEY, "English"));

        // cleanup
        PREFERENCES.put(LANGUAGE_KEY, lang);
    }

    @Test
    void getLanguageSpanish() {
        // setup
        String lang = PREFERENCES.get(LANGUAGE_KEY, "English");
        LanguagePreference.saveLanguage(Language.ES);


        // check
        assertEquals(Language.ES, LanguagePreference.getLanguage());

        // cleanup
        PREFERENCES.put(LANGUAGE_KEY, lang);
    }

    @Test
    void getLanguageDutch() {
        // setup
        String lang = PREFERENCES.get(LANGUAGE_KEY, "English");
        LanguagePreference.saveLanguage(Language.NL);


        // check
        assertEquals(Language.NL, LanguagePreference.getLanguage());

        // cleanup
        PREFERENCES.put(LANGUAGE_KEY, lang);
    }

    @Test
    void getLanguageEnglish() {
        // store
        String lang = PREFERENCES.get(LANGUAGE_KEY, "English");

        // check
        LanguagePreference.saveLanguage(Language.EN);
        assertEquals(Language.EN, LanguagePreference.getLanguage());

        // cleanup
        PREFERENCES.put(LANGUAGE_KEY, lang);
    }
}