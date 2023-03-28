package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.emcw.entities.Location;
import io.github.emcw.entities.Nation;
import io.github.emcw.entities.Resident;
import io.github.emcw.entities.Town;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.Translation;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

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
        Town town = EarthMCAPI.getTown(townName.toLowerCase(Locale.ROOT));

        if (town == null) {
            Component townArg = Component.text(townName).color(townTextColour);
            Messaging.sendPrefixed(Messaging.create("text_towninfo_err", NamedTextColor.RED, townArg));
        }
        else sendTownInfo(town, townTextColour);
    }

    private void trySendNation(@NotNull String nationName) {
        NamedTextColor nationTextColour = instance.getConfig().commands.nationInfoTextColour.named();
        Nation nation = EarthMCAPI.getNation(nationName.toLowerCase(Locale.ROOT));

        if (nation == null) {
            Component nationArg = Component.text(nationName).color(nationTextColour);
            Messaging.sendPrefixed(Messaging.create("text_nationinfo_err", NamedTextColor.RED, nationArg));
        }
        else sendNationInfo(nation, nationTextColour);
    }

    static Audience audience = null;
    private void sendMsg(NamedTextColor colour, String key, Object... args) {
        if (audience == null) audience = FabricClientAudiences.of().audience();
        audience.sendMessage(Translation.of(key, args).color(colour));
    }

    private void sendTownInfo(@NotNull Town town, NamedTextColor colour) {
        sendMsg(colour, "text_towninfo_header", town.getName());
        sendMsg(colour, "text_towninfo_mayor", town.getMayor());
        sendMsg(colour, "text_shared_area", town.getArea());
        sendMsg(colour, "text_shared_residents", town.getResidents().size());

        Location loc = town.getLocation();
        sendMsg(colour, "text_towninfo_location", loc.getX(), loc.getZ());
    }

    private void sendNationInfo(@NotNull Nation nation, NamedTextColor colour) {
        sendMsg(colour, "text_nationinfo_header", nation.getName());
        sendMsg(colour, "text_nationinfo_king", nation.getLeader());
        sendMsg(colour, "text_nationinfo_capital", nation.getCapital().getName());
        sendMsg(colour, "text_shared_area", nation.getArea());
        sendMsg(colour, "text_shared_residents", nation.getResidents().size());
        sendMsg(colour, "text_nationinfo_towns", nation.getTowns().size());
    }
}