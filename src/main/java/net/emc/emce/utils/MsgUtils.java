package net.emc.emce.utils;

import net.emc.emce.EarthMCEssentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MsgUtils {
    public static void sendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed) {
        if (prefixed)
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), actionBar);
        else
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText(message).formatted(formatting), actionBar);
    }

    public static void sendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed, Object... args) {
        if (prefixed)
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), actionBar);
        else
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText(message, args).formatted(formatting), actionBar);
    }

    public static void sendSystem(String message, Formatting formatting, boolean prefixed) {
        if (prefixed)
            MinecraftClient.getInstance().player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), MinecraftClient.getInstance().player.getUuid());
        else
            MinecraftClient.getInstance().player.sendSystemMessage(new TranslatableText(message).formatted(formatting), MinecraftClient.getInstance().player.getUuid());
    }

    public static void sendSystem(String message, Formatting formatting, boolean prefixed, Object... args) {
        if (prefixed)
            MinecraftClient.getInstance().player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), MinecraftClient.getInstance().player.getUuid());
        else
            MinecraftClient.getInstance().player.sendSystemMessage(new TranslatableText(message, args).formatted(formatting), MinecraftClient.getInstance().player.getUuid());
    }

    public static void sendChat(String message) {
        MinecraftClient.getInstance().player.sendChatMessage(message);
    }

    public static void sendDebugMessage(String message) {
        if (EarthMCEssentials.instance().isDebugModeEnabled()) {
            sendPlayer("§6[§bDEBUG§6]§r " + message, false, Formatting.GRAY, true);

            EarthMCEssentials.instance().logger().info(message);
        }
    }

    public static void sendDebugMessage(String message, Exception exception) {
        if (EarthMCEssentials.instance().isDebugModeEnabled()) {
            sendDebugMessage(message);
            sendDebugMessage(exception.getMessage());
            exception.printStackTrace();
        }
    }
}