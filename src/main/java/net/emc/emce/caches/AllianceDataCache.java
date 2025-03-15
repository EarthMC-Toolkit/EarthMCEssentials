package net.emc.emce.caches;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.emc.emce.utils.CustomAPI;
import net.emc.emce.utils.Messaging;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AllianceDataCache extends SimpleCache<Map<String, JsonObject>> {
    public static final AllianceDataCache INSTANCE = new AllianceDataCache();

    @Override
    public int secondsUntilExpiry() {
        return 60;
    }
    
    @Override
    public Map<String, JsonObject> fetchCacheData() {
        JsonArray alliances = CustomAPI.getAlliances();
        if (alliances.isEmpty()) {
            return null; // No alliances, no update. Continue using stale data.
        }

        Map<String, JsonObject> data = new HashMap<>();
        for (JsonElement alliance : alliances) {
            JsonObject allianceObj = alliance.getAsJsonObject();
            
            String allianceName = allianceObj.get("allianceName").getAsString().toLowerCase(Locale.ROOT);
            data.put(allianceName, allianceObj);
        }
        
        Messaging.sendDebugMessage("Updated alliances. Count: " + data.size());
        return data;
    }
}