package net.emc.emce.tasks;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.MsgUtils;

public class NearbyTask extends Thread {
    public NearbyTask() {
        super();
    }

    @Override
    public void run() {
        MsgUtils.sendDebugMessage("Starting NearbyTask.");

        EarthMCEssentials.setNearbyPlayers(EarthMCAPI.getNearby(EarthMCEssentials.getConfig().nearby.xBlocks, EarthMCEssentials.getConfig().nearby.zBlocks));

        MsgUtils.sendDebugMessage("Finished NearbyTask.");
    }
}
