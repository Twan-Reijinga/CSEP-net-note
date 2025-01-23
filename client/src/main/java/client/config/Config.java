/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.config;

import java.util.*;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Config {

    private static final String CONFIG_LOCATION = "user-config.json";

    // Server URL defaults to localhost on port 8080
    private String serverUrl = "http://localhost:8080";
    // Default collection ID is supposed to be null at initial launch
    private UUID defaultCollectionId = null;
    // Language defaults to English
    private Locale language = Locale.of("EN", "us");

    public Config() {
        // for object mapper
    }

    public static Config load() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File(CONFIG_LOCATION);
        try {
            Config config = objectMapper.readValue(configFile, Config.class);

            // Server URL not allowed to be null
            if (config.serverUrl == null) {
                config.serverUrl = "http://localhost:8080";
            }

            // Language not allowed to be null
            if (config.language == null) {
                config.language = Locale.of("EN", "us");
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
     * Get a URL to a server that hosts user data
     * @return a string URL
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Restore language from storage to use in app when re-opened.
     * @return The stored language or the default english.
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Save language locally to restore preference when app is opened again.
     * @param language The language to save.
     */
    public void setLanguage(Locale language) {
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
