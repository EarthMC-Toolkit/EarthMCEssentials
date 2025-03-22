package net.emc.emce.caches;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.emc.emce.utils.CustomAPI;
import net.emc.emce.utils.Messaging;
import org.slf4j.event.Level;

import java.util.*;

public class NewsDataCache extends SimpleCache<List<JsonElement>> {
    public static final NewsDataCache INSTANCE = new NewsDataCache();
    
    @Override
    public int secondsUntilExpiry() {
        return 5 * 60;
    }
    
    @Override
    protected List<JsonElement> fetchCacheData() {
        JsonArray news = CustomAPI.getNews();
        if (news.isEmpty()) {
            return null; // No alliances, no update. Continue using stale data.
        }

        Messaging.sendDebugMessage("Updated news. Count: " + news.size(), Level.INFO);
        return news.asList();
    }
}