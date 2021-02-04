package net.emc.emce;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class PlayerMessaging {

    public static void sendMessage(String message, Formatting formatting, boolean prefixed) {
        if (prefixed)
            EMCE.client.player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message).formatted(formatting)), false);
        else
            EMCE.client.player.sendMessage(new TranslatableText(message).formatted(formatting), false);
    }

    public static void sendMessage(String message, Formatting formatting, boolean prefixed, Object... args) {
        if (prefixed)
            EMCE.client.player.sendMessage(new TranslatableText("mod_prefix").append(new TranslatableText(message, args).formatted(formatting)), false);
        else
            EMCE.client.player.sendMessage(new TranslatableText(message, args).formatted(formatting), false);
    }
}
