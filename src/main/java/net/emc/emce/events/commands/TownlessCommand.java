package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.Translation;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

public record TownlessCommand(EarthMCEssentials instance) {

    public void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("townless").executes(c -> {
            NamedTextColor color = instance.getConfig().commands.townlessTextColour.named();

            Messaging.sendMessage(Translation.of("text_townless_header", instance.getTownlessPlayers().size()).color(color));

            if (instance.getTownlessPlayers().size() > 0)
                Messaging.sendMessage(Component.text(String.join(", ", instance.getTownlessPlayers()), color));

            return 1;
        }).then(ClientCommandManager.literal("inviteAll").executes(c -> {
            if (MinecraftClient.getInstance().player == null)
                return -1;

            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();

                for (String townlessPlayer : instance.getTownlessPlayers()) {
                    if (("/towny:town invite " + townlessString + " " + townlessPlayer).length() > 256)
                        break;
                    else
                        townlessString.append(townlessPlayer).append(" ");
                }

                Messaging.performCommand("/towny:town invite " + townlessString);
                Messaging.sendPrefixedMessage(Translation.of("msg_townless_sent", instance.getTownlessPlayers().size()));
            } else
                Messaging.sendPrefixedMessage(Translation.of("msg_townless_invite_err"));
            return 1;
        })).then(ClientCommandManager.literal("revokeAll").executes(c -> {
            if (MinecraftClient.getInstance().player == null)
                return -1;

            if (ModUtils.isConnectedToEMC()) {
                StringBuilder townlessString = new StringBuilder();

                for (String townlessPlayer : instance.getTownlessPlayers()) {
                    if (("/towny:town invite -" + townlessString + " " + townlessPlayer).length() > 256)
                        break;
                    else
                        townlessString.append("-").append(townlessPlayer).append(" ");
                }

                Messaging.performCommand("/towny:town invite " + townlessString);
                Messaging.sendPrefixedMessage(Translation.of("msg_townless_revoked", instance.getTownlessPlayers().size()));
            } else
                Messaging.sendPrefixedMessage(Translation.of("msg_townless_revoke_err"));
            return 1;
        })).then(ClientCommandManager.literal("refresh").executes(c -> {
            EarthMCAPI.getTownless().thenAccept(instance::setTownlessResidents);
            Messaging.sendPrefixedMessage(Translation.of("msg_townless_refresh"));
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(c -> {
            instance.setTownlessResidents(new JsonArray());
            Messaging.sendPrefixedMessage(Translation.of("msg_townless_clear"));
            return 1;
        })));
    }
}
