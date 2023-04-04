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
import static net.emc.emce.utils.EarthMCAPI.*;

public class TaskScheduler {
    public ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    public boolean townlessRunning, nearbyRunning, cacheCheckRunning;
    public boolean hasMap = false;

    private static final List<Cache<?>> CACHES = List.of(AllianceDataCache.INSTANCE);

    public void start() {
        ModConfig config = ModConfig.instance();

        // Pre-fill data.
        if (config.general.enableMod) {
            if (config.townless.enabled) instance().setTownless(getTownless());
            if (config.nearby.enabled) instance().setNearbyPlayers(getNearby());
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
        service.scheduleAtFixedRate(() -> {
            if (hasMap) return;

            if (clientOnline("aurora")) setHasMap("aurora");
            if (clientOnline("nova")) setHasMap("nova");
            else setHasMap(null);
        }, 10, 5, TimeUnit.SECONDS);
    }

    public void setHasMap(String map) {
        hasMap = map != null;

        if (map == null) {
            stop();
            Messaging.sendDebugMessage("Player not found on any map!");
        } else {
            start();
            Messaging.sendDebugMessage("Player found on: " + map);
        }
    }

    private void startTownless() {
        townlessRunning = true;

        service.scheduleAtFixedRate(() -> {
            var config = instance().config();
            if (townlessRunning && config.townless.enabled && shouldRun()) {
                Messaging.sendDebugMessage("Starting townless task.");
                instance().setTownless(getTownless());
                Messaging.sendDebugMessage("Finished townless task.");
            }
        }, 5, Math.max(instance().config().intervals.townless, 200), TimeUnit.SECONDS);
    }

    private void startNearby() {
        nearbyRunning = true;

        service.scheduleAtFixedRate(() -> {
            var config = instance().config();
            if (nearbyRunning && config.nearby.enabled && shouldRun()) {
                Messaging.sendDebugMessage("Starting nearby task.");
                instance().setNearbyPlayers(EarthMCAPI.getNearby());
                Messaging.sendDebugMessage("Finished nearby task.");
            }
        }, 5, Math.max(instance().config().intervals.nearby, 30), TimeUnit.SECONDS);
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
