package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
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

public record NearbyCommand(EarthMCEssentials instance) {

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("nearby").executes(c -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return -1;

            ModConfig.Nearby nearbyConfig = instance.getConfig().nearby;

            NamedTextColor headingColour = nearbyConfig.headingTextColour.named();
            NamedTextColor textColour = nearbyConfig.playerTextColour.named();

            Messaging.send(Component.text("text_nearby_header", headingColour));

            JsonArray nearby = instance.getNearbyPlayers();
            int size = nearby.size();

            for (int i = 0; i < size; i++) {
                JsonObject currentPlayer = nearby.get(i).getAsJsonObject();

                JsonElement xElement = currentPlayer.get("x");
                JsonElement zElement = currentPlayer.get("z");
                if (xElement == null || zElement == null) continue;

                int distance = Math.abs(xElement.getAsInt() - (int) client.player.getX()) +
                        Math.abs(zElement.getAsInt() - (int) client.player.getZ());

                Component prefix = Component.empty();
                if (nearbyConfig.showRank) {
                    if (!currentPlayer.has("town")) prefix = Translation.of("text_nearby_rank_townless");
                    else prefix = Component.text("(" + currentPlayer.get("rank").getAsString() + ") ");
                }

                String str = currentPlayer.get("name").getAsString() + ": " + distance + "m";
                Component comp = Component.empty().append(prefix.append(Component.text(str)));

                Messaging.send(comp.color(textColour));
            }

            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c -> {
            EarthMCAPI.getNearby().thenAccept(instance::setNearbyPlayers);
            Messaging.sendPrefixed("msg_nearby_refresh");
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setNearbyPlayers(new JsonArray());
            Messaging.send("msg_nearby_clear");
            return 1;
        })));
    }
}
