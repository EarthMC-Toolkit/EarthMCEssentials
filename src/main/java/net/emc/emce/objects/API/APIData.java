package net.emc.emce.objects.API;

import com.google.gson.JsonObject;
import net.emc.emce.config.ModConfig;

public class APIData {
    private final String domain;
    public Routes routes = new Routes();

    public static class Routes {
        public String townless;
        public String nations;
        public String towns;
        public String residents;
        public String allPlayers;
        public String onlinePlayers;
        public String nearby;
        public String alliances;
    }

    public APIData(JsonObject object) {
        domain = object.get("domain").getAsString();
        JsonObject routesObj = object.get("routes").getAsJsonObject();

        routes.towns = routesObj.get("towns").getAsString();
        routes.nations = routesObj.get("nations").getAsString();
        routes.residents = routesObj.get("residents").getAsString();

        routes.onlinePlayers = routesObj.get("onlineplayers").getAsString();
        routes.allPlayers = routesObj.get("allplayers").getAsString();

        routes.townless = routesObj.get("townless").getAsString();
        routes.nearby = routesObj.get("nearby").getAsString();

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
        routes.alliances = "";
    }

    public String getDomain() { return domain; }
}