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

public class AllianceDataCache extends Cache<Map<String, JsonObject>> {
    public static final AllianceDataCache INSTANCE = new AllianceDataCache();

    @Override
    public CompletableFuture<@NotNull Map<String, JsonObject>> getCache() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.cachedData == null || this.needsUpdate()) {
                this.updating = true;
                this.cachedData = new HashMap<>();

                JsonArray alliances = EarthMCAPI.getAlliances().join();
                for (JsonElement alliance : alliances) {
                    JsonObject object = alliance.getAsJsonObject();
                    this.cachedData.put(object.get("allianceName").getAsString().toLowerCase(Locale.ROOT), object);
                }

                Messaging.sendDebugMessage("Updated alliances, array size: " + this.cachedData.size());
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
