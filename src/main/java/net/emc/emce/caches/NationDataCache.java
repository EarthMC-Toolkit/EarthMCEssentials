package net.emc.emce.caches;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NationDataCache extends Cache<Map<String, JsonObject>> {
    public static final NationDataCache INSTANCE = new NationDataCache();

    @Override
    public CompletableFuture<@NotNull Map<String, JsonObject>> getCache() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.cachedData == null || this.needsUpdate()) {
                this.updating = true;
                this.cachedData = new HashMap<>();

                JsonArray nations = EarthMCAPI.getNations().join();
                for (JsonElement nation : nations) {
                    JsonObject object = nation.getAsJsonObject();
                    this.cachedData.put(object.get("name").getAsString().toLowerCase(Locale.ROOT), object);
                }

                Messaging.sendDebugMessage("Updated nations, array size: " + this.cachedData.size());
                this.update();
            }

            return this.cachedData;
        });
    }

    @Override
    public void clear() {
        this.cachedData.clear();
        super.clear();
    }
}
