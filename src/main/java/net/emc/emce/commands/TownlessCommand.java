package net.emc.emce.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.emc.emce.utils.Timers;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.*;

public class TownlessCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("townless").executes(c -> {
            StringBuilder townlessString = new StringBuilder();
            Formatting townlessTextFormatting = Formatting.byName(config.commands.townlessTextColour);

            for (int i = 0; i < townless.size(); i++) {
                JsonObject currentPlayer = (JsonObject) townless.get(i);
                townlessString.append(currentPlayer.get("name").getAsString()).append(", ");
            }

            MsgUtils.sendPlayer("text_townless_header", false, townlessTextFormatting, false, townless.size());
            
            if (townless.size() > 0)
                MsgUtils.sendPlayer(townlessString.toString(), false, townlessTextFormatting, false);
                
            return 1;
        }).then(ClientCommandManager.literal("inviteAll").executes(c -> {
            if (client.player == null) return -1;

            if (ModUtils.getServerName().endsWith("earthmc.net")) {
                StringBuilder townlessString = new StringBuilder();

                for (int i = 0; i < townless.size(); i++) {
                    JsonObject currentPlayer = (JsonObject) townless.get(i);
                    if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256) break;
                    else townlessString.append(currentPlayer.get("name").getAsString()).append(" ");
                }

                MsgUtils.sendChat("/towny:town invite " + townlessString);
                MsgUtils.sendPlayer("msg_townless_sent", false, Formatting.AQUA, true, townless.size());
            } else
                MsgUtils.sendPlayer("msg_townless_invite_err", false, Formatting.RED, true);
            return 1;
        })).then(ClientCommandManager.literal("revokeAll").executes(c -> {
            if (client.player == null) return -1;

            if (ModUtils.getServerName().endsWith("earthmc.net")) {
                StringBuilder townlessString = new StringBuilder();

                for (int i = 0; i < townless.size(); i++) {
                    JsonObject currentPlayer = (JsonObject) townless.get(i);
                    if (("/towny:town invite -" + townlessString + currentPlayer.get("name").getAsString()).length() > 256) break;
                    else townlessString.append("-" + currentPlayer.get("name").getAsString()).append(" ");
                }

                MsgUtils.sendChat("/towny:town invite " + townlessString);
                MsgUtils.sendPlayer("msg_townless_revoked", false, Formatting.AQUA, true, townless.size());
            } else
                MsgUtils.sendPlayer("msg_townless_revoke_err", false, Formatting.RED, true);
            return 1;
        })).then(ClientCommandManager.literal("refresh").executes(c -> {
            Timers.restartTimer(Timers.townlessTimer);
            MsgUtils.sendPlayer("msg_townless_refresh", false, Formatting.AQUA, true);
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            townless = new JsonArray();
            MsgUtils.sendPlayer("msg_townless_clear", false, Formatting.AQUA, true);
            return 1;
        })));
    }
}
