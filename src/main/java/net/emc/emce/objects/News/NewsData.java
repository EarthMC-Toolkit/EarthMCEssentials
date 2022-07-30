package net.emc.emce.objects.News;

import com.google.gson.JsonObject;

public class NewsData {
    private final String message;
    private final int id;

    public NewsData(JsonObject object) {
        this.message = object == null ? "" : object.get("message").getAsString();
        this.id = object == null ? 0 : object.get("id").getAsInt();
    }

    public String getMsg() {
        return message;
    }
    public int getID() {
        return id;
    }
}