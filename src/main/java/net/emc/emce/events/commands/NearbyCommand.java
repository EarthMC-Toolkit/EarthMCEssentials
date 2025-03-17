package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;

import io.github.emcw.squaremap.entities.SquaremapResident;
import net.emc.emce.EMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.utils.Translation;

import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.HashMap;
import java.util.Map;

import static net.emc.emce.EMCEssentials.squaremapPlayerToResident;
import static net.emc.emce.modules.OverlayRenderer.distFromClientPlayer;

public record NearbyCommand(EMCEssentials instance) implements ICommand {
    TextComponent whiteText(int size) { return Component.text(size).color(NamedTextColor.WHITE); }
    
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("nearby").executes(ctx -> execNearby())
            .then(ClientCommandManager.literal("refresh").executes(ctx -> execNearbyRefresh()))
            .then(ClientCommandManager.literal("clear").executes(ctx -> execNearbyClear()));
    }
    
    public int execNearby() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        
        ModConfig.Nearby nearbyConfig = instance.config().nearby;
        NamedTextColor headingColour = nearbyConfig.headingTextColour.named();
        NamedTextColor textColour = nearbyConfig.playerTextColour.named();
        
        Map<String, SquaremapOnlinePlayer> nearby = instance.getNearbyPlayers();
        if (nearby.isEmpty()) {
            instance.updateNearbyPlayers();
        }
        
        int size = nearby.size();
        Messaging.send(Messaging.create("text_nearby_header", headingColour, whiteText(size)));
        
        for (SquaremapOnlinePlayer curOp : nearby.values()) {
            Integer x = curOp.getLocation().getX();
            Integer z = curOp.getLocation().getZ();
            if (x == null || z == null) continue;
            
            int distance = distFromClientPlayer(x, z);
            Component prefix = Component.empty();
            
            if (nearbyConfig.showRank) {
                SquaremapResident res = squaremapPlayerToResident(instance.getCurrentMap(), curOp);
                if (res != null) {
                    String rankText = String.format("(%s) ", res.getRank());
                    prefix = Component.text(rankText);
                } else {
                    prefix = Translation.of("text_nearby_rank_townless");
                }
            }
            
            String formatted = String.format("%s: %dm", curOp.getName(), distance);
            Component comp = Component.empty().append(prefix.append(Component.text(formatted)));
            
            Messaging.send(comp.color(textColour));
        }
        
        return 1;
    }
    
    public int execNearbyRefresh() {
        instance.updateNearbyPlayers();
        Messaging.sendPrefixed("msg_nearby_refresh");
        return 1;
    }
    
    public int execNearbyClear() {
        instance.setNearbyPlayers(new HashMap<>());
        Messaging.send("msg_nearby_clear");
        return 1;
    }
}