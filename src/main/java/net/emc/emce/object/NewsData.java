package net.emc.emce.object;

import com.google.gson.JsonObject;

public class NewsData {
    private final String msg;
    private final String sender;
    private final int id;

    public NewsData(JsonObject object) {
        this.msg = object.get("msg").getAsString();
        this.sender = object.get("sender").getAsString();
        this.id = object.get("id").getAsInt();
    }

    public NewsData() {
        this.msg = "";
        this.sender = "";
        this.id = 0;
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public int getID() {
        return id;
    }
}