package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.emcw.entities.Player;
import io.github.emcw.entities.Resident;
import io.github.emcw.exceptions.MissingEntryException;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.utils.Translation;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

            Map<String, Player> nearby = instance.getNearbyPlayers();

            for (Player curPlayer : nearby.values()) {
                Integer x = curPlayer.getLocation().getX();
                Integer z = curPlayer.getLocation().getZ();
                if (x == null || z == null) continue;

                int distance = dist(x, z);
                Component prefix = Component.empty();

                if (nearbyConfig.showRank) {
                    if (!curPlayer.isResident()) prefix = Translation.of("text_nearby_rank_townless");
                    else {
                        Resident res;
                        try {
                            res = curPlayer.asResident(instance.mapName);
                        } catch (MissingEntryException e) {
                            continue;
                        }

                        prefix = Component.text("(" + res.getRank() + ") ");
                    }
                }

                String str = curPlayer.getName() + ": " + distance + "m";
                Component comp = Component.empty().append(prefix.append(Component.text(str)));

                Messaging.send(comp.color(textColour));
            }
            
            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c -> {
            instance.setNearbyPlayers(EarthMCAPI.getNearby());
            Messaging.sendPrefixed("msg_nearby_refresh");
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setNearbyPlayers(Map.of());
            Messaging.send("msg_nearby_clear");
            return 1;
        })));
    }
}