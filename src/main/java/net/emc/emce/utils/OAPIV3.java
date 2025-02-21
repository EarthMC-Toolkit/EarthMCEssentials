package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.emcw.KnownMap;
import io.github.emcw.oapi.OfficialAPI;
import net.emc.emce.EMCEssentials;
import org.jetbrains.annotations.Nullable;

// Doc Reference: https://earthmc.net/docs/api
public class OAPIV3 {
    static OfficialAPI.V3 auroraAPI = new OfficialAPI.V3(KnownMap.AURORA);
    
    public static @Nullable JsonElement getPlayer(String name) {
        switch (EMCEssentials.instance().currentMap) {
            case AURORA: return getAuroraPlayer(name);
            default: return null;
        }
    }
    
    static @Nullable JsonElement getAuroraPlayer(String name) {
        JsonArray players = auroraAPI.players(new String[]{ name });
        if (players == null || players.isEmpty()) {
            return null;
        }
        
        return players.get(0);
    }
}