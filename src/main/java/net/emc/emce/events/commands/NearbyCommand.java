package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;

import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;

import io.github.emcw.squaremap.entities.SquaremapResident;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.utils.Translation;

import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.emc.emce.EarthMCEssentials.squaremapPlayerToResident;
import static net.emc.emce.modules.OverlayRenderer.dist;

public record NearbyCommand(EarthMCEssentials instance) {

    public void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("nearby").executes(c -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return -1;

            ModConfig.Nearby nearbyConfig = instance.config().nearby;

            NamedTextColor headingColour = nearbyConfig.headingTextColour.named();
            NamedTextColor textColour = nearbyConfig.playerTextColour.named();

            Messaging.send(Component.text("text_nearby_header", headingColour));

            Map<String, SquaremapOnlinePlayer> nearby = instance.getNearbyPlayers();

            for (SquaremapOnlinePlayer curOp : nearby.values()) {
                Integer x = curOp.getLocation().getX();
                Integer z = curOp.getLocation().getZ();
                if (x == null || z == null) continue;

                int distance = dist(x, z);
                Component prefix = Component.empty();

                if (nearbyConfig.showRank) {
                    SquaremapResident res = squaremapPlayerToResident(instance().getCurrentMap(), curOp);

                    if (res == null) prefix = Translation.of("text_nearby_rank_townless");
                    else {
                        String rankText = String.format("(%s) ", res.getRank());
                        prefix = Component.text(rankText);
                    }
                }

                String formatted = String.format("%s: %dm", curOp.getName(), distance);
                Component comp = Component.empty().append(prefix.append(Component.text(formatted)));

                Messaging.send(comp.color(textColour));
            }
            
            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c -> {
            instance.setNearbyPlayers();
            Messaging.sendPrefixed("msg_nearby_refresh");
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setNearbyPlayers(new HashMap<>());
            Messaging.send("msg_nearby_clear");
            return 1;
        })));
    }
}