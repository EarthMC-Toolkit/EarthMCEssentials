package net.emc.emce.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.emc.emce.PlayerMessaging;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.Timers;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.townless;
import static net.emc.emce.EMCE.config;
import static net.emc.emce.EMCE.client;

public class TownlessCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("townless").executes(c -> {
            StringBuilder townlessString = new StringBuilder();
            Formatting townlessTextFormatting = Formatting.byName(config.commands.townlessTextColour);

            for (int i = 0; i < townless.size(); i++) {
                JsonObject currentPlayer = (JsonObject) townless.get(i);
                townlessString.append(currentPlayer.get("name").getAsString()).append(", ");
            }

            c.getSource().sendFeedback(new TranslatableText("text_townless_header", townless.size()).formatted(townlessTextFormatting));
            
            if (townless.size() > 0)
                c.getSource().sendFeedback(new TranslatableText(townlessString.toString()).formatted(townlessTextFormatting));
                
            return 1;
        }).then(ArgumentBuilders.literal("inviteAll").executes(c -> {
            if (client.player == null) return -1;

            if (ModUtils.getServerName().endsWith("earthmc.net")) {
                StringBuilder townlessString = new StringBuilder();

                for (int i = 0; i < townless.size(); i++) {
                    JsonObject currentPlayer = (JsonObject) townless.get(i);
                    if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256) break;
                    else townlessString.append(currentPlayer.get("name").getAsString()).append(" ");
                }

                client.player.sendChatMessage("/towny:town invite " + townlessString);

                c.getSource().sendFeedback(new TranslatableText("msg_townless_sent", townless.size()).formatted(Formatting.AQUA));
            } else
                PlayerMessaging.sendMessage("msg_townless_invite_err", Formatting.RED, true);
            return 1;
        })).then(ArgumentBuilders.literal("refresh").executes(c -> {
            Timers.restartTimer(Timers.townlessTimer);
            PlayerMessaging.sendMessage("msg_townless_refresh", Formatting.BLUE, true);
            return 1;
        })).then(ArgumentBuilders.literal("clear").executes(c -> {
            townless = new JsonArray();
            PlayerMessaging.sendMessage("msg_townless_clear", Formatting.BLUE, true);
            return 1;
        })));
    }
}
