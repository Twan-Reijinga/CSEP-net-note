package client.config;

import java.util.*;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;

public class Config {

    private static final String CONFIG_LOCATION = "user-config.json";

    private int syncThresholdMs;
    private String localServer;
    private UUID defaultCollectionId;
    private List<Collection> remoteCollections;

    public Config() {
        // for object mapper
    }

    public Config(int syncThresholdMs) {
        this.syncThresholdMs = syncThresholdMs;
        this.localServer = "http://localhost:8080";
        this.defaultCollectionId = null;
        this.remoteCollections = new ArrayList<>();
    }

    public static Config load() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File(CONFIG_LOCATION);
        try {
            return objectMapper.readValue(configFile, Config.class);
        } catch (IOException e) {
            System.out.println("Config not found. Creating a default config...");
            System.out.println(e.getMessage());

            return new Config(5000);
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

    public int getSyncThresholdMs() {
        return syncThresholdMs;
    }

    public String getLocalServer() {
        return localServer;
    }

    public UUID getDefaultCollectionId() {
        return defaultCollectionId;
    }

    public void setDefaultCollectionId(UUID defaultCollectionId) {
        this.defaultCollectionId = defaultCollectionId;
        save();
    }

    public List<Collection> getRemoteCollections() {
        return remoteCollections;
    }

    public void addRemoteCollection(Collection remoteCollection) {
        this.remoteCollections.add(remoteCollection);
        save();
    }

    public void removeRemoteCollection(Collection remoteCollection) {
        this.remoteCollections.remove(remoteCollection);
        save();
    }
}
