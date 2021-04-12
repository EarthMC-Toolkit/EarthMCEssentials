package net.emc.emce.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.emc.emce.utils.MsgUtils;
import net.emc.emce.utils.Timers;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.config;
import static net.emc.emce.EMCE.client;
import static net.emc.emce.EMCE.nearby;

public class NearbyCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("nearby").executes(c -> {
            if (client.player == null) return -1;

            Formatting headingFormatting = Formatting.byName(config.nearby.headingTextColour);
            Formatting textFormatting = Formatting.byName(config.nearby.playerTextColour);

            MsgUtils.sendPlayer("text_nearby_header", false, headingFormatting, false);

            for (int i = 0; i < nearby.size(); i++) {
                JsonObject currentPlayer = (JsonObject) nearby.get(i);
                int distance = Math.abs(currentPlayer.get("x").getAsInt() - (int) client.player.getX()) +
                               Math.abs(currentPlayer.get("z").getAsInt() - (int) client.player.getZ());

                String prefix = "";

                if (config.nearby.showRank) {
                    if (!currentPlayer.has("town")) prefix = new TranslatableText("text_nearby_rank_townless").toString();
                    else prefix = "(" + currentPlayer.get("rank").getAsString() + ") ";
                }

                MsgUtils.sendPlayer(prefix + currentPlayer.get("name").getAsString() + ": " + distance + "m", false, textFormatting, false);
            }

            return 1;
        }).then(ClientCommandManager.literal("refresh").executes(c ->
        {
            Timers.restartTimer(Timers.nearbyTimer);
            MsgUtils.sendPlayer("msg_nearby_refresh", false, Formatting.AQUA, true);
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c ->
        {
            nearby = new JsonArray();
            MsgUtils.sendPlayer("msg_nearby_clear", false, Formatting.AQUA, true);
            return 1;
        })));
    }
}
