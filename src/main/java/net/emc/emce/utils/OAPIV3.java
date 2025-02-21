package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.emcw.KnownMap;
import io.github.emcw.oapi.OfficialAPI;
import org.jetbrains.annotations.Nullable;

import static net.emc.emce.EarthMCEssentials.instance;

public class OAPIV3 {
    static OfficialAPI.V3 auroraAPI = new OfficialAPI.V3(KnownMap.AURORA);

    // Doc Reference: https://earthmc.net/docs/api#players
    public static @Nullable JsonElement getPlayer(String name) {
        switch(instance().currentMap) {
            case AURORA:
            default: {
                JsonArray players = auroraAPI.players(new String[]{ name });
                return players != null ? players.get(0) : null;
            }
        }
    }
}