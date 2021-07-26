package net.emc.emce.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.emc.emce.tasks.Timers;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.util.Formatting;

public class TownlessCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("townless").executes(c -> {
            StringBuilder townlessString = new StringBuilder();
            Formatting townlessTextFormatting = Formatting.byName(EarthMCEssentials.getConfig().commands.townlessTextColour);

            for (int i = 0; i < EarthMCEssentials.getTownless().size(); i++) {
                JsonObject currentPlayer = EarthMCEssentials.getTownless().get(i).getAsJsonObject();
                townlessString.append(currentPlayer.get("name").getAsString()).append(", ");
            }

            MsgUtils.sendPlayer("text_townless_header", false, townlessTextFormatting, false, EarthMCEssentials.getTownless().size());
            
            if (EarthMCEssentials.getTownless().size() > 0)
                MsgUtils.sendPlayer(townlessString.toString(), false, townlessTextFormatting, false);
                
            return 1;
        }).then(ClientCommandManager.literal("inviteAll").executes(c -> {
            if (EarthMCEssentials.getClient().player == null)
                return -1;

            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();

                for (int i = 0; i < EarthMCEssentials.getTownless().size(); i++) {
                    JsonObject currentPlayer = EarthMCEssentials.getTownless().get(i).getAsJsonObject();
                    if (("/towny:town invite " + townlessString + currentPlayer.get("name").getAsString()).length() > 256)
                        break;
                    else
                        townlessString.append(currentPlayer.get("name").getAsString()).append(" ");
                }

                MsgUtils.sendChat("/towny:town invite " + townlessString);
                MsgUtils.sendPlayer("msg_townless_sent", false, Formatting.AQUA, true, EarthMCEssentials.getTownless().size());
            } else
                MsgUtils.sendPlayer("msg_townless_invite_err", false, Formatting.RED, true);
            return 1;
        })).then(ClientCommandManager.literal("revokeAll").executes(c -> {
            if (EarthMCEssentials.getClient().player == null)
                return -1;

            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();

                for (int i = 0; i < EarthMCEssentials.getTownless().size(); i++) {
                    JsonObject currentPlayer = EarthMCEssentials.getTownless().get(i).getAsJsonObject();
                    if (("/towny:town invite -" + townlessString + currentPlayer.get("name").getAsString()).length() > 256)
                        break;
                    else
                        townlessString.append("-").append(currentPlayer.get("name").getAsString()).append(" ");
                }

                MsgUtils.sendChat("/towny:town invite " + townlessString);
                MsgUtils.sendPlayer("msg_townless_revoked", false, Formatting.AQUA, true, EarthMCEssentials.getTownless().size());
            } else
                MsgUtils.sendPlayer("msg_townless_revoke_err", false, Formatting.RED, true);
            return 1;
        })).then(ClientCommandManager.literal("refresh").executes(c -> {
            Timers.restartTimer(Timers.townlessTimer);
            MsgUtils.sendPlayer("msg_townless_refresh", false, Formatting.AQUA, true);
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            EarthMCEssentials.setTownlessResidents(new JsonArray());
            MsgUtils.sendPlayer("msg_townless_clear", false, Formatting.AQUA, true);
            return 1;
        })));
    }
}
