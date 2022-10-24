package net.emc.emce.objects.News;

import com.google.gson.JsonObject;

public class NewsData {
    private final String message;
    private final int id, timestamp;

    public NewsData(JsonObject object) {
        JsonObject elem = object != null ? object.get("latest").getAsJsonObject() : null;
        
        this.message = elem == null ? "" : elem.get("message").getAsString();
        this.id = elem == null ? 0 : elem.get("id").getAsInt();
        this.timestamp = elem == null ? 0 : elem.get("timestamp").getAsInt();
    }

    public String getMsg() {
        return message;
    }
    public int getID() {
        return id;
    }
    public int getTimestamp() {
        return timestamp;
    }
}