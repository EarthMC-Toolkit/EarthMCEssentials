package net.emc.emce.caches;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TownDataCache extends Cache<Map<String, JsonObject>> {
    public static final TownDataCache INSTANCE = new TownDataCache();

    @Override
    public CompletableFuture<Map<String, JsonObject>> getCache() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.cachedData == null || this.needsUpdate()) {
                this.updating = true;
                this.cachedData = new HashMap<>();

                JsonArray towns = EarthMCAPI.getTowns().join();
                for (JsonElement town : towns) {
                    JsonObject object = town.getAsJsonObject();
                    this.cachedData.put(object.get("name").getAsString().toLowerCase(Locale.ROOT), object);
                }

                Messaging.sendDebugMessage("Updated towns, array size: " + this.cachedData.size());
                this.update();
            }

            return this.cachedData;
        });
    }

    @Override
    public void clearCache() {
        this.cachedData.clear();
        super.clearCache();
    }
}
