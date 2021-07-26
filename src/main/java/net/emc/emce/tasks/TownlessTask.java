package net.emc.emce.tasks;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.MsgUtils;

public class TownlessTask extends Thread {
    public TownlessTask() {
        super();
    }

    @Override
    public void run() {
        MsgUtils.sendDebugMessage("Starting TownlessTask.");

        EarthMCEssentials.setTownlessResidents(EarthMCAPI.getTownless());

        MsgUtils.sendDebugMessage("Finished TownlessTask.");
    }
}
