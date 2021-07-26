package net.emc.emce.tasks;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.MsgUtils;

/**
 * Updates town, nation and resident data.
 */
public class TownyDataTask extends Thread {
    public TownyDataTask() {
        super();
    }

    @Override
    public void run() {
        MsgUtils.sendDebugMessage("Starting TownyDataTask.");

        EarthMCEssentials.setClientResident(EarthMCAPI.getResident(EarthMCEssentials.getClient().player.getName().asString()));

        EarthMCEssentials.setTowns(EarthMCAPI.getTowns());
        EarthMCEssentials.setNations(EarthMCAPI.getNations());

        MsgUtils.sendDebugMessage("Finished TownyDataTask.");
    }
}
