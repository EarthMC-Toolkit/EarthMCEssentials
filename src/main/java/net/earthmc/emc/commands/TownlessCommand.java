package net.earthmc.emc.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.earthmc.emc.EMCMod.*;
import static net.earthmc.emc.utils.Timers.*;

public class TownlessCommand
{
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(ArgumentBuilders.literal("townless").executes(c ->
        {
            StringBuilder townlessString = new StringBuilder();
            Formatting headingFormatting = Formatting.byName(config.townless.headingTextColour);
            Formatting playerNameFormatting = Formatting.byName(config.townless.playerTextColour);

            for (int i = 0; i < townless.size(); i++)
            {
                JsonObject currentPlayer = (JsonObject) townless.get(i);
                townlessString.append(currentPlayer.get("name").getAsString()).append(", ");
            }

            c.getSource().sendFeedback(new TranslatableText("text_townless_header", townless.size()).formatted(headingFormatting));
            
            if (townless.size() > 0)
                c.getSource().sendFeedback(new TranslatableText(townlessString.toString()).formatted(playerNameFormatting));
                
            return 1;
        }).then(ArgumentBuilders.literal("inviteAll").executes(c ->
        {
            StringBuilder townlessString = new StringBuilder();

            for (int i = 0; i < townless.size(); i++)
            {
                JsonObject currentPlayer = (JsonObject) townless.get(i);
                if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256) break;
                else townlessString.append(currentPlayer.get("name").getAsString()).append(" ");
            }

            if (client.player == null) return -1;
            else {
                client.player.sendChatMessage("/towny:town invite " + townlessString);

                c.getSource().sendFeedback(new TranslatableText("msg_townless_sent"));
                c.getSource().sendFeedback(new TranslatableText("msg_townless_permissions"));

                return 1;
            }
        })).then(ArgumentBuilders.literal("refresh").executes(c -> {
            restartTimer(townlessTimer);
            c.getSource().sendFeedback(new TranslatableText("msg_townless_refresh"));

            return 1;
        })).then(ArgumentBuilders.literal("clear").executes(c -> {
            townless = new JsonArray();
            c.getSource().sendFeedback(new TranslatableText("msg_townless_clear"));

            return 1;
        })));
    }
}
