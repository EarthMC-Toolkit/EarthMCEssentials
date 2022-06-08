package net.emc.emce.utils;

import net.emc.emce.EarthMCEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

import java.util.Collections;
import java.util.List;

import static net.emc.emce.EarthMCEssentials.instance;
import static net.kyori.adventure.platform.fabric.FabricClientAudiences.of;
import static net.kyori.adventure.text.Component.*;

public class Messaging {
    public static Component create(String key, NamedTextColor keyColour, Object... args) {
        List<Component> argList = Collections.emptyList();
        for (Object obj : args) argList.add(Component.text(obj.toString()));

        return translatable().key(key).color(keyColour).args(argList).build();
    }

    public static void send(Component text) {
        of().audience().sendMessage(text);
    }

    public static void sendPrefixed(Component text) {
        send(empty().append(prefix()).append(text));
    }

    public static void sendActionBar(Component text) {
        of().audience().sendActionBar(text);
    }

    public static void sendPrefixedActionBar(Component text) {
       of().audience().sendActionBar(empty().append(prefix()).append(text));
    }

    public static void performCommand(String command) {
        MinecraftClient.getInstance().player.sendChatMessage(command.startsWith("/") ? command : "/" + command);
    }

    public static void sendDebugMessage(String message) {
        if (instance().isDebugModeEnabled()) {
            send(translatable("debug_format", text(message).color(NamedTextColor.GRAY)));

            instance().logger().info(message);
        }
    }

    public static void sendDebugMessage(String message, Exception exception) {
        if (instance().isDebugModeEnabled()) {
            sendDebugMessage(message);
            sendDebugMessage(exception.getMessage());
            exception.printStackTrace();
        }
    }

    private static Component prefix() { return translatable("mod_prefix"); }
}