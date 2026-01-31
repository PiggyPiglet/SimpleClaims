package com.buuz135.simpleclaims.files;

import com.buuz135.simpleclaims.claim.player_name.PlayerNameTracker;
import com.buuz135.simpleclaims.util.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class PlayerNameTrackerBlockingFile extends BlockingDiskFile {

    private PlayerNameTracker tracker;

    public PlayerNameTrackerBlockingFile() {
        super(Path.of(FileUtils.NAMES_CACHE_PATH));
        this.tracker = new PlayerNameTracker();
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        var rootElement = JsonParser.parseReader(bufferedReader);
        if (rootElement == null || !rootElement.isJsonObject()) return;
        var root = rootElement.getAsJsonObject();
        JsonArray valuesArray = root.getAsJsonArray("Values");
        if (valuesArray == null) return;
        this.tracker = new PlayerNameTracker();
        valuesArray.forEach(jsonElement -> {
            JsonObject playerObj = jsonElement.getAsJsonObject();
            this.tracker.setPlayerName(
                    UUID.fromString(playerObj.get("UUID").getAsString()),
                    playerObj.get("Name").getAsString(),
                    -1,
                    0
            );
        });
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray valuesArray = new JsonArray();
        for (PlayerNameTracker.PlayerName name : this.tracker.getNames()) {
            JsonObject playerObj = new JsonObject();
            playerObj.addProperty("UUID", name.getUuid().toString());
            playerObj.addProperty("Name", name.getName());
            valuesArray.add(playerObj);
        }
        root.add("Values", valuesArray);
        bufferedWriter.write(root.toString());
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        this.tracker = new PlayerNameTracker();
        write(bufferedWriter);
    }

    public PlayerNameTracker getTracker() {
        return tracker;
    }
}
