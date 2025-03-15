package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.emc.emce.EMCEssentials;
import net.emc.emce.utils.CustomAPI;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record NewsCommand(EMCEssentials instance) implements ICommand {
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("news")
            .then(ClientCommandManager.literal("latest")).executes(c -> execNewsLatest())
            .then(ClientCommandManager.literal("summary")).executes(c -> execNewsSummary())
            .then(ClientCommandManager.argument("", StringArgumentType.string())
                .executes(c -> execNewsLatest())
            );
    }
    
    // Prints the latest message and the date (x days ago) it was send at.
    public int execNewsLatest() {
        JsonArray news = CustomAPI.getNews();
        
        
        
        return 1;
    }
    
    // Prints every message on a new line with its respective date since.
    public int execNewsSummary() {
    
    }
}