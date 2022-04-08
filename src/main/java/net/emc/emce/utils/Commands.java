package net.emc.emce.utils;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.events.commands.*;

public class Commands {
    public static void register(EarthMCEssentials instance) {
        // Register client-sided commands.
        new InfoCommands(instance).register();
        new NearbyCommand(instance).register();
        new NetherCommand().register();
        new QueueCommand().register();
        new ToggleDebugCommand().register();
        new TownlessCommand(instance).register();
        new AllianceCommand(instance).register();
    }
}