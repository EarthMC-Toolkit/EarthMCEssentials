package net.emc.emce.utils;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.client;

public class MsgUtils
{
    public static void SendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed)
    {
        if (prefixed)
            client.player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), actionBar);
        else
            client.player.sendMessage(new TranslatableText(message).formatted(formatting), actionBar);
    }

    public static void SendPlayer(String message, boolean actionBar, Formatting formatting, boolean prefixed, Object... args)
    {
        if (prefixed)
            client.player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), actionBar);
        else
            client.player.sendMessage(new TranslatableText(message, args).formatted(formatting), actionBar);
    }

    public static void SendSystem(String message, Formatting formatting, boolean prefixed)
    {
        if (prefixed)
            client.player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), client.player.getUuid());
        else
            client.player.sendSystemMessage(new TranslatableText(message).formatted(formatting), client.player.getUuid());
    }

    public static void SendSystem(String message, Formatting formatting, boolean prefixed, Object... args)
    {
        if (prefixed)
            client.player.sendSystemMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), client.player.getUuid());
        else
            client.player.sendSystemMessage(new TranslatableText(message, args).formatted(formatting), client.player.getUuid());
    }

    public static void SendChat(String message)
    {
        client.player.sendChatMessage(message);
    }
}