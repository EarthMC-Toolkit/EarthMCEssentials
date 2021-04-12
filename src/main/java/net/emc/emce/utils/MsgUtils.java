package net.emc.emce.utils;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.client;
import static net.emc.emce.EMCE.debugModeEnabled;

import org.apache.logging.log4j.LogManager;

public class MsgUtils
{
    public static void sendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed)
    {
        if (prefixed)
            client.player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), actionBar);
        else
            client.player.sendMessage(new TranslatableText(message).formatted(formatting), actionBar);
    }

    public static void sendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed, Object... args)
    {
        if (prefixed)
            client.player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), actionBar);
        else
            client.player.sendMessage(new TranslatableText(message, args).formatted(formatting), actionBar);
    }

    public static void sendSystem(String message, Formatting formatting, boolean prefixed)
    {
        if (prefixed)
            client.player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), client.player.getUuid());
        else
            client.player.sendSystemMessage(new TranslatableText(message).formatted(formatting), client.player.getUuid());
    }

    public static void sendSystem(String message, Formatting formatting, boolean prefixed, Object... args)
    {
        if (prefixed)
            client.player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), client.player.getUuid());
        else
            client.player.sendSystemMessage(new TranslatableText(message, args).formatted(formatting), client.player.getUuid());
    }

    public static void sendChat(String message)
    {
        client.player.sendChatMessage(message);
    }

    public static void sendDebugMessage(String message) {
        if (debugModeEnabled && client.player != null)
            sendPlayer("§6[§bDEBUG§6]§r " + message, false, Formatting.GRAY, true);
        LogManager.getLogger().info("[EMCE DEBUG] " + message);
    }

    public static void sendDebugMessage(String message, Exception exception) {
        if (debugModeEnabled) {
            sendDebugMessage(message);
            exception.printStackTrace();
        }
    }
}