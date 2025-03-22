package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.emcw.KnownMap;
import io.github.emcw.oapi.OfficialAPI;

import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

import java.util.concurrent.CompletableFuture;

// Doc Reference: https://earthmc.net/docs/api
public class OAPIV3 {
    static OfficialAPI.V3 auroraAPI = new OfficialAPI.V3(KnownMap.AURORA);

    public static @Nullable JsonElement getPlayer(KnownMap map, String name) {
        switch (map) {
            case AURORA: return getAuroraPlayer(name);
            default: {
                String errMsg = String.format("Could not get player '%s' because the given map is invalid. Map value: %s", name, map);
                Messaging.sendDebugMessage(errMsg, Level.DEBUG);
                
                return null;
            }
        }
    }
    
    public static @Nullable JsonElement getAuroraPlayer(String name) {
        return CompletableFuture.supplyAsync(() -> {
            JsonArray players = auroraAPI.players(new String[]{ name });
            if (players == null || players.isEmpty()) {
                return null;
            }
            
            return players.get(0);
        }).join();
    }
}