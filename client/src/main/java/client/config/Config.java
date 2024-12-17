package client.config;

import java.util.*;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;

public class Config {

    private static final String configLocation = "user-config.json";

    private int syncThresholdMs;

    private Language language;

    private List<Collection> remoteCollections;

    // FIXME: not sustainable; collections are weak entities of server (IDs are only server-unique), yikes...
    public long defaultCollectionId;

    public Config() {
        // for object mapper
    }

    public Config(int syncThresholdMs, Language language) {
        this.syncThresholdMs = syncThresholdMs;
        this.language = language;
        this.remoteCollections = new ArrayList<>();
        this.defaultCollectionId = -1;
    }

    public static Config load() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File(configLocation);
        try {
            return objectMapper.readValue(configFile, Config.class);
        } catch (IOException e) {
            System.out.println("Creating default config...");
            System.out.println(e.getMessage());
            return new Config(5000, Language.English);
        }
    }

    public void save() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File(configLocation);
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

    public void setSyncThresholdMs(int syncThresholdMs) {
        this.syncThresholdMs = syncThresholdMs;
        save();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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
