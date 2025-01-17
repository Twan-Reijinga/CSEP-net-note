package client.config;

import client.utils.Language;
import com.google.inject.Key;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

class LanguagePreferenceTest {

    private static final String LANGUAGE_KEY = "language_option";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(LanguagePreference.class);
    private String language;

    @BeforeEach
    void setUp() {
        language = PREFERENCES.get(LANGUAGE_KEY, "English");
    }

    @AfterEach
    void tearDown() {
        PREFERENCES.put(LANGUAGE_KEY, language);

    }

    @Test
    void saveLanguageSpanish() {
        LanguagePreference.saveLanguage(Language.ES);
        assertEquals("Spanish", PREFERENCES.get(LANGUAGE_KEY, "English"));
    }

    @Test
    void saveLanguageDutch() {
        LanguagePreference.saveLanguage(Language.NL);
        assertEquals("Dutch", PREFERENCES.get(LANGUAGE_KEY, "English"));
    }

    @Test
    void saveLanguageEnglish() {
        LanguagePreference.saveLanguage(Language.EN);
        assertEquals("English", PREFERENCES.get(LANGUAGE_KEY, "English"));
    }

    @Test
    void getLanguageSpanish() {
        LanguagePreference.saveLanguage(Language.ES);
        assertEquals(Language.ES, LanguagePreference.getLanguage());
    }

    @Test
    void getLanguageDutch() {
        LanguagePreference.saveLanguage(Language.NL);
        assertEquals(Language.NL, LanguagePreference.getLanguage());
    }

    @Test
    void getLanguageEnglish() {
        LanguagePreference.saveLanguage(Language.EN);
        assertEquals(Language.EN, LanguagePreference.getLanguage());
    }
}