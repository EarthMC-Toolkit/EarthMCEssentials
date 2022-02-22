package net.emc.emce.caches;

import net.emc.emce.object.ServerData;
import net.emc.emce.utils.EarthMCAPI;

import java.util.concurrent.CompletableFuture;

public class ServerDataCache extends Cache<ServerData> {
    public static final ServerDataCache INSTANCE = new ServerDataCache();

    @Override
    public CompletableFuture<ServerData> getCache() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.cachedData == null || this.needsUpdate()) {
                this.updating = true;

                this.cachedData = EarthMCAPI.getServerData().join();
                this.update();
            }

            return this.cachedData;
        });
    }
}
