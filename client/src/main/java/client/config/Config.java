package client.config;

import java.util.*;
import java.io.*;

import client.utils.Language;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;

public class Config {

    private static final String CONFIG_LOCATION = "user-config.json";

    // Default collection ID is supposed to be null at initial launch
    private UUID defaultCollectionId = null;
    // Language defaults to English
    private Language language = Language.EN;

    public Config() {
        // for object mapper
    }

    public static Config load() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File(CONFIG_LOCATION);
        try {
            Config config = objectMapper.readValue(configFile, Config.class);

            // Language not allowed to be null
            if (config.language == null) {
                config.language = Language.EN;
            }

            return config;
        } catch (IOException e) {
            System.out.println("Config is not available. Creating a default config...");
            System.out.println(e.getMessage());

            return new Config();
        }
    }

    public void save() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File(CONFIG_LOCATION);
        try {
            objectMapper.writeValue(configFile, this);
        } catch (IOException e) {
            // TODO: should be display to the user
            System.err.println("ERROR: Failed to save config: " + e.getMessage());
        }
    }

    /**
     * Restore language from storage to use in app when re-opened.
     * @return The stored language or the default english.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Save language locally to restore preference when app is opened again.
     * @param language The language to save.
     */
    public void setLanguage(Language language) {
        this.language = language;
        save();
    }

    /**
     * Get the default collection ID from the local config file
     * @return a UUID of default collection
     */
    public UUID getDefaultCollectionId() {
        return defaultCollectionId;
    }

    /**
     * Set a new ID for default collection and save to the local config file
     * @param defaultCollectionId an ID of a new default collection
     */
    public void setDefaultCollectionId(UUID defaultCollectionId) {
        this.defaultCollectionId = defaultCollectionId;
        save();
    }
}
