package com.froobworld.archiveroute.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class ExileDataManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Set<ExileManager.ExileEntry> loadObjectsFromFile(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
                return new HashSet<>();
            }
            String jsonString = Files.readString(filePath);
            JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
            Set<ExileManager.ExileEntry> entries = new HashSet<>();
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
                String ipAddress = jsonObject.get("ip_address").getAsString();
                entries.add(new ExileManager.ExileEntry(uuid, ipAddress));
            }
            return entries;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public void saveObjectsToFile(Set<ExileManager.ExileEntry> entries, Path filePath) {
        try {
            JsonArray jsonArray = new JsonArray();
            for (ExileManager.ExileEntry exileEntry : entries) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("uuid", exileEntry.uuid().toString());
                jsonObject.addProperty("ip_address", exileEntry.ipAddress());
                jsonArray.add(jsonObject);
            }
            String jsonString = gson.toJson(jsonArray);
            Files.writeString(filePath, jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
