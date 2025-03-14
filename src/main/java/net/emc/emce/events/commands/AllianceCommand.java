package net.emc.emce.events.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import com.mojang.brigadier.context.CommandContext;
import net.emc.emce.EMCEssentials;
import net.emc.emce.caches.AllianceDataCache;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Locale;

public record AllianceCommand(EMCEssentials instance) {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        var allianceCmd = ClientCommandManager.literal("alliance")
            .then(ClientCommandManager.literal("list").executes(ctx -> execAllianceList()))
            .then(ClientCommandManager.argument("allianceName", StringArgumentType.string())
                .executes(this::execAllianceNameArg)
            );
        
        dispatcher.register(allianceCmd);
    }
    
    public int execAllianceList() {
        return 1;
    }
    
    public int execAllianceNameArg(CommandContext<FabricClientCommandSource> ctx) {
        String allianceName = StringArgumentType.getString(ctx, "allianceName");
        NamedTextColor colour = instance.config().commands.allianceInfoTextColour.named();
        
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
    }

    private void sendAllianceInfo(JsonObject allianceObj, NamedTextColor colour) {
        Audience pAud = MinecraftClientAudiences.of().audience();
        
        pAud.sendMessage(createText("text_alliance_header", allianceObj, "allianceName", colour, "rank"));
        pAud.sendMessage(createText("text_alliance_leader", allianceObj, "leaderName", colour));
        pAud.sendMessage(createText("text_alliance_type", allianceObj, "type", colour));
        pAud.sendMessage(createText("text_shared_area", allianceObj, "area", colour));
        pAud.sendMessage(createText("text_alliance_towns", allianceObj, "towns", colour));
        pAud.sendMessage(createText("text_alliance_nations", allianceObj, "nations", colour));
        pAud.sendMessage(createText("text_shared_residents", allianceObj, "residents", colour));
        pAud.sendMessage(createText("text_alliance_discord", allianceObj, "discordInvite", colour, true));
    }

    private TranslatableComponent createText(String langKey, JsonObject obj, String key, TextColor color) {
        return Component.translatable(langKey, Component.text(formatElement(obj.get(key)))).color(color);
    }

    @SuppressWarnings("SameParameterValue")
    private TranslatableComponent createText(String langKey, JsonObject obj, String key, TextColor color, String option) {
        return Component.translatable(langKey, Component.text(formatElement(obj.get(key))),
               Component.text(obj.get(option).getAsString())).color(color);
    }
    
    @SuppressWarnings("SameParameterValue")
    private TranslatableComponent createText(String langKey, JsonObject obj, String key, TextColor color, boolean isLink) {
        if (!isLink) return createText(langKey, obj, key, color);

        // Create style for link text.
        String text = formatElement(obj.get(key));
        Style linkStyle = Style.style(ClickEvent.openUrl(text), NamedTextColor.AQUA, TextDecoration.UNDERLINED);
        TextComponent textComp = Component.text(text).style(linkStyle);
        
        // Create new component and append text with link style.
        return Component.translatable(langKey).color(color).append(textComp);
    }

    private static String formatElement(JsonElement element) {
        if (!element.isJsonArray()) return element.getAsString();

        StringBuilder sb = new StringBuilder();

        for (JsonElement jsonElement : element.getAsJsonArray()) {
            sb.append(jsonElement.getAsString());
            sb.append(", ");
        }

        return sb.substring(0, sb.length() - 2);
    }
}