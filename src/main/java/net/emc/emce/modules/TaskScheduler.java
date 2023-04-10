package net.emc.emce.modules;

import net.emc.emce.caches.AllianceDataCache;
import net.emc.emce.caches.Cache;
import net.emc.emce.config.ModConfig;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.github.emcw.utils.GsonUtil.serialize;
import static net.emc.emce.EarthMCEssentials.instance;
import static net.emc.emce.utils.EarthMCAPI.clientOnline;

public class TaskScheduler {
    public boolean townlessRunning, nearbyRunning, cacheCheckRunning;
    public boolean hasMap = false;

    private static final List<Cache<?>> CACHES = List.of(AllianceDataCache.INSTANCE);
    private ScheduledExecutorService service;

    public void start() {
        ModConfig config = ModConfig.instance();

        // Pre-fill data.
        if (config.general.enableMod) {
            if (config.townless.enabled) instance().setTownless(EarthMCAPI.getTownless());
            if (config.nearby.enabled) instance().setNearbyPlayers(EarthMCAPI.getNearby());
        }

        startCacheCheck();
        startTownless();
        startNearby();
    }

    public void stop() {
        townlessRunning = false;
        nearbyRunning = false;
        cacheCheckRunning = false;

        Messaging.sendDebugMessage("Stopping scheduled tasks...");
    }

    public void initMap() {
        service = Executors.newScheduledThreadPool(4);
        service.scheduleAtFixedRate(() -> {
            if (hasMap) return;

            if (clientOnline("aurora")) setHasMap("aurora");
            else if (clientOnline("nova")) setHasMap("nova");
            else setHasMap(null);
        }, 5, 15, TimeUnit.SECONDS);
    }

    public void setHasMap(String map) {
        if (map == null) {
            hasMap = false;
            instance().mapName = "aurora";

            stop();
            Messaging.sendDebugMessage("Player not found on any map!");
        } else {
            hasMap = true;

            start();
            Messaging.sendDebugMessage("Player found on: " + map);
        }
    }

    private void startTownless() {
        townlessRunning = true;
        ModConfig config = ModConfig.instance();

        service.scheduleAtFixedRate(() -> {
            if (townlessRunning && config.townless.enabled && shouldRun()) {
                instance().setTownless(EarthMCAPI.getTownless());
                Messaging.sendDebugMessage("Updating townless...");
            }
        }, 5, Math.min(config.intervals.townless, 200), TimeUnit.SECONDS);
    }

    private void startNearby() {
        nearbyRunning = true;
        ModConfig config = ModConfig.instance();

        service.scheduleAtFixedRate(() -> {
            if (nearbyRunning && config.nearby.enabled && shouldRun()) {
                instance().setNearbyPlayers(EarthMCAPI.getNearby());
                Messaging.sendDebugMessage("Updating nearby...");
            }
        }, 5, Math.min(config.intervals.nearby, 30), TimeUnit.SECONDS);
    }

    private void startCacheCheck() {
        cacheCheckRunning = true;

        service.scheduleAtFixedRate(() -> {
            if (!cacheCheckRunning) return;

            for (Cache<?> cache : CACHES)
                if (cache.needsUpdate())
                    cache.clear();
        }, 0, 5, TimeUnit.MINUTES);
    }

    boolean shouldRun() {
        return ModConfig.instance().general.enableMod && MinecraftClient.getInstance().isWindowFocused();
    }
}