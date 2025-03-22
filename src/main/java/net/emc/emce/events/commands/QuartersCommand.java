package net.emc.emce.events.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class QuartersCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        // QFS = Quarters For Sale
        return literal("qfs")
            .then(literal("town")
                .then(argument("name", StringArgumentType.string()))
                .executes(this::execQfsTown)
            )
            .then(literal("nation")
                .then(argument("name", StringArgumentType.string()))
                .executes(this::execQfsNation)
            );
    }
    
    public int execQfsTown(CommandContext<FabricClientCommandSource> ctx) {
        String townName = ctx.getArgument("name", String.class);
        
        return 1;
    }
    
    public int execQfsNation(CommandContext<FabricClientCommandSource> ctx) {
        String nationName = ctx.getArgument("name", String.class);
        
        return 1;
    }
}