package net.emc.emce.tasks;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    public ScheduledExecutorService service;
    public boolean townlessRunning;
    public boolean nearbyRunning;
    public boolean serverDataRunning;
    public boolean townyDataRunning;

    public void start() {
        service = Executors.newScheduledThreadPool(1);

        startTownless();
        startNearby();
        startServerData();
        startTownyData();
    }

    public void stop() {
        service.shutdown();

        townlessRunning = false;
        nearbyRunning = false;
        serverDataRunning = false;
        townyDataRunning = false;
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

    private void startServerData() {
        serverDataRunning = true;

        service.scheduleAtFixedRate(() -> {
            if (serverDataRunning && EarthMCEssentials.instance().getConfig().general.enableMod && shouldRun()) {
                MsgUtils.sendDebugMessage("Starting server data task.");
                EarthMCAPI.getServerData().thenAccept(serverData -> {
                    EarthMCEssentials.instance().setServerData(serverData);
                    MsgUtils.sendDebugMessage("Finished server data task.");
                });
            }
        }, 0, Math.max(EarthMCEssentials.instance().getConfig().api.serverDataInterval, 90), TimeUnit.SECONDS);
    }

    private void startTownyData() {
        townyDataRunning = true;

        service.scheduleAtFixedRate(() -> {
            if (townyDataRunning && EarthMCEssentials.instance().getConfig().general.enableMod && shouldRun()) {
                EarthMCAPI.getTowns().thenAccept(EarthMCEssentials.instance()::setTowns);
                EarthMCAPI.getNations().thenAccept(EarthMCEssentials.instance()::setNations);

                if (MinecraftClient.getInstance().player != null)
                    EarthMCAPI.getResident(MinecraftClient.getInstance().player.getName().asString()).thenAccept(EarthMCEssentials.instance()::setClientResident);
            }
        }, 0, Math.max(EarthMCEssentials.instance().getConfig().api.townyDataInterval, 90), TimeUnit.SECONDS);
    }

    private boolean shouldRun() {
        // Only run if the game isn't paused and the window is focused.
        return !MinecraftClient.getInstance().isPaused() && MinecraftClient.getInstance().isWindowFocused();
    }
}
