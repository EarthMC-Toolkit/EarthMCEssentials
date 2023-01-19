package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.mojang.brigadier.CommandDispatcher;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.ModUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public record TownlessCommand(EarthMCEssentials instance) {
    static NamedTextColor townlessTextColour;

    boolean lengthLimited(String str, int length) { return str.length() > length; }
    boolean lengthLimited(String str) { return lengthLimited(str, 256); }

    String inviteStr(StringBuilder str) { return inviteStr(str.toString()); }
    String inviteStr(String str) { return inviteStr(str, false); }

    String inviteStr(Object str, boolean revoking) {
        return "towny:town invite" + (revoking ? " -" : " ") + str.toString();
    }

    Component createMsg(String key, int size) { return Messaging.create(key, townlessTextColour, whiteText(size)); }
    Component whiteText(int size) { return Component.text(size).color(NamedTextColor.WHITE); }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("townless").executes(c -> {
            List<String> townless = instance.getTownless();
            int size = townless.size();

            townlessTextColour = instance.getConfig().commands.townlessTextColour.named();
            Messaging.send(createMsg("text_townless_header", size));
            if (size > 0) Messaging.send(Component.text(String.join(", ", townless), townlessTextColour));

            return 1;
        }).then(ClientCommandManager.literal("inviteAll").executes(c -> {
            if (MinecraftClient.getInstance().player == null) return -1;
            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();
                List<String> townless = instance.getTownless();

                for (String townlessPlayer : townless) {
                    if (lengthLimited(inviteStr(townlessString) + townlessPlayer)) break;
                    townlessString.append(townlessPlayer).append(" ");
                }

                Messaging.performCommand("towny:town invite " + townlessString);
                Messaging.sendPrefixed(createMsg("msg_townless_sent", townless.size()));
            } else Messaging.sendPrefixed("msg_townless_invite_err");

            return 1;
        })).then(ClientCommandManager.literal("revokeAll").executes(c -> {
            if (MinecraftClient.getInstance().player == null) return -1;
            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();
                List<String> townless = instance.getTownless();

                for (String townlessPlayer : townless) {
                    if (lengthLimited(inviteStr(townlessString, true) + townlessPlayer)) break;
                    townlessString.append("-").append(townlessPlayer).append(" ");
                }

                Messaging.performCommand("towny:town invite " + townlessString);
                Messaging.sendPrefixed(createMsg("msg_townless_revoked", townless.size()));
            }
            else Messaging.sendPrefixed("msg_townless_revoke_err");

            return 1;
        })).then(ClientCommandManager.literal("refresh").executes(c -> {
            EarthMCAPI.getTownless().thenAccept(instance::setTownlessResidents);
            Messaging.sendPrefixed("msg_townless_refresh");

            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setTownlessResidents(new JsonArray());
            Messaging.sendPrefixed("msg_townless_clear");

            return 1;
        })));
    }
}
