package net.emc.emce.tasks;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.caches.Cache;
import net.emc.emce.caches.NationDataCache;
import net.emc.emce.caches.ServerDataCache;
import net.emc.emce.caches.TownDataCache;
import net.emc.emce.config.ModConfig;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    public ScheduledExecutorService service;
    public boolean townlessRunning;
    public boolean nearbyRunning;
    public boolean cacheCheckRunning;
    private static final List<Cache<?>> CACHES = Arrays.asList(NationDataCache.INSTANCE, ServerDataCache.INSTANCE, TownDataCache.INSTANCE);

    public void start() {
        service = Executors.newScheduledThreadPool(1);

        startTownless();
        startNearby();
        startCacheCheck();

        // Pre-fill townless and nearby player arrays with some data.
        if (EarthMCEssentials.instance().getConfig().general.enableMod) {
            if (ModConfig.instance().townless.enabled)
                EarthMCAPI.getTownless().thenAccept(EarthMCEssentials.instance()::setTownlessResidents);
            if (ModConfig.instance().nearby.enabled)
                EarthMCAPI.getNations().thenAccept(EarthMCEssentials.instance()::setNearbyPlayers);
        }
    }

    public void stop() {
        service.shutdown();

        townlessRunning = false;
        nearbyRunning = false;
        cacheCheckRunning = false;
    }

    private void startTownless() {
        townlessRunning = true;

        service.scheduleAtFixedRate(() -> {
            if (townlessRunning && EarthMCEssentials.instance().getConfig().general.enableMod && EarthMCEssentials.instance().getConfig().townless.enabled && shouldRun()) {
                MsgUtils.sendDebugMessage("Starting townless task.");
                EarthMCAPI.getTownless().thenAccept(townless -> {
                    EarthMCEssentials.instance().setTownlessResidents(townless);
                    MsgUtils.sendDebugMessage("Finished townless task.");
                });
            }
        }, 0, Math.max(EarthMCEssentials.instance().getConfig().api.townlessInterval, 30), TimeUnit.SECONDS);
    }

    private void startNearby() {
        nearbyRunning = true;

        service.scheduleAtFixedRate(() -> {
            if (nearbyRunning && ModUtils.isConnectedToEMC() && EarthMCEssentials.instance().getConfig().general.enableMod && EarthMCEssentials.instance().getConfig().nearby.enabled && shouldRun()) {
                MsgUtils.sendDebugMessage("Starting nearby task.");
                EarthMCAPI.getNearby().thenAccept(nearby -> {
                    EarthMCEssentials.instance().setNearbyPlayers(nearby);
                    MsgUtils.sendDebugMessage("Finished nearby task.");
                });
            }
        }, 0, Math.max(EarthMCEssentials.instance().getConfig().api.nearbyInterval, 15), TimeUnit.SECONDS);
    }

    private void startCacheCheck() {
        cacheCheckRunning = true;

        service.scheduleAtFixedRate(() -> {
            for (Cache<?> cache : CACHES)
                if (cache.needsUpdate())
                    cache.clearCache();
        }, 0, 5, TimeUnit.MINUTES);
    }

    private boolean shouldRun() {
        // Only run if the game isn't paused and the window is focused.
        return !MinecraftClient.getInstance().isPaused() && MinecraftClient.getInstance().isWindowFocused();
    }
}
