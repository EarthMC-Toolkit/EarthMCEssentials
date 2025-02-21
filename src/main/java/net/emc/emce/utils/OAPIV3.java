package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.emcw.KnownMap;
import io.github.emcw.oapi.OfficialAPI;
import net.emc.emce.EarthMCEssentials;
import org.jetbrains.annotations.Nullable;

public class OAPIV3 {
    static OfficialAPI.V3 auroraAPI = new OfficialAPI.V3(KnownMap.AURORA);

    // Doc Reference: https://earthmc.net/docs/api#players
    public static @Nullable JsonElement getPlayer(String name) {
        switch(EarthMCEssentials.instance().currentMap) {
            case AURORA:
            default: {
                JsonArray players = auroraAPI.players(new String[]{ name });
                return players != null ? players.get(0) : null;
            }
        }
    }
}