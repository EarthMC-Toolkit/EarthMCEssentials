package net.emc.emce.modules;

import io.github.emcw.KnownMap;
import net.emc.emce.EMCEssentials;
import net.emc.emce.caches.AllianceDataCache;
import net.emc.emce.caches.NewsDataCache;
import net.emc.emce.caches.SimpleCache;
import net.emc.emce.config.ModConfig;

import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.MinecraftClient;
import org.slf4j.event.Level;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    public boolean townlessRunning, nearbyRunning, cacheCheckRunning;
    public boolean hasMap = false;

    //private static final List<SimpleCache<?>> CACHES = List.of(AllianceDataCache.INSTANCE, NewsDataCache.INSTANCE);
    private ScheduledExecutorService service;

    public void start() {
        ModConfig config = ModConfig.instance();

        // Pre-fill data.
        if (config.general.enableMod) {
            if (config.townless.enabled) EMCEssentials.instance().updateTownless();
            if (config.nearby.enabled) EMCEssentials.instance().updateNearbyPlayers();
        }

        //startCacheCheck();
        startTownless();
        startNearby();
    }

    public void stop() {
        townlessRunning = false;
        nearbyRunning = false;
        cacheCheckRunning = false;

        Messaging.sendDebugMessage("Stopping scheduled tasks...", Level.DEBUG);
    }

    public void initMap() {
        service = Executors.newScheduledThreadPool(2);
        service.scheduleAtFixedRate(this::checkMap, 0, 5, TimeUnit.SECONDS);
    }
    
    /**
     * Tests all known maps to see which the player is online in.
     * Will break and set {@link #hasMap} to {@code true} at the first map detected, {@code false} otherwise.
     */
    void checkMap() {
        if (hasMap) return;
        
        // In singleplayer, no need to check, just use current one (likely default).
        if (ModUtils.isInSinglePlayer() && ModConfig.instance().general.enableInSingleplayer) {
            setHasMap(EMCEssentials.instance().currentMap);
            return;
        }

        Messaging.sendDebugMessage("Checking which map client player is on...", Level.DEBUG);
        
        for (KnownMap map : KnownMap.values()) {
            if (EMCEssentials.instance().clientOnlineInSquaremap(map)) {
                setHasMap(map);
                return;
            }
        }
        
        setHasMap(null);
    }

    public void reset() {
        OverlayRenderer.Clear();
        setHasMap(null);
    }

    public void setHasMap(KnownMap map) {
        hasMap = map != null;
        
        if (map == null) {
            Messaging.sendDebugMessage("Player not found on any map!", Level.DEBUG);
            stop();
            
            return;
        }
        
        if (ModUtils.isConnectedToEMC()) {
            EMCEssentials.instance().currentMap = map;
            Messaging.sendDebugMessage("Player found on: " + map, Level.DEBUG);
        }
        
        start();
    }

    private void startTownless() {
        townlessRunning = true;
        ModConfig config = ModConfig.instance();

        service.scheduleAtFixedRate(() -> {
            if (townlessRunning && config.townless.enabled && shouldRun()) {
                EMCEssentials.instance().updateTownless();
                Messaging.sendDebugMessage("Updated townless", Level.INFO);
            }
        }, 5, Math.min(config.intervals.townless, 200), TimeUnit.SECONDS);
    }

    private void startNearby() {
        nearbyRunning = true;
        ModConfig config = ModConfig.instance();

        service.scheduleAtFixedRate(() -> {
            if (nearbyRunning && config.nearby.enabled && shouldRun()) {
                boolean updated = EMCEssentials.instance().updateNearbyPlayers();
                if (updated) Messaging.sendDebugMessage("Updated nearby", Level.INFO);
            }
        }, 5, Math.min(config.intervals.nearby, 30), TimeUnit.SECONDS);
    }

//    private void startCacheCheck() {
//        cacheCheckRunning = true;
//
//        // TODO: Caches will usually have different expiry times.
//        //       This means checking on a set schedule is pretty dumb, replace with events or something betta.
//        service.scheduleAtFixedRate(() -> {
//            if (!cacheCheckRunning) return;
//
//            for (SimpleCache<?> cache : CACHES) {
//                if (cache.cacheNeedsUpdate()) {
//                    cache.clearCache();
//                }
//            }
//        }, 0, 3, TimeUnit.MINUTES);
//    }

    boolean shouldRun() {
        boolean modEnabled = ModConfig.instance().general.enableMod;
        boolean focused = MinecraftClient.getInstance().isWindowFocused();

        return modEnabled && focused;
    }
}