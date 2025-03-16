package net.emc.emce.caches;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.utils.CustomAPI;
import net.emc.emce.utils.Messaging;

import java.util.*;

public class NewsDataCache extends SimpleCache<Map<Long, JsonObject>> {
    public static final NewsDataCache INSTANCE = new NewsDataCache();
    
    @Override
    public int secondsUntilExpiry() {
        return 5 * 60;
    }
    
    @Override
    protected Map<Long, JsonObject> fetchCacheData() {
        JsonArray news = CustomAPI.getNews();
        if (news.isEmpty()) {
            return null; // No alliances, no update. Continue using stale data.
        }
        
        Map<Long, JsonObject> data = new HashMap<>();
        for (JsonElement newsMsg : news) {
            JsonObject newsObj = newsMsg.getAsJsonObject();
            
            long newsMsgId = newsObj.get("id").getAsLong();
            data.put(newsMsgId, newsObj);
        }
        
        Messaging.sendDebugMessage("Updated news. Count: " + data.size());
        return data;
    }
}