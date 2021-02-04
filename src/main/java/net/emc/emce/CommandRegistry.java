package net.emc.emce;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.emc.emce.commands.InfoCommands;
import net.emc.emce.commands.NearbyCommand;
import net.emc.emce.commands.NetherCommand;
import net.emc.emce.commands.QueueCommand;
import net.emc.emce.commands.TownlessCommand;

public class CommandRegistry implements ClientCommandPlugin
{
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
