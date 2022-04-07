package net.emc.emce.events.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.caches.NationDataCache;
import net.emc.emce.caches.TownDataCache;
import net.emc.emce.object.Resident;
import net.emc.emce.object.Translation;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

import java.util.Locale;

public record InfoCommands(EarthMCEssentials instance) {

    public void register() {
        registerTownInfoCommand();
        registerNationInfoCommand();
    }

    public void registerTownInfoCommand() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("towninfo").then(
                ClientCommandManager.argument("townName", StringArgumentType.string()).executes(c -> {
                    String townName = StringArgumentType.getString(c, "townName");

                    TownDataCache.INSTANCE.getCache().thenAccept(towns -> {
                        JsonObject townObject = towns.get(townName.toLowerCase(Locale.ROOT));

                        if (townObject == null)
                            Messaging.sendPrefixedMessage(Translation.of("text_towninfo_err", townName));
                        else
                            sendTownInfo(townObject);
                    });

                    return 1;
                })
        ).executes(c -> {
            FabricClientCommandSource source = c.getSource();
            Resident clientResident = instance.getClientResident();

            if (clientResident == null) {
                Messaging.sendMessage(Translation.of("text_shared_notregistered", MinecraftClient.getInstance().player.getName()));
                return 1;
            }

            String townName = clientResident.getTown();
            if (townName.equals("") || townName.equals("No Town"))
                Messaging.sendMessage(Translation.of("text_towninfo_not_registered"));
            else {
                TownDataCache.INSTANCE.getCache().thenAccept(towns -> {
                    JsonObject townObject = towns.get(townName.toLowerCase(Locale.ROOT));

                    if (townObject == null)
                        Messaging.sendMessage(Translation.of("text_towninfo_err"));
                    else
                        sendTownInfo(townObject);
                });
            }

            return 1;
        }));
    }

    public void registerNationInfoCommand() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("nationinfo").then(
                ClientCommandManager.argument("nationName", StringArgumentType.string()).executes(c -> {
                    String nationName = StringArgumentType.getString(c, "nationName");

                    NationDataCache.INSTANCE.getCache().thenAccept(nations -> {
                        JsonObject nationObject = nations.get(nationName.toLowerCase(Locale.ROOT));

                        if (nationObject == null)
                            Messaging.sendPrefixedMessage(Translation.of("text_nationinfo_err"));
                        else
                            sendNationInfo(nationObject);
                    });

                    return 1;
                })).executes(c -> {
            FabricClientCommandSource source = c.getSource();
            Resident clientResident = instance.getClientResident();

            if (clientResident == null) {
                Messaging.sendPrefixedMessage(Translation.of("text_shared_notregistered", MinecraftClient.getInstance().player.getName()));
                return 1;
            }

            String nationName = clientResident.getNation();
            if (nationName.equals("") || nationName.equals("No Nation"))
                Messaging.sendPrefixedMessage(Translation.of("text_nationinfo_not_registered"));
            else {
                NationDataCache.INSTANCE.getCache().thenAccept(nations -> {
                    JsonObject nationObject = nations.get(nationName.toLowerCase(Locale.ROOT));

                    if (nationObject == null)
                        Messaging.sendPrefixedMessage(Translation.of("text_nationinfo_err"));
                    else
                        sendNationInfo(nationObject);
                });
            }

            return 1;
        }));
    }

    private void sendTownInfo(JsonObject townObject) {
        NamedTextColor color = instance.getConfig().commands.townInfoTextColour.named();
        Audience audience = FabricClientAudiences.of().audience();

        audience.sendMessage(Translation.of("text_towninfo_header", townObject.get("name").getAsString()).color(color));
        audience.sendMessage(Translation.of("text_towninfo_mayor", townObject.get("mayor").getAsString()).color(color));
        audience.sendMessage(Translation.of("text_shared_area", townObject.get("area").getAsString()).color(color));
        audience.sendMessage(Translation.of("text_shared_residents", townObject.get("residents").getAsJsonArray().size()).color(color));
        audience.sendMessage(Translation.of("text_towninfo_location", townObject.get("x").getAsString(), townObject.get("z").getAsString()).color(color));
    }

    private void sendNationInfo(JsonObject nationObject) {
        NamedTextColor color = instance.getConfig().commands.nationInfoTextColour.named();
        Audience audience = FabricClientAudiences.of().audience();

        audience.sendMessage(Translation.of("text_nationinfo_header", nationObject.get("name").getAsString()).color(color));
        audience.sendMessage(Translation.of("text_nationinfo_king", nationObject.get("king").getAsString()).color(color));
        audience.sendMessage(Translation.of("text_nationinfo_capital", nationObject.get("capitalName").getAsString()).color(color));
        audience.sendMessage(Translation.of("text_shared_area", nationObject.get("area").getAsString()).color(color));
        audience.sendMessage(Translation.of("text_shared_residents", nationObject.get("residents").getAsJsonArray().size()).color(color));
        audience.sendMessage(Translation.of("text_nationinfo_towns", nationObject.get("towns").getAsJsonArray().size()).color(color));
    }
}