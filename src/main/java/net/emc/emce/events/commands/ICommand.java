package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface ICommand {
    LiteralArgumentBuilder<FabricClientCommandSource> build();
    
    default void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(build());
    }
}