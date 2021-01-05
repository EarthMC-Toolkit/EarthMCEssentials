package net.earthmc.emc;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.earthmc.emc.commands.InfoCommands;
import net.earthmc.emc.commands.NearbyCommand;
import net.earthmc.emc.commands.NetherCommand;
import net.earthmc.emc.commands.QueueCommand;
import net.earthmc.emc.commands.TownlessCommand;

public class ClientSideCommands implements ClientCommandPlugin {
    
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        TownlessCommand.register(dispatcher);
        NetherCommand.register(dispatcher);
        QueueCommand.register(dispatcher);
        InfoCommands.registerNationInfoCommand(dispatcher);
        InfoCommands.registerTownInfoCommand(dispatcher);
        NearbyCommand.register(dispatcher);
    }
}
