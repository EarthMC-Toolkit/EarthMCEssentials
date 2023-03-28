package net.emc.emce.objects.API;

import com.google.gson.JsonObject;
import net.emc.emce.config.ModConfig;

public class APIData {
    private final String domain;
    public Routes routes = new Routes();

    public static class Routes {
        public String allPlayers;
        public String nearby;
        public String news;
        public String alliances;
    }

    public APIData(JsonObject object) {
        domain = object.get("domain").getAsString();
        JsonObject routesObj = object.get("routes").getAsJsonObject();

        routes.allPlayers = routesObj.get("allplayers").getAsString();
        routes.nearby = routesObj.get("nearby").getAsString();
        routes.alliances = routesObj.get("alliances").getAsString();
        //routes.news = routesObj.get("news").getAsString();
    }

    public APIData() {
        domain = "";

        routes.allPlayers = "";
        routes.nearby = "";
        routes.news = "";
        routes.alliances = "";
    }

    public String getDomain() { return domain; }
}