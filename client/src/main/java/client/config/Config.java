package client.config;

import java.util.*;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;

public class Config {

    private static final String CONFIG_LOCATION = "user-config.json";

    private UUID defaultCollectionId;

    public Config() {
        // for object mapper
    }

    public static Config load() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File(CONFIG_LOCATION);
        try {
            return objectMapper.readValue(configFile, Config.class);
        } catch (IOException e) {
            System.out.println("Config not found. Creating a default config...");
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

    public UUID getDefaultCollectionId() {
        return defaultCollectionId;
    }

    public void setDefaultCollectionId(UUID defaultCollectionId) {
        this.defaultCollectionId = defaultCollectionId;
        save();
    }
}
