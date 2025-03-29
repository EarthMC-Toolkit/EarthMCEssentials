package net.emc.emce.utils.api;

import io.github.emcw.KnownMap;
import io.github.emcw.oapi.OfficialAPI;

import net.emc.emce.utils.Messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

// Doc Reference: https://earthmc.net/docs/api
@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class OAPIV3 {
    static OfficialAPI.V3 auroraAPI = new OfficialAPI.V3(KnownMap.AURORA);

    public static @Nullable JsonElement getPlayer(KnownMap map, String name) {
        switch (map) {
            case AURORA: return Aurora.getPlayer(name);
            default: {
                String errMsg = String.format("Could not get player '%s' because the given map is invalid. Map value: %s", name, map);
                Messaging.sendDebugMessage(errMsg, Level.DEBUG);
                
                return null;
            }
        }
    }
    
    public static @Nullable JsonArray getQuarters(KnownMap map, String[] uuids) {
        switch (map) {
            case AURORA: return Aurora.getQuarters(uuids);
            default: {
                String errMsg = String.format("Could not get quarters because the given map is invalid. Map value: %s", map);
                Messaging.sendDebugMessage(errMsg, Level.DEBUG);
                
                return null;
            }
        }
    }
    
    public static @Nullable JsonArray getTowns(KnownMap map, String[] uuids) {
        switch (map) {
            case AURORA: return Aurora.getTowns(uuids);
            default: {
                String errMsg = String.format("Could not get towns because the given map is invalid. Map value: %s", map);
                Messaging.sendDebugMessage(errMsg, Level.DEBUG);
                
                return null;
            }
        }
    }
    
    public static @Nullable JsonArray getNations(KnownMap map, String[] uuids) {
        switch (map) {
            case AURORA: return Aurora.getNations(uuids);
            default: {
                String errMsg = String.format("Could not get nations because the given map is invalid. Map value: %s", map);
                Messaging.sendDebugMessage(errMsg, Level.DEBUG);
                
                return null;
            }
        }
    }
    
    public static class Aurora {
        public static @Nullable JsonElement getPlayer(String name) {
            return async(() -> {
                JsonArray players = auroraAPI.players(new String[]{ name });
                if (players == null || players.isEmpty()) {
                    return null;
                }
                
                return players.get(0);
            });
        }
        
        public static @Nullable JsonArray getQuarters(String[] uuids) {
            return async(() -> {
                JsonArray quarters = auroraAPI.quarters(uuids);
                if (quarters == null || quarters.isEmpty()) {
                    return null;
                }
                
                return quarters;
            });
        }
        
        public static @Nullable JsonArray getTowns(String[] identifiers) {
            return async(() -> {
                JsonArray towns = auroraAPI.towns(identifiers);
                if (towns == null || towns.isEmpty()) {
                    return null;
                }
                
                return towns;
            });
        }
        
        public static @Nullable JsonArray getNations(String[] identifiers) {
            return async(() -> {
                JsonArray nations = auroraAPI.nations(identifiers);
                if (nations == null || nations.isEmpty()) {
                    return null;
                }
                
                return nations;
            });
        }
        
        public static @Nullable <T> T async(Supplier<T> supplier) {
            return CompletableFuture.supplyAsync(supplier).join();
        }
    }
}