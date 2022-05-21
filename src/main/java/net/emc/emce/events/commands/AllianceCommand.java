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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.Translatable;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.Locale;

public record AllianceCommand(EarthMCEssentials instance) {

    public void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("alliance").then(
                ClientCommandManager.argument("allianceName", StringArgumentType.string()).executes(c -> {
                    String allianceName = StringArgumentType.getString(c, "allianceName");
                    NamedTextColor colour = instance.getConfig().commands.allianceInfoTextColour.named();

                    // Implement data cache
                    AllianceDataCache.INSTANCE.getCache().thenAccept(alliances -> {
                        JsonObject allianceObject = alliances.get(allianceName.toLowerCase(Locale.ROOT));

                        if (allianceObject == null) {
                            Component arg = Component.text(allianceName).color(colour);
                            Messaging.send(Messaging.create("text_alliance_err", NamedTextColor.RED, arg));
                        }
                        else sendAllianceInfo(allianceObject, colour);
                    });

                    return 1;
                })
        ));
    }

    private void sendAllianceInfo(JsonObject allianceObj, NamedTextColor colour) {
        Audience client = FabricClientAudiences.of().audience();

        client.sendMessage(createText("text_alliance_header", allianceObj, "allianceName", colour, "rank"));
        client.sendMessage(createText("text_alliance_leader", allianceObj, "leaderName", colour));
        client.sendMessage(createText("text_alliance_type", allianceObj, "type", colour));
        client.sendMessage(createText("text_shared_area", allianceObj, "area", colour));
        client.sendMessage(createText("text_alliance_towns", allianceObj, "towns", colour));
        client.sendMessage(createText("text_alliance_nations", allianceObj, "nations", colour));
        client.sendMessage(createText("text_shared_residents", allianceObj, "residents", colour));
        client.sendMessage(createText("text_alliance_discord", allianceObj, "discordInvite", colour, true));
    }

    private Component createText(String langKey, JsonObject obj, String key, TextColor color) {
        return Component.translatable(langKey, Component.text(formatElement(obj.get(key)))).color(color);
    }

    private Component createText(String langKey, JsonObject obj, String key, TextColor color, String option) {
        return Component.translatable(langKey, Component.text(formatElement(obj.get(key))),
               Component.text(obj.get(option).getAsString())).color(color);
    }

    private Component createText(String langKey, JsonObject obj, String key, TextColor color, boolean isLink) {
        String text = formatElement(obj.get(key));
        Style linkStyle = Style.style(ClickEvent.openUrl(text), NamedTextColor.AQUA, TextDecoration.UNDERLINED);

        return Component.translatable().key(langKey).color(color)
                .append(Component.text(text).style(linkStyle)).build();
    }

    private static String formatElement(JsonElement element) {
        if (!element.isJsonArray()) return element.getAsString();

        StringBuilder sb = new StringBuilder();

        var iter = element.getAsJsonArray().iterator();
        while (iter.hasNext()) {
            sb.append(iter.next().getAsString());
            sb.append(", ");
        }

        return sb.substring(0, sb.length() - 2);
    }
}
