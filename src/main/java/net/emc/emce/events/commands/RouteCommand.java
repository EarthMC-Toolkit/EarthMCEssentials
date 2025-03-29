package net.emc.emce.events.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.emc.emce.utils.Messaging;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class RouteCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return literal("route")
            .then(literal("fastest")
                .then(argument("x", IntegerArgumentType.integer())
                .then(argument("z", IntegerArgumentType.integer()).executes(this::execRouteSafest))
            ).executes(ctx -> {
                Messaging.send("msg_route_err_args");
                return 1;
            }))
            .then(literal("safest")
                .then(argument("x", IntegerArgumentType.integer())
                .then(argument("z", IntegerArgumentType.integer()).executes(this::execRouteFastest))
            ).executes(ctx -> {
                Messaging.send("msg_route_err_args");
                return 1;
            }));
    }
    
    public int execRouteSafest(CommandContext<FabricClientCommandSource> ctx) {
        return execRouteArgs(ctx);
    }
    
    public int execRouteFastest(CommandContext<FabricClientCommandSource> ctx) {
        return execRouteArgs(ctx);
    }
    
    public int execRouteArgs(CommandContext<FabricClientCommandSource> ctx) {
        int x = ctx.getArgument("x", int.class);
        int z = ctx.getArgument("z", int.class);
        
        Messaging.sendRegular(String.format("X: %d Z: %d", x, z));
        
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            Messaging.send("msg_route_err_null");
            return 1;
        }
        
        return 1;
    }
}