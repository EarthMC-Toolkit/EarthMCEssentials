package net.earthmc.emc.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.earthmc.emc.EMCMod;
import net.earthmc.emc.utils.TimerTasks;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class TownlessCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("townless").executes(c -> {
            final JsonArray townless = EMCMod.townless;
            StringBuilder townlessString = new StringBuilder();
            Formatting headingFormatting = Formatting.byName(EMCMod.config.townless.headingTextColour);
            Formatting playerNameFormatting = Formatting.byName(EMCMod.config.townless.playerTextColour);

            for (int i = 0; i < townless.size(); i++) {
                JsonObject currentPlayer = (JsonObject) townless.get(i);
                townlessString.append(currentPlayer.get("name").getAsString()).append(", ");
            }

            c.getSource().sendFeedback(new TranslatableText("text_townless_header", townless.size()).formatted(headingFormatting));
            
            if (townless.size() > 0)
                c.getSource().sendFeedback(new TranslatableText(townlessString.toString()).formatted(playerNameFormatting));
                
            return Command.SINGLE_SUCCESS;
        }).then(ArgumentBuilders.literal("inviteAll").executes(c -> {
            final JsonArray townless = EMCMod.townless;
            StringBuilder townlessString = new StringBuilder();

            for (int i = 0; i < townless.size(); i++) {
                JsonObject currentPlayer = (JsonObject) townless.get(i);
                if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256) break;
                else townlessString.append(currentPlayer.get("name").getAsString()).append(" ");
            }

            if (EMCMod.client.player != null) 
                EMCMod.client.player.sendChatMessage("/towny:town invite " + townlessString);
                
            c.getSource().sendFeedback(new TranslatableText("msg_townless_sent"));
            c.getSource().sendFeedback(new TranslatableText("msg_townless_permissions"));
            return Command.SINGLE_SUCCESS;
        })).then(ArgumentBuilders.literal("refresh").executes(c -> {
            TimerTasks.restartTimers();
            c.getSource().sendFeedback(new TranslatableText("msg_townless_refresh"));
            return Command.SINGLE_SUCCESS;
        })).then(ArgumentBuilders.literal("clear").executes(c -> {
            EMCMod.townless = new JsonArray();
            c.getSource().sendFeedback(new TranslatableText("msg_townless_clear"));
            return Command.SINGLE_SUCCESS;
        })));
    }
}
