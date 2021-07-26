package net.emc.emce.tasks;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.MsgUtils;

public class ServerDataTask extends Thread {
    public ServerDataTask() {
        super();
    }

    @Override
    public void run() {
        MsgUtils.sendDebugMessage("Starting ServerDataTask.");

        EarthMCEssentials.setServerData(EarthMCAPI.getServerData());

        MsgUtils.sendDebugMessage("Finished ServerDataTask.");
    }
}
