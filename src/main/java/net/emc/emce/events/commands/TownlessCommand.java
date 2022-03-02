package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.mojang.brigadier.context.CommandContext;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import static net.emc.emce.EarthMCEssentials.instance;

public class TownlessCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("townless").executes(TownlessCommand::run).then(
                ClientCommandManager.literal("inviteAll").executes(c -> {
            if (MinecraftClient.getInstance().player == null)
                return -1;

            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();

                for (String townlessPlayer : instance().getTownless()) {
                    if (("/towny:town invite " + townlessString + " " + townlessPlayer).length() > 256)
                        break;
                    else
                        townlessString.append(townlessPlayer).append(" ");
                }

                MsgUtils.sendChat("/towny:town invite " + townlessString);
                MsgUtils.sendPlayer("msg_townless_sent", false, Formatting.AQUA, true, instance().getTownless().size());
            } else
                MsgUtils.sendPlayer("msg_townless_invite_err", false, Formatting.RED, true);
            return 1;
        })).then(ClientCommandManager.literal("revokeAll").executes(c -> {
            if (MinecraftClient.getInstance().player == null)
                return -1;

            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();

                for (String townlessPlayer : instance().getTownless()) {
                    if (("/towny:town invite -" + townlessString + " " + townlessPlayer).length() > 256)
                        break;
                    else
                        townlessString.append("-").append(townlessPlayer).append(" ");
                }

                MsgUtils.sendChat("/towny:town invite " + townlessString);
                MsgUtils.sendPlayer("msg_townless_revoked", false, Formatting.AQUA, true, instance().getTownless().size());
            } else
                MsgUtils.sendPlayer("msg_townless_revoke_err", false, Formatting.RED, true);
            return 1;
        })).then(ClientCommandManager.literal("refresh").executes(c -> {
            EarthMCAPI.getTownless().thenAccept(instance()::setTownlessResidents);
            MsgUtils.sendPlayer("msg_townless_refresh", false, Formatting.AQUA, true);
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance().setTownlessResidents(new JsonArray());
            MsgUtils.sendPlayer("msg_townless_clear", false, Formatting.AQUA, true);
            return 1;
        })));
    }

    private static int run(CommandContext<FabricClientCommandSource> c) {
        StringBuilder townlessString = new StringBuilder();
        Formatting townlessTextFormatting = Formatting.byName(instance().getConfig().commands.townlessTextColour.name());

        for (String townlessPlayer : instance().getTownless())
            townlessString.append(townlessPlayer).append(", ");

        MsgUtils.sendPlayer("text_townless_header", false, townlessTextFormatting, false, instance().getTownless().size());

        if (instance().getTownless().size() > 0)
            MsgUtils.sendPlayer(townlessString.toString(), false, townlessTextFormatting, false);

        return 1;
    }
}
