package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.emcw.entities.Player;
import io.github.emcw.entities.Resident;
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

import java.util.Map;

public record NearbyCommand(EarthMCEssentials instance) {

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("nearby").executes(c -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return -1;

            ModConfig.Nearby nearbyConfig = instance.config().nearby;

            NamedTextColor headingColour = nearbyConfig.headingTextColour.named();
            NamedTextColor textColour = nearbyConfig.playerTextColour.named();

            Messaging.send(Component.text("text_nearby_header", headingColour));

            Map<String, Player> nearby = instance.getNearbyPlayers();

            for (Player curPlayer : nearby.values()) {
                Integer x = curPlayer.getLocation().getX();
                Integer z = curPlayer.getLocation().getZ();
                if (x == null || z == null) continue;

                int distance = Math.abs(x - (int) client.player.getX()) +
                               Math.abs(z - (int) client.player.getZ());

                Component prefix = Component.empty();
                if (nearbyConfig.showRank) {
                    if (!curPlayer.isResident()) prefix = Translation.of("text_nearby_rank_townless");
                    else {
                        Resident res = (Resident) curPlayer;
                        prefix = Component.text("(" + res.getRank() + ") ");
                    }
                }

                String str = curPlayer.getName() + ": " + distance + "m";
                Component comp = Component.empty().append(prefix.append(Component.text(str)));

                Messaging.send(comp.color(textColour));
            }
            
            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c -> {
            EarthMCAPI.getNearby().thenAccept(instance::setNearbyPlayers);
            Messaging.sendPrefixed("msg_nearby_refresh");
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setNearbyPlayers(Map.of());
            Messaging.send("msg_nearby_clear");
            return 1;
        })));
    }
}
