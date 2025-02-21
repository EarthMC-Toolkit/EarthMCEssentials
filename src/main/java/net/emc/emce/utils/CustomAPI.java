package net.emc.emce.utils;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.objects.CustomAPI.APIData;
import net.emc.emce.objects.CustomAPI.APIRoute;

import com.google.gson.JsonElement;
import io.github.emcw.utils.http.JSONRequest;
import java.util.concurrent.CompletableFuture;

public class CustomAPI {
    public static APIData apiData = new APIData();
    private static final String endpoints =
          "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMCEssentials/main/src/main/resources/api.json";
    
    @Contract(" -> new")
    public static @NotNull CompletableFuture<JsonArray> getAlliances() {
        return CompletableFuture.supplyAsync(() -> {
            JsonElement alliances = JSONRequest.sendGet(getRoute(APIRoute.ALLIANCES));
            if (alliances != null) {
                return alliances.getAsJsonArray();
            }
            
            Messaging.sendDebugMessage("Could not get alliances. GET request returned null.");
            return new JsonArray();
        });
    }
    
    public static @NotNull String getRoute(@NotNull APIRoute routeType) {
        String route;
        
        switch(routeType) {
            case ALLIANCES -> route = apiData.routes.alliances;
            default -> throw new IllegalStateException("Unexpected value: " + routeType);
        }
        
        String endpoint = EarthMCEssentials.instance().currentMap.getName() + route;
        Messaging.sendDebugMessage("Requesting endpoint -> " + endpoint);
        
        return apiData.getDomain() + endpoint;
    }
}