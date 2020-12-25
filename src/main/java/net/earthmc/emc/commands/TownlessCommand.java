package net.earthmc.emc.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.earthmc.emc.EMCMod;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class TownlessCommand 
{
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(ArgumentBuilders.literal("townless").executes
        (            
            source -> 
            {
                final JsonArray townless = EMCMod.townless;
                String townlessString = "";
                Formatting headingFormatting = Formatting.byName(EMCMod.config.townless.headingTextColour);
                Formatting playerNameFormattting = Formatting.byName(EMCMod.config.townless.playerTextColour);

                for (int i = 0; i < townless.size(); i++)
                {
                    JsonObject currentPlayer = (JsonObject) townless.get(i);
                    townlessString += currentPlayer.get("name").getAsString() + " ";
                }

                if (townlessString.length() != 0) {
                    source.getSource().sendFeedback(new TranslatableText("Townless Players [" + townless.size() + "]").formatted(headingFormatting));
                    source.getSource().sendFeedback(new TranslatableText(townlessString).formatted(playerNameFormattting));
                } else {
                    source.getSource().sendFeedback(new TranslatableText("There don't seem to be any townless players online at the moment.").formatted(Formatting.byName("RED")));
                }
                
                return Command.SINGLE_SUCCESS;
            }
        ).then(ArgumentBuilders.literal("inviteAll").executes
        (
            source -> 
            {
                final JsonArray townless = EMCMod.townless;
                String townlessString = "";

                for (int i = 0; i < townless.size(); i++)
                {
                    JsonObject currentPlayer = (JsonObject) townless.get(i);
                    if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256) break;
                    else townlessString += currentPlayer.get("name").getAsString() + " ";
                }

                EMCMod.client.player.sendChatMessage("/towny:town invite " + townlessString);
                source.getSource().sendFeedback(new TranslatableText("EMCE > Invites sent!").formatted(Formatting.byName("AQUA")));
                source.getSource().sendFeedback(new TranslatableText("EMCE > Note: You still need permissions to invite players to your town.").formatted(Formatting.byName("RED")));
                return Command.SINGLE_SUCCESS;
            }
        )));
    }
}
