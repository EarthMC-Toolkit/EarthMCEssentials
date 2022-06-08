package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.Translation;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

public record NearbyCommand(EarthMCEssentials instance) {

    public void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("nearby").executes(c -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null)
                return -1;

            NamedTextColor headingColour = instance.getConfig().nearby.headingTextColour.named();
            NamedTextColor textColour = instance.getConfig().nearby.playerTextColour.named();

            Messaging.send(Component.text("text_nearby_header", headingColour));

            for (int i = 0; i < instance.getNearbyPlayers().size(); i++) {
                JsonObject currentPlayer = instance.getNearbyPlayers().get(i).getAsJsonObject();

                JsonElement xElement = currentPlayer.get("x");
                JsonElement zElement = currentPlayer.get("z");
                if (xElement == null || zElement == null) continue;

                int distance = Math.abs(xElement.getAsInt() - (int) client.player.getX()) +
                        Math.abs(zElement.getAsInt() - (int) client.player.getZ());

                Component prefix = Component.empty();

                if (instance.getConfig().nearby.showRank) {
                    if (!currentPlayer.has("town"))
                        prefix = Translation.of("text_nearby_rank_townless");
                    else
                        prefix = Component.text("(" + currentPlayer.get("rank").getAsString() + ") ");
                }

                Messaging.send(Component.empty().append(
                        prefix.append(Component.text(currentPlayer.get("name").getAsString() + ": " + distance + "m")
                              .color(textColour))));
            }

            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c -> {
            EarthMCAPI.getNearby().thenAccept(instance::setNearbyPlayers);
            Messaging.sendPrefixed(Translation.of("msg_nearby_refresh"));
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setNearbyPlayers(new JsonArray());
            Messaging.send(Translation.of("msg_nearby_clear"));
            return 1;
        })));
    }
}
