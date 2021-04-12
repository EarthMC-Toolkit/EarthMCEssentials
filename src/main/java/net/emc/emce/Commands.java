package net.emc.emce;

import net.emc.emce.commands.*;

public class Commands {
    public static void registerCommands() {
        InfoCommands.registerNationInfoCommand();
        InfoCommands.registerTownInfoCommand();
        NearbyCommand.register();
        NetherCommand.register();
        QueueCommand.register();
        TownlessCommand.register();
        ToggleDebugCommand.register();
    }
}
