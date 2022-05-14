package net.emc.emce.object;

import com.google.gson.JsonObject;
import net.emc.emce.config.ModConfig;

public class APIData {
    private final String domain;
    public Routes routes = new Routes();
    public static String mapName = "aurora";

    public static class Routes {
        public String townless;
        public String nations;
        public String towns;
        public String residents;
        public String allPlayers;
        public String onlinePlayers;
        public String nearby;
        public String serverInfo;
        public String news;
        public String alliances;
    }

    public static void setMap(String name) {
        mapName = name;
    }

    public APIData(JsonObject object) {
        String dom = object.get("domain").getAsString();
        String ver = ModConfig.instance().api.version;

        domain = dom + "/api/" + ver + "/" + mapName + "/";

        JsonObject routesObj = object.get("routes").getAsJsonObject();

        routes.townless = routesObj.get("townless").getAsString();
        routes.nations = routesObj.get("nations").getAsString();
        routes.towns = routesObj.get("towns").getAsString();
        routes.residents = routesObj.get("residents").getAsString();
        routes.allPlayers = routesObj.get("allplayers").getAsString();
        routes.onlinePlayers = routesObj.get("onlineplayers").getAsString();
        routes.nearby = routesObj.get("nearby").getAsString();
        routes.serverInfo = routesObj.get("serverData").getAsString();
        routes.news = routesObj.get("news").getAsString();
        routes.alliances = routesObj.get("alliances").getAsString();
    }

    public APIData() {
        domain = "";

        routes.townless = "";
        routes.nations = "";
        routes.towns = "";
        routes.residents = "";
        routes.allPlayers = "";
        routes.onlinePlayers = "";
        routes.nearby = "";
        routes.serverInfo = "";
        routes.news = "";
        routes.alliances = "";
    }

    public String getDomain() { return domain; }
}