package net.emc.emce.caches;

import java.time.Instant;

public abstract class SimpleCache<T> {
    private T cachedData;
    
    private boolean updating = false;
    private Instant lastUpdate = Instant.now();
    
    public final void clearCache() {
        this.cachedData = null;
    }
    
    public final boolean cacheNeedsUpdate() {
        if (updating) return false;
        
        Instant diff = this.lastUpdate.plusSeconds(secondsUntilExpiry());
        return diff.isBefore(Instant.now());
    }
    
    public final T getCache() {
        if (this.cachedData != null && !this.cacheNeedsUpdate()) {
            return this.cachedData;
        }
        
        this.updating = true;
        
        T data = fetchCacheData();
        if (data == null) {
            return this.cachedData;
        }
        
        this.lastUpdate = Instant.now();
        this.updating = false;
        
        this.cachedData = data;
        return data;
    }
    
    protected abstract T fetchCacheData();
    protected abstract int secondsUntilExpiry();
}