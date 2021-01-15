package net.emc.emce.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.Timers;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.*;

public class TownlessCommand
{
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(ArgumentBuilders.literal("townless").executes(c ->
        {
            StringBuilder townlessString = new StringBuilder();
            Formatting townlessTextFormatting = Formatting.byName(config.commands.townlessTextColour);

            for (int i = 0; i < townless.size(); i++)
            {
                JsonObject currentPlayer = (JsonObject) townless.get(i);
                townlessString.append(currentPlayer.get("name").getAsString()).append(", ");
            }

            c.getSource().sendFeedback(new TranslatableText("text_townless_header", townless.size()).formatted(townlessTextFormatting));
            
            if (townless.size() > 0)
                c.getSource().sendFeedback(new TranslatableText(townlessString.toString()).formatted(townlessTextFormatting));
                
            return 1;
        }).then(ArgumentBuilders.literal("inviteAll").executes(c ->
        {
            if (client.player == null) return -1;

            if (ModUtils.getServerName().endsWith("earthmc.net"))
            {
                StringBuilder townlessString = new StringBuilder();

                for (int i = 0; i < townless.size(); i++)
                {
                    JsonObject currentPlayer = (JsonObject) townless.get(i);
                    if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256) break;
                    else townlessString.append(currentPlayer.get("name").getAsString()).append(" ");
                }

                client.player.sendChatMessage("/towny:town invite " + townlessString);

                c.getSource().sendFeedback(new TranslatableText("msg_townless_sent", townless.size()).formatted(Formatting.AQUA));
            }
            else c.getSource().sendFeedback(new TranslatableText("msg_townless_invite_err"));

            return 1;
        })).then(ArgumentBuilders.literal("inviteNearby").executes(c -> {
            if (client.player == null) return -1;

            if (ModUtils.getServerName().endsWith("earthmc.net"))
            {
                StringBuilder townlessString = new StringBuilder();
                int invitesSent = 0;

                for (int i = 0; i < nearbySurrounding.size(); i++)
                {
                    JsonObject currentPlayer = (JsonObject) nearbySurrounding.get(i);

                    // not townless, skip
                    if (currentPlayer.has("town")) continue;

                    if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256)
                        break;
                    else
                        townlessString.append(currentPlayer.get("name").getAsString()).append(" ");
                        invitesSent += 1;
                }

                client.player.sendChatMessage("/towny:town invite " + townlessString);

                c.getSource().sendFeedback(new TranslatableText("msg_townless_sent", Integer.toString(invitesSent)).formatted(Formatting.AQUA));
            }
            else c.getSource().sendFeedback(new TranslatableText("msg_townless_invite_err"));

            return 1;
        })).then(ArgumentBuilders.literal("refresh").executes(c -> {
            Timers.restartTimer(Timers.townlessTimer);
            c.getSource().sendFeedback(new TranslatableText("msg_townless_refresh"));

            return 1;
        })).then(ArgumentBuilders.literal("clear").executes(c -> {
            townless = new JsonArray();
            c.getSource().sendFeedback(new TranslatableText("msg_townless_clear"));

            return 1;
        })));
    }
}
