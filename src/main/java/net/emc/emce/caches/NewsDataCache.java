package net.emc.emce.caches;

import com.google.gson.JsonArray;

public class NewsDataCache extends SimpleCache<JsonArray> {
    @Override
    public int secondsUntilExpiry() {
        return 5 * 60;
    }
    
    @Override
    public JsonArray fetchCacheData() {
        return null;
    }
}