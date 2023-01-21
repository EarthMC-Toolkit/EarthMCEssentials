package net.emc.emce.events.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.caches.NationDataCache;
import net.emc.emce.caches.TownDataCache;
import net.emc.emce.objects.Resident;
import net.emc.emce.utils.Translation;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

import java.util.Locale;

import static net.emc.emce.utils.EarthMCAPI.clientName;

public record InfoCommands(EarthMCEssentials instance) {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerTownInfoCommand(dispatcher);
        registerNationInfoCommand(dispatcher);
    }

    public void registerTownInfoCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("towninfo").then(
            ClientCommandManager.argument("townName", StringArgumentType.string()).executes(c -> {
                String townName = StringArgumentType.getString(c, "townName");
                trySendTown(townName);

                return 1;
        })).executes(c -> {
            Resident clientResident = instance.getClientResident();

            if (residentExists(clientResident)) {
                String townName = clientResident.getTown();

                if (townName.equals("") || townName.equals("No Town"))
                    Messaging.sendPrefixed("text_towninfo_not_registered");
                else trySendTown(townName);
            }

            return 1;
        }));
    }

    public void registerNationInfoCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("nationinfo").then(
            ClientCommandManager.argument("nationName", StringArgumentType.string()).executes(c -> {
                String nationName = StringArgumentType.getString(c, "nationName");
                trySendNation(nationName);

                return 1;
        })).executes(c -> {
            Resident clientResident = instance.getClientResident();

            if (residentExists(clientResident)) {
                String nationName = clientResident.getNation();

                if (nationName.equals("") || nationName.equals("No Nation"))
                    Messaging.sendPrefixed("text_nationinfo_not_registered");
                else trySendNation(nationName);
            }

            return 1;
        }));
    }

    private boolean residentExists(Resident res) {
        if (res == null) {
            Messaging.send(Translation.of("text_shared_notregistered", clientName()));
            return false;
        }

        return true;
    }

    private void trySendTown(String townName) {
        NamedTextColor townTextColour = instance.getConfig().commands.townInfoTextColour.named();
        TownDataCache.INSTANCE.getCache().thenAccept(towns -> {
            JsonObject townObject = towns.get(townName.toLowerCase(Locale.ROOT));

            if (townObject == null) {
                Component townArg = Component.text(townName).color(townTextColour);
                Messaging.sendPrefixed(Messaging.create("text_towninfo_err", NamedTextColor.RED, townArg));
            }
            else sendTownInfo(townObject, townTextColour);
        });
    }

    private void trySendNation(String nationName) {
        NamedTextColor nationTextColour = instance.getConfig().commands.nationInfoTextColour.named();
        NationDataCache.INSTANCE.getCache().thenAccept(nations -> {
            JsonObject nationObject = nations.get(nationName.toLowerCase(Locale.ROOT));

            if (nationObject == null) {
                Component nationArg = Component.text(nationName).color(nationTextColour);
                Messaging.sendPrefixed(Messaging.create("text_nationinfo_err", NamedTextColor.RED, nationArg));
            }
            else sendNationInfo(nationObject, nationTextColour);
        });
    }

    static Audience audience = null;
    private void sendMsg(NamedTextColor colour, String key, Object... args) {
        if (audience == null) audience = FabricClientAudiences.of().audience();
        audience.sendMessage(Translation.of(key, args).color(colour));
    }

    private void sendTownInfo(JsonObject townObject, NamedTextColor colour) {
        sendMsg(colour, "text_towninfo_header", townObject.get("name").getAsString());
        sendMsg(colour, "text_towninfo_mayor", townObject.get("mayor").getAsString());
        sendMsg(colour, "text_shared_area", townObject.get("area").getAsString());
        sendMsg(colour, "text_shared_residents", townObject.get("residents").getAsJsonArray().size());
        sendMsg(colour, "text_towninfo_location", townObject.get("x").getAsString(), townObject.get("z").getAsString());
    }

    private void sendNationInfo(JsonObject nationObject, NamedTextColor colour) {
        sendMsg(colour, "text_nationinfo_header", nationObject.get("name").getAsString());
        sendMsg(colour, "text_nationinfo_king", nationObject.get("king").getAsString());
        sendMsg(colour, "text_nationinfo_capital", nationObject.get("capitalName").getAsString());
        sendMsg(colour, "text_shared_area", nationObject.get("area").getAsString());
        sendMsg(colour, "text_shared_residents", nationObject.get("residents").getAsJsonArray().size());
        sendMsg(colour, "text_nationinfo_towns", nationObject.get("towns").getAsJsonArray().size());
    }
}