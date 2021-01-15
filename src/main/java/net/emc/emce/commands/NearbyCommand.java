package net.emc.emce.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.emc.emce.utils.Timers;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.*;

public class NearbyCommand
{
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(ArgumentBuilders.literal("nearby").executes(c ->
        {
            if (client.player == null) return -1;

            Formatting headingFormatting = Formatting.byName(config.nearby.headingTextColour);
            Formatting textFormatting = Formatting.byName(config.nearby.playerTextColour);

            c.getSource().sendFeedback(new TranslatableText("text_nearby_header", nearby.size()).formatted(headingFormatting));

            for (int i = 0; i < nearby.size(); i++)
            {
                JsonObject currentPlayer = (JsonObject) nearby.get(i);
                int distance = Math.abs(currentPlayer.get("x").getAsInt() - (int) client.player.getX()) +
                               Math.abs(currentPlayer.get("z").getAsInt() - (int) client.player.getZ());

                String prefix = "";

                if (config.nearby.showRank)
                {
                    if (!currentPlayer.has("town")) prefix = "(Townless) ";
                    else prefix = "(" + currentPlayer.get("rank").getAsString() + ") ";
                }

                c.getSource().sendFeedback(new TranslatableText(prefix + currentPlayer.get("name").getAsString() + ": " + distance + "m").formatted(textFormatting));
            }

            return 1;
        }).then(ArgumentBuilders.literal("refresh").executes(c ->
        {
            Timers.restartTimer(Timers.nearbyTimer);
            c.getSource().sendFeedback(new TranslatableText("msg_nearby_refresh"));

            return 1;
        })).then(ArgumentBuilders.literal("clear").executes(c ->
        {
            nearby = new JsonArray();
            c.getSource().sendFeedback(new TranslatableText("msg_nearby_clear"));

            return 1;
        })));
    }
}
