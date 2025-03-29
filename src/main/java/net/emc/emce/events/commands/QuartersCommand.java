package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import io.github.emcw.KnownMap;

import net.emc.emce.EMCEssentials;
import net.emc.emce.objects.Quarter;
import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.api.OAPIV3;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.List;
import java.util.stream.Collectors;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class QuartersCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        // QFS = Quarters For Sale
        return literal("qfs")
            .then(literal("town")
                .then(argument("name", StringArgumentType.string()).executes(this::execQfsTown))
            )
            .then(literal("nation")
                .then(argument("name", StringArgumentType.string()).executes(this::execQfsNation))
            );
    }
    
    public int execQfsTown(CommandContext<FabricClientCommandSource> ctx) {
        String townName = ctx.getArgument("name", String.class);

        // Handles errors and messaging itself.
        List<Quarter> townQuarters = getQuartersFromTown(townName);
        if (townQuarters != null) {
            townQuarters.forEach(q ->
                Messaging.sendRegular(q.name + " | " + q.type + " | Owned by: " + q.owner.getName() + "For sale: " + q.isForSale)
            );
            //townQuarters.stream().filter(q -> q.isForSale);
        }
        
        return 1;
    }
    
    public int execQfsNation(CommandContext<FabricClientCommandSource> ctx) {
        String nationName = ctx.getArgument("name", String.class);
        
        // Handles errors and messaging itself.
        List<Quarter> nationQuarters = getQuartersFromNation(nationName);
        if (nationQuarters != null) {
        
        }
        
        return 1;
    }
    
    public List<Quarter> getQuartersFromTown(String name) {
        KnownMap curMap = EMCEssentials.instance().currentMap;
        JsonArray towns = OAPIV3.getTowns(curMap, new String[]{ name });
        if (towns == null) {
            TextComponent errComp = Component.text("[qfs] Failed to get town: ", NamedTextColor.RED)
                .append(Component.text(name, NamedTextColor.GOLD))
                .append(Component.text(". Check the debug logs to see the cause.", NamedTextColor.RED));
            
            Messaging.send(errComp);
            return null;
        }
        
        JsonObject town = towns.get(0).getAsJsonObject();
        return tryGetQuarters(town);
    }
    
    public List<Quarter> getQuartersFromNation(String name) {
        KnownMap curMap = EMCEssentials.instance().currentMap;
        JsonArray nations = OAPIV3.getNations(curMap, new String[]{ name });
        if (nations == null) {
            TextComponent errComp = Component.text("[qfs] Failed to get nation: ", NamedTextColor.RED)
                .append(Component.text(name, NamedTextColor.GOLD))
                .append(Component.text(". Check the debug logs to see the cause.", NamedTextColor.RED));
            
            Messaging.send(errComp);
            return null;
        }
        
        JsonObject nation = nations.get(0).getAsJsonObject();
        return tryGetQuarters(nation);
    }
    
    List<Quarter> tryGetQuarters(JsonObject obj) {
        JsonArray quarterObjs = obj.get("quarters").getAsJsonArray();
        String[] uuids = quarterObjs.asList().stream()
            .map(q -> q.getAsJsonObject().get("uuid").getAsString())
            .toList().toArray(String[]::new);
        
        JsonArray quarters = OAPIV3.getQuarters(EMCEssentials.instance().currentMap, uuids);
        if (quarters == null) {
            Messaging.send(Component.text(
                "[qfs] Command failed. Check the debug logs to see the cause.",
                NamedTextColor.RED
            ));
            
            return null;
        }
        
        return quarters.asList().stream()
            .map(q -> new Quarter(q.getAsJsonObject()))
            .collect(Collectors.toList());
    }
}