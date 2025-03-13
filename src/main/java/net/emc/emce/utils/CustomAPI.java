package net.emc.emce.utils;

import com.google.gson.JsonArray;

import lombok.Getter;
import net.emc.emce.EMCEssentials;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonElement;
import io.github.emcw.utils.http.JSONRequest;
import org.jetbrains.annotations.Nullable;

public class CustomAPI {
    public static final String DOMAIN = "https://emctoolkit.vercel.app/api/";
    
    @Getter
    public enum Route {
        ALLIANCES("/alliances"),
        NEWS("/news");
        
        private final String endpoint;
        
        Route(String endpoint) {
            this.endpoint = endpoint;
        }
    }
    
    @Contract(" -> new")
    public static @NotNull JsonArray getAlliances() {
        JsonElement alliancesRes = sendAsyncGet(Route.ALLIANCES);
        if (alliancesRes == null) {
            Messaging.sendDebugMessage("An error occurred fetching alliances, check the console to see the request details.");
            return new JsonArray();
        }
        
        return alliancesRes.getAsJsonArray();
    }
    
    @Contract(" -> new")
    public static @NotNull JsonArray getNews() {
        JsonElement newsRes = sendAsyncGet(Route.NEWS);
        if (newsRes == null) {
            Messaging.sendDebugMessage("An error occurred fetching news, check the console to see the request details.");
            return new JsonArray();
        }
        
        JsonArray news = newsRes.getAsJsonObject().getAsJsonArray("all");
        if (news == null) {
            Messaging.sendDebugMessage("An error occurred fetching news, key 'all' may be missing from the object.");
            return new JsonArray();
        }
        
        return news;
    }
    
    @SuppressWarnings("all")
    private static @Nullable JsonElement sendAsyncGet(@NotNull Route route) throws IllegalStateException {
        String endpoint = EMCEssentials.instance().currentMap.getName() + route.getEndpoint();
        //Messaging.sendDebugMessage("Requesting endpoint -> " + endpoint);
        
        return JSONRequest.ASYNC.sendGet(DOMAIN + endpoint);
    }
}