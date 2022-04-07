package net.emc.emce.events.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.caches.AllianceDataCache;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Locale;

public class AllianceCommand {
    private final EarthMCEssentials instance;

    public AllianceCommand(EarthMCEssentials instance) {
        this.instance = instance;
    }

    public void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("alliance").then(
                ClientCommandManager.argument("allianceName", StringArgumentType.string()).executes(c -> {
                    String allianceName = StringArgumentType.getString(c, "allianceName");

                    // Implement data cache
                    AllianceDataCache.INSTANCE.getCache().thenAccept(alliances -> {
                        JsonObject allianceObject = alliances.get(allianceName.toLowerCase(Locale.ROOT));

                        if (allianceObject == null)
                            Messaging.sendMessage(Component.translatable("text_alliance_err").color(NamedTextColor.RED));
                        else
                            sendAllianceInfo(allianceObject);
                    });

                    return 1;
                })
        ));
    }

    private void sendAllianceInfo(JsonObject allianceObj) {
        NamedTextColor color = instance.getConfig().commands.allianceInfoTextColour.named();
        Audience client = FabricClientAudiences.of().audience();

        client.sendMessage(createText("text_alliance_header", allianceObj, "allianceName", color, "rank"));
        client.sendMessage(createText("text_alliance_leader", allianceObj, "leaderName", color));
        client.sendMessage(createText("text_alliance_type", allianceObj, "type", color));
        client.sendMessage(createText("text_shared_area", allianceObj, "area", color));
        client.sendMessage(createText("text_alliance_towns", allianceObj, "towns", color));
        client.sendMessage(createText("text_alliance_nations", allianceObj, "nations", color));
        client.sendMessage(createText("text_shared_residents", allianceObj, "residents", color));
        client.sendMessage(createText("text_alliance_discord", allianceObj, "discordInvite", color));
    }

    private Component createText(String langKey, JsonObject obj, String key, TextColor color) {
        JsonElement value = obj.get(key);

        if (value.isJsonArray())
            return Component.translatable(langKey, Component.text(value.getAsJsonArray().size())).color(color);

        return Component.translatable(langKey, Component.text(value.getAsString())).color(color);
    }

    private Component createText(String langKey, JsonObject obj, String key, TextColor color, String option) {
        JsonElement value = obj.get(key);

        if (value.isJsonArray())
            return Component.translatable(langKey, Component.text(value.getAsJsonArray().size()), Component.translatable(obj.get(option).getAsString())).color(color);

        return Component.translatable(langKey, Component.text(value.getAsString()), Component.text(obj.get(option).getAsString())).color(color);
    }
}
