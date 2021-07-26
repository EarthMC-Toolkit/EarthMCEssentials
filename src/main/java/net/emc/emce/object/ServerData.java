package net.emc.emce.object;

import com.google.gson.JsonObject;

public class ServerData {
    private final boolean serverOnline;
    private final int online;
    private final int max;
    private final int towny;
    private final int queue;

    /**
     * Constructs a new serverInfo object from a {@link JsonObject}
     * @param object Server info as a json object.
     */
    public ServerData(JsonObject object) {
        serverOnline = object.get("serverOnline").getAsBoolean();
        online = object.get("online").getAsInt();
        max = object.get("max").getAsInt();
        towny = object.get("towny").getAsInt();
        queue = object.get("queue").getAsInt();
    }

    public ServerData() {
        this.serverOnline = false;
        this.online = 0;
        this.max = 0;
        this.towny = 0;
        this.queue = 0;
    }

    public boolean isServerOnline() {
        return serverOnline;
    }

    public int getOnline() {
        return online;
    }

    public int getMax() {
        return max;
    }

    public int getTowny() {
        return towny;
    }

    public int getQueue() {
        return queue;
    }
}
