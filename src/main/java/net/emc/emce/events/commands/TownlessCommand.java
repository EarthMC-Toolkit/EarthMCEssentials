package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.ModUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public record TownlessCommand(EarthMCEssentials instance) {
    static NamedTextColor townlessTextColour;

    boolean lengthLimited(String str, int length) { return str.length() >= length; }
    boolean lengthLimited(String str) { return lengthLimited(str, 256); }

    String inviteStr(StringBuilder str) { return inviteStr(str.toString()); }
    String inviteStr(String str) { return inviteStr(str, false); }

    String inviteStr(Object str, boolean revoking) {
        return "towny:town invite" + (revoking ? " -" : " ") + str.toString() + " ";
    }

    NamedTextColor getTextColour() { return instance.config().commands.townlessTextColour.named(); }
    TextComponent whiteText(int size) { return Component.text(size).color(NamedTextColor.WHITE); }

    Component createMsg(String key, int size) {
        return Messaging.create(key, getTextColour(), whiteText(size));
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("townless").executes(c -> {
            List<String> townless = instance.getTownless();
            int size = townless.size();

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
                    else townlessString.append(townlessPlayer).append(" ");
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
                    else townlessString.append("-").append(townlessPlayer).append(" ");
                }

                Messaging.performCommand("towny:town invite " + townlessString);
                Messaging.sendPrefixed(createMsg("msg_townless_revoked", townless.size()));
            }
            else Messaging.sendPrefixed("msg_townless_revoke_err");

            return 1;
        })).then(ClientCommandManager.literal("refresh").executes(c -> {
            instance.setTownless(EarthMCAPI.getTownless());
            Messaging.sendPrefixed("msg_townless_refresh");

            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            OverlayRenderer.SetTownless(List.of());
            OverlayRenderer.UpdateStates(true, false);

            Messaging.sendPrefixed("msg_townless_clear");

            return 1;
        })));
    }
}
