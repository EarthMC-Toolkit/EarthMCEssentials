package net.emc.emce.events.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.jna.StringArray;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.caches.AllianceDataCache;
import net.emc.emce.caches.TownDataCache;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Locale;
import java.util.Objects;

import static net.emc.emce.EarthMCEssentials.instance;

public class AllianceCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("alliance").then(
                ClientCommandManager.argument("allianceName", StringArgumentType.string()).executes(c -> {
                    String allianceName = StringArgumentType.getString(c, "allianceName");

                    // Implement data cache
                    AllianceDataCache.INSTANCE.getCache().thenAccept(alliances -> {
                        JsonObject allianceObject = alliances.get(allianceName.toLowerCase(Locale.ROOT));

                        if (allianceObject == null) MsgUtils.sendPlayer("text_alliance_err", false, Formatting.RED, true, allianceName);
                        else sendAllianceInfo(allianceObject, c.getSource());
                    });

                    return 1;
                })
        ).executes(c -> {
            FabricClientCommandSource source = c.getSource();

            return 1;
        }));
    }

    private static void sendAllianceInfo(JsonObject allianceObj, FabricClientCommandSource source) {
        Formatting allianceInfoTextColour = Formatting.byName(instance().getConfig().commands.allianceInfoTextColour.name());

        source.sendFeedback(createText("text_alliance_header", allianceObj, "allianceName", allianceInfoTextColour, "rank"));
        source.sendFeedback(createText("text_alliance_leader", allianceObj, "leaderName", allianceInfoTextColour));
        source.sendFeedback(createText("text_alliance_type", allianceObj, "type", allianceInfoTextColour));
        source.sendFeedback(createText("text_shared_area", allianceObj, "area", allianceInfoTextColour));
        source.sendFeedback(createText("text_alliance_towns", allianceObj, "towns", allianceInfoTextColour));
        source.sendFeedback(createText("text_alliance_nations", allianceObj, "nations", allianceInfoTextColour));
        source.sendFeedback(createText("text_shared_residents", allianceObj, "residents", allianceInfoTextColour));
        source.sendFeedback(createText("text_alliance_discord", allianceObj, "discordInvite", allianceInfoTextColour));
    }

    private static Text createText(String langKey, JsonObject obj, String key, Formatting formatting) {
        JsonElement value = obj.get(key);

        if (value.isJsonArray()) {
            return new TranslatableText(langKey, value.getAsJsonArray().size()).formatted(formatting);
        }

        return new TranslatableText(langKey, value.getAsString()).formatted(formatting);
    }

    private static Text createText(String langKey, JsonObject obj, String key, Formatting formatting, String option) {
        JsonElement value = obj.get(key);

        if (value.isJsonArray()) {
            return new TranslatableText(langKey, value.getAsJsonArray().size(), obj.get(option).getAsString()).formatted(formatting);
        }

        return new TranslatableText(langKey, value.getAsString(), obj.get(option).getAsString()).formatted(formatting);
    }
}
