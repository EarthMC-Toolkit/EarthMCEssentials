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

import static net.emc.emce.EarthMCEssentials.instance;
import static net.emc.emce.utils.EarthMCAPI.getTownless;
import static net.emc.emce.utils.EarthMCAPI.playerOnline;

public class TaskScheduler {
    public ScheduledExecutorService service;
    public boolean townlessRunning, nearbyRunning, cacheCheckRunning;
    public boolean hasMap = false;

    private static final List<Cache<?>> CACHES = List.of(AllianceDataCache.INSTANCE);

    public void start() {
        ModConfig config = ModConfig.instance();

        // Pre-fill data.
        if (config.general.enableMod) {
            if (config.townless.enabled) instance().setTownless(getTownless());
            if (config.nearby.enabled) EarthMCAPI.getNearby().thenAccept(instance()::setNearbyPlayers);
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
        initMap(false);
    }

    public void initMap(Boolean singleplayer) {
        service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> {
            if (hasMap) return;

            if (singleplayer || playerOnline("aurora")) {
                setHasMap("aurora");
                return;
            }

            if (playerOnline("nova")) setHasMap("nova");
            else setHasMap(null);
        }, 15, 10, TimeUnit.SECONDS); // Give enough time for Dynmap & Vercel to update.
    }

    public void setHasMap(String map) {
        if (map == null) {
            Messaging.sendDebugMessage("Player not found on any map.");
            stop();

            instance().mapName = "aurora";
            hasMap = false;
        }
        else {
            Messaging.sendDebugMessage("Player found on: " + map);

            hasMap = true;
            start();
        }
    }

    private void startTownless() {
        townlessRunning = true;
        ModConfig config = ModConfig.instance();

        service.scheduleAtFixedRate(() -> {
            if (townlessRunning && config.townless.enabled && shouldRun()) {
                Messaging.sendDebugMessage("Starting townless task.");

                instance().setTownless(getTownless());
                Messaging.sendDebugMessage("Finished townless task.");
            }
        }, 5, Math.max(config.intervals.townless, 200), TimeUnit.SECONDS);
    }

    private void startNearby() {
        nearbyRunning = true;
        final ModConfig config = ModConfig.instance();

        service.scheduleAtFixedRate(() -> {
            if (nearbyRunning && config.nearby.enabled && shouldRun()) {
                Messaging.sendDebugMessage("Starting nearby task.");
                EarthMCAPI.getNearby().thenAccept(nearby -> {
                    instance().setNearbyPlayers(nearby);
                    Messaging.sendDebugMessage("Finished nearby task.");
                });
            }
        }, 5, Math.max(config.intervals.nearby, 30), TimeUnit.SECONDS);
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
