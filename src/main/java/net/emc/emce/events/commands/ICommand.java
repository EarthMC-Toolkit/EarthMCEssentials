package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

// Fabric Commands: https://wiki.fabricmc.net/tutorial:commands
// Brigadier Examples: https://docs.papermc.io/paper/dev/command-api/basics/introduction
public interface ICommand {
    LiteralArgumentBuilder<FabricClientCommandSource> build();
    
    default void registerSelf(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(build());
    }
}