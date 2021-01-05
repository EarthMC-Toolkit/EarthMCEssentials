package net.earthmc.emc.commands;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.earthmc.emc.EMCMod;
import net.earthmc.emc.utils.TimerTasks;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

public class NearbyCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("nearby").executes(c -> {
            JsonArray nearby = EMCMod.nearby;
            Formatting headingFormatting = Formatting.byName(EMCMod.config.nearby.headingTextColour);
            Formatting textFormatting = Formatting.byName(EMCMod.config.nearby.playerTextColour);

            c.getSource().sendFeedback(new TranslatableText("text_nearby_header", nearby.size()).formatted(headingFormatting));

            for (int i = 0; i < nearby.size(); i++) {
                JsonObject currentPlayer = (JsonObject) nearby.get(i);
                int x = currentPlayer.get("x").getAsInt();
                int y = currentPlayer.get("y").getAsInt();
                int z = currentPlayer.get("z").getAsInt();

                c.getSource().sendFeedback(new LiteralText(currentPlayer.get("name").getAsString() + ": " + x + ", " + y + ", " + z).formatted(textFormatting));
            }
            return Command.SINGLE_SUCCESS;
        }).then(ArgumentBuilders.literal("refresh").executes(c -> {
            TimerTasks.restartTimers();
            c.getSource().sendFeedback(new TranslatableText("msg_nearby_refresh"));
            return Command.SINGLE_SUCCESS;
        })).then(ArgumentBuilders.literal("clear").executes(c -> {
            EMCMod.nearby = new JsonArray();
            c.getSource().sendFeedback(new TranslatableText("msg_nearby_clear"));
            return Command.SINGLE_SUCCESS;
        })));
    }
}
