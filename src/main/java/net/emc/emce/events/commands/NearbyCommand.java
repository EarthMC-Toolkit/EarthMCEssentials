package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EarthMCEssentials.instance;

public class NearbyCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("nearby").executes(c -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null)
                return -1;

            Formatting headingFormatting = Formatting.byName(instance().getConfig().nearby.headingTextColour.name());
            Formatting textFormatting = Formatting.byName(instance().getConfig().nearby.playerTextColour.name());

            MsgUtils.sendPlayer("text_nearby_header", false, headingFormatting, false);

            for (int i = 0; i < instance().getNearbyPlayers().size(); i++) {
                JsonObject currentPlayer = instance().getNearbyPlayers().get(i).getAsJsonObject();

                JsonElement xElement = currentPlayer.get("x");
                JsonElement zElement = currentPlayer.get("z");
                if (xElement == null || zElement == null) continue;

                int distance = Math.abs(xElement.getAsInt() - (int) client.player.getX()) +
                               Math.abs(zElement.getAsInt() - (int) client.player.getZ());

                String prefix = "";

                if (instance().getConfig().nearby.showRank) {
                    if (!currentPlayer.has("town")) prefix = new TranslatableText("text_nearby_rank_townless").toString();
                    else prefix = "(" + currentPlayer.get("rank").getAsString() + ") ";
                }

                MsgUtils.sendPlayer(prefix + currentPlayer.get("name").getAsString() + ": " + distance + "m", false, textFormatting, false);
            }

            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c -> {
            instance().scheduler().stop();
            instance().scheduler().start();

            MsgUtils.sendPlayer("msg_nearby_refresh", false, Formatting.AQUA, true);
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance().setNearbyPlayers(new JsonArray());

            MsgUtils.sendPlayer("msg_nearby_clear", false, Formatting.AQUA, true);
            return 1;
        })));
    }
}
