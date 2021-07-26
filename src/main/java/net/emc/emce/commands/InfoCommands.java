package net.emc.emce.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class InfoCommands {
    public static void registerTownInfoCommand() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("towninfo").then(
            ClientCommandManager.argument("townName", StringArgumentType.string()).executes(c -> {
                String townName = StringArgumentType.getString(c, "townName");
                JsonObject townObject = new JsonObject();

                for (int i  = 0; i < EarthMCEssentials.getTowns().size(); i++) {
                    JsonObject town = EarthMCEssentials.getTowns().get(i).getAsJsonObject();

                    if (town.get("name").getAsString().equalsIgnoreCase(townName)) {
                        townObject = town;
                        break;
                    }
                }

                FabricClientCommandSource source = c.getSource();
                if (!townObject.has("name")) 
                    MsgUtils.sendPlayer("text_towninfo_err", false, Formatting.RED, true, townName);
                else {
                    Formatting townInfoTextColour = Formatting.byName(EarthMCEssentials.getConfig().commands.townInfoTextColour);

                    source.sendFeedback(new TranslatableText("text_towninfo_header", townObject.get("name").getAsString()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_towninfo_mayor", townObject.get("mayor").getAsString()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_towninfo_area", townObject.get("area").getAsString()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_shared_residents", townObject.get("residents").getAsJsonArray().size()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_towninfo_location", townObject.get("x").getAsString(), townObject.get("z").getAsString()).formatted(townInfoTextColour));
                }

                return 1;
            })
        ).executes(c -> {
            FabricClientCommandSource source = c.getSource();

            if (EarthMCEssentials.getClientResident() == null) {
                MsgUtils.sendPlayer("text_shared_notregistered", false, Formatting.RED, true, EarthMCEssentials.getClient().player.getName());
                return 1;
            }

            if (EarthMCEssentials.getClientResident().getTown().equals("") || EarthMCEssentials.getClientResident().getTown().equals("No Town"))
                MsgUtils.sendPlayer("text_towninfo_not_registered", false, Formatting.RED, true);
            else {
                JsonObject townObject = new JsonObject();

                for (int i = 0; i < EarthMCEssentials.getTowns().size(); i++) {
                    JsonObject town = EarthMCEssentials.getTowns().get(i).getAsJsonObject();
                    if (town.get("name").getAsString().equalsIgnoreCase(EarthMCEssentials.getClientResident().getTown())) {
                        townObject = town;
                        break;
                    }
                }

                if (!townObject.has("name")) 
                    source.sendFeedback(new TranslatableText("text_towninfo_err", EarthMCEssentials.getClientResident().getTown()).formatted(Formatting.RED));
                else {
                    Formatting townInfoTextColour = Formatting.byName(EarthMCEssentials.getConfig().commands.townInfoTextColour);

                    source.sendFeedback(new TranslatableText("text_towninfo_header", townObject.get("name").getAsString()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_towninfo_mayor", townObject.get("mayor").getAsString()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_towninfo_area", townObject.get("area").getAsString()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_shared_residents", townObject.get("residents").getAsJsonArray().size()).formatted(townInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_towninfo_location", townObject.get("x").getAsString(), townObject.get("z").getAsString()).formatted(townInfoTextColour));
                }
            }

            return 1;
        }));
    }

    public static void registerNationInfoCommand() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("nationinfo").then(
            ClientCommandManager.argument("nationName", StringArgumentType.string()).executes(c -> {
            String nationName = StringArgumentType.getString(c, "nationName");
            JsonObject nationObject = new JsonObject();

            for (int i = 0; i < EarthMCEssentials.getNations().size(); i++) {
                JsonObject nation = EarthMCEssentials.getNations().get(i).getAsJsonObject();

                if (nation.get("name").getAsString().equalsIgnoreCase(nationName)) {
                    nationObject = nation;
                    break;
                }
            }

            FabricClientCommandSource source = c.getSource();
            if (!nationObject.has("name"))
                MsgUtils.sendPlayer("text_nationinfo_err", false, Formatting.RED, true, nationName);
            else {
                Formatting nationInfoTextColour = Formatting.byName(EarthMCEssentials.getConfig().commands.nationInfoTextColour);

                source.sendFeedback(new TranslatableText("text_nationinfo_header", nationObject.get("name").getAsString()).formatted(nationInfoTextColour));
                source.sendFeedback(new TranslatableText("text_nationinfo_king", nationObject.get("king").getAsString()).formatted(nationInfoTextColour));
                source.sendFeedback(new TranslatableText("text_nationinfo_capital", nationObject.get("capitalName").getAsString()).formatted(nationInfoTextColour));
                source.sendFeedback(new TranslatableText("text_nationinfo_area", nationObject.get("area").getAsString()).formatted(nationInfoTextColour));
                source.sendFeedback(new TranslatableText("text_shared_residents", nationObject.get("residents").getAsJsonArray().size()).formatted(nationInfoTextColour));
                source.sendFeedback(new TranslatableText("text_nationinfo_towns", nationObject.get("towns").getAsJsonArray().size()).formatted(nationInfoTextColour));
            }

            return 1;
        })).executes(c -> {
            FabricClientCommandSource source = c.getSource();

            if (EarthMCEssentials.getClientResident() == null) {
                MsgUtils.sendPlayer("text_shared_notregistered", false, Formatting.RED, true, EarthMCEssentials.getClient().player.getName());
                return 1;
            }

            if (EarthMCEssentials.getClientResident().getNation().equals("") || EarthMCEssentials.getClientResident().getNation().equals("No Nation"))
                MsgUtils.sendPlayer("text_nationinfo_not_registered", false, Formatting.RED, true);
            else {
                JsonObject nationObject = new JsonObject();
                
                for (int i = 0; i < EarthMCEssentials.getNations().size(); i++) {
                    JsonObject nation = EarthMCEssentials.getNations().get(i).getAsJsonObject();
                    if (nation.get("name").getAsString().equalsIgnoreCase(EarthMCEssentials.getClientResident().getNation())) {
                        nationObject = nation;
                        break;
                    }
                }

                if (!nationObject.has("name"))
                    MsgUtils.sendPlayer("text_nationinfo_err", false, Formatting.RED, true, EarthMCEssentials.getClientResident().getNation());
                else {
                    Formatting nationInfoTextColour = Formatting.byName(EarthMCEssentials.getConfig().commands.nationInfoTextColour);

                    source.sendFeedback(new TranslatableText("text_nationinfo_header", nationObject.get("name").getAsString()).formatted(nationInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_nationinfo_king", nationObject.get("king").getAsString()).formatted(nationInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_nationinfo_capital", nationObject.get("capitalName").getAsString()).formatted(nationInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_nationinfo_area", nationObject.get("area").getAsString()).formatted(nationInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_shared_residents", nationObject.get("residents").getAsJsonArray().size()).formatted(nationInfoTextColour));
                    source.sendFeedback(new TranslatableText("text_nationinfo_towns", nationObject.get("towns").getAsJsonArray().size()).formatted(nationInfoTextColour));
                }
            }

            return 1;
        }));
    }
}