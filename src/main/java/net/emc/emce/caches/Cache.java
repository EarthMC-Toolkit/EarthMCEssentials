package net.emc.emce.caches;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public abstract class Cache<T> {
    final int CACHE_SECONDS = 600;
    private Instant lastUpdate = Instant.now();
    public boolean updating = false;
    public T cachedData;

    public boolean cacheNeedsUpdate() {
        return !this.updating && this.lastUpdate.plusSeconds(CACHE_SECONDS).isBefore(Instant.now());
    }

    public void updateCache() {
        this.lastUpdate = Instant.now();
        this.updating = false;
    }

    public abstract CompletableFuture<T> getCache();

    public void clearCache() {
        this.cachedData = null;
    }
}