package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.Translation;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.ModUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

public record TownlessCommand(EarthMCEssentials instance) {

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("townless").executes(c -> {
                NamedTextColor color = instance.getConfig().commands.townlessTextColour.named();
                Messaging.send(Messaging.create("text_townless_header", color, instance.getTownless().size()));

                if (instance.getTownless().size() > 0)
                    Messaging.send(Component.text(String.join(", ", instance.getTownless()), color));

                return 1;
            }).then(ClientCommandManager.literal("inviteAll").executes(c -> {
                if (MinecraftClient.getInstance().player == null) return -1;
                NamedTextColor townlessTextColour = instance.getConfig().commands.townlessTextColour.named();

                if (ModUtils.isConnectedToEMC()) {
                    StringBuilder townlessString = new StringBuilder();

                    for (String townlessPlayer : instance.getTownless()) {
                        if (("/towny:town invite " + townlessString + " " + townlessPlayer).length() > 256) break;
                        else townlessString.append(townlessPlayer).append(" ");
                    }

                    Messaging.performCommand("/towny:town invite " + townlessString);
                    Messaging.sendPrefixed(Messaging.create("msg_townless_sent", townlessTextColour,
                            Component.text(instance.getTownless().size()).color(NamedTextColor.WHITE)));
                } else Messaging.sendPrefixed(Translation.of("msg_townless_invite_err"));

                return 1;
            })).then(ClientCommandManager.literal("revokeAll").executes(c -> {
                if (MinecraftClient.getInstance().player == null) return -1;
                NamedTextColor townlessTextColour = instance.getConfig().commands.townlessTextColour.named();

                if (ModUtils.isConnectedToEMC()) {
                    StringBuilder townlessString = new StringBuilder();

                    for (String townlessPlayer : instance.getTownless()) {
                        if (("/towny:town invite -" + townlessString + " " + townlessPlayer).length() > 256) break;
                        else townlessString.append("-").append(townlessPlayer).append(" ");
                    }

                    Messaging.performCommand("/towny:town invite " + townlessString);
                    Messaging.sendPrefixed(Messaging.create("msg_townless_revoked", townlessTextColour,
                            Component.text(instance.getTownless().size()).color(NamedTextColor.WHITE)));
                } else Messaging.sendPrefixed(Translation.of("msg_townless_revoke_err"));

                return 1;
            })).then(ClientCommandManager.literal("refresh").executes(c -> {
                EarthMCAPI.getTownless().thenAccept(instance::setTownlessResidents);
                Messaging.sendPrefixed(Translation.of("msg_townless_refresh"));

                return 1;
            })).then(ClientCommandManager.literal("clear").executes(c -> {
                instance.setTownlessResidents(new JsonArray());
                Messaging.sendPrefixed(Translation.of("msg_townless_clear"));

                return 1;
            })));
        });
    }
}
