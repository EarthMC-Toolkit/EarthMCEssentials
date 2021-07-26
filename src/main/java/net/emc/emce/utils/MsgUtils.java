package net.emc.emce.utils;

import net.emc.emce.EarthMCEssentials;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MsgUtils {
    public static void sendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed) {
        if (prefixed)
            EarthMCEssentials.getClient().player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), actionBar);
        else
            EarthMCEssentials.getClient().player.sendMessage(new TranslatableText(message).formatted(formatting), actionBar);
    }

    public static void sendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed, Object... args) {
        if (prefixed)
            EarthMCEssentials.getClient().player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), actionBar);
        else
            EarthMCEssentials.getClient().player.sendMessage(new TranslatableText(message, args).formatted(formatting), actionBar);
    }

    public static void sendSystem(String message, Formatting formatting, boolean prefixed) {
        if (prefixed)
            EarthMCEssentials.getClient().player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), EarthMCEssentials.getClient().player.getUuid());
        else
            EarthMCEssentials.getClient().player.sendSystemMessage(new TranslatableText(message).formatted(formatting), EarthMCEssentials.getClient().player.getUuid());
    }

    public static void sendSystem(String message, Formatting formatting, boolean prefixed, Object... args) {
        if (prefixed)
            EarthMCEssentials.getClient().player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), EarthMCEssentials.getClient().player.getUuid());
        else
            EarthMCEssentials.getClient().player.sendSystemMessage(new TranslatableText(message, args).formatted(formatting), EarthMCEssentials.getClient().player.getUuid());
    }

    public static void sendChat(String message) {
        EarthMCEssentials.getClient().player.sendChatMessage(message);
    }

    public static void sendDebugMessage(String message) {
        if (EarthMCEssentials.isDebugModeEnabled()) {
            sendPlayer("§6[§bDEBUG§6]§r " + message, false, Formatting.GRAY, true);

            EarthMCEssentials.getLogger().info(message);
        }
    }

    public static void sendDebugMessage(String message, Exception exception) {
        if (EarthMCEssentials.isDebugModeEnabled()) {
            sendDebugMessage(message);
            sendDebugMessage(exception.getMessage());
            exception.printStackTrace();
        }
    }
}