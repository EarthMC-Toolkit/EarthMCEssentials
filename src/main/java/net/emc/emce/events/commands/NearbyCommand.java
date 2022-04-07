package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.Translation;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.client.MinecraftClient;

public record NearbyCommand(EarthMCEssentials instance) {

    public void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("nearby").executes(c -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null)
                return -1;

            TextColor headingColor = instance.getConfig().nearby.headingTextColour.named();
            TextColor textColor = instance.getConfig().nearby.playerTextColour.named();

            Messaging.sendMessage(Translation.of("text_nearby_header").color(headingColor));

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

                Messaging.sendMessage(Component.empty().append(prefix.append(Component.text(currentPlayer.get("name").getAsString() + ": " + distance + "m").color(textColor))));
            }

            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c -> {
            instance.scheduler().stop();
            instance.scheduler().start();

            Messaging.sendMessage(Translation.of("msg_nearby_refresh"));
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setNearbyPlayers(new JsonArray());

            Messaging.sendMessage(Translation.of("msg_nearby_clear"));
            return 1;
        })));
    }
}
