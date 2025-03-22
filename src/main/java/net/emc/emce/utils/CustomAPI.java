package net.emc.emce.utils;

import com.google.gson.JsonArray;

import lombok.Getter;
import net.emc.emce.EMCEssentials;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonElement;
import io.github.emcw.utils.http.JSONRequest;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

import java.util.concurrent.CompletableFuture;

public class CustomAPI {
    public static final String DOMAIN = "https://emctoolkit.vercel.app/api/";
    
    @Getter
    public enum EndpointType {
        ALLIANCES("/alliances"),
        NEWS("/news");
        
        private final String value;
        
        EndpointType(String endpoint) {
            this.value = endpoint;
        }
    }
    
    @Contract(" -> new")
    public static @NotNull JsonArray getAlliances() {
        CompletableFuture<JsonElement> alliancesRes = sendAsyncGet(EndpointType.ALLIANCES);
        if (alliancesRes == null) {
            Messaging.sendDebugMessage("An error occurred fetching alliances, check the console to see the request details.", Level.ERROR);
            return new JsonArray();
        }
        
        return alliancesRes.thenApplyAsync(JsonElement::getAsJsonArray).join();
    }
    
    @Contract(" -> new")
    public static @NotNull JsonArray getNews() {
        CompletableFuture<JsonElement> newsRes = sendAsyncGet(EndpointType.NEWS);
        if (newsRes == null) {
            Messaging.sendDebugMessage("An error occurred fetching news, check the console to see the request details.", Level.ERROR);
            return new JsonArray();
        }
   
        return newsRes.thenApplyAsync(JsonElement::getAsJsonArray).join();
    }
    
    @SuppressWarnings("all")
    private static @Nullable CompletableFuture<JsonElement> sendAsyncGet(
        @NotNull EndpointType endpoint
    ) throws IllegalStateException {
        return CompletableFuture.supplyAsync(() -> {
            String route = EMCEssentials.instance().currentMap.getName() + endpoint.getValue();
            //Messaging.sendDebugMessage("Requesting endpoint -> " + endpoint);
            
            return JSONRequest.sendGet(DOMAIN + route);
        });
    }
}