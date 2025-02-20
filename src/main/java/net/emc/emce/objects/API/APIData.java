package net.emc.emce.objects.API;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class APIData {
    private final String domain;
    public final Routes routes;

    public static class Routes {
        public final String news;
        public final String alliances;

        public Routes() {
            news = "";
            alliances = "";
        }

        public Routes(String allPlayers, String alliances) {
            this.news = allPlayers;
            this.alliances = alliances;
        }
    }

    public APIData(@NotNull JsonObject object) {
        domain = keyAsStr(object, "domain");
        JsonObject obj = object.get("routes").getAsJsonObject();

        routes = new Routes(keyAsStr(obj, "news"), keyAsStr(obj, "alliances"));
    }

    public APIData() {
        domain = "";
        routes = new Routes();
    }

    public String getDomain() { return domain; }
}