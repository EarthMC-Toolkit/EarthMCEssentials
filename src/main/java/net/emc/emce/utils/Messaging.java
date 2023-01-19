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

import static net.minecraft.client.MinecraftClient.getInstance;

public class Messaging {
    //#region Helper Methods
    public static Component create(String key, NamedTextColor keyColour, Object... args) {
        List<Component> argList = Collections.emptyList();
        for (Object obj : args) argList.add(Component.text(obj.toString()));

        return translatable().key(key).color(keyColour).args(argList).build();
    }
    //#endregion

    private static Component prefix() {
        return translatable("mod_prefix");
    }

    //#region Regular Msg
    public static void send(String text) {
        send(translatable(text));
    }

    public static void send(Component text) {
        of().audience().sendMessage(text);
    }
    //#endregion

    //#region Prefixed Msg
    public static void sendPrefixed(String text) {
        sendPrefixed(translatable(text));
    }

    public static void sendPrefixed(Component text) {
        send(empty().append(prefix()).append(text));
    }
    //#endregion

    //#region Action Bar
    public static void sendActionBar(Component text) {
        of().audience().sendActionBar(text);
    }

    public static void sendPrefixedActionBar(Component text) {
       of().audience().sendActionBar(empty().append(prefix()).append(text));
    }
    //#endregion

    //#region Send Command
    public static void performCommand(String cmd) {
        getInstance().player.networkHandler.sendCommand(cmd);
    }
    //endregion

    //#region Debug Methods
    public static void sendDebugMessage(String message) {
        if (instance().debugEnabled()) {
            send(translatable("debug_format", text(message).color(NamedTextColor.GRAY)));
            instance().logger().info(message);
        }
    }

    public static void sendDebugMessage(String message, Exception exception) {
        if (instance().debugEnabled()) {
            sendDebugMessage(message);
            sendDebugMessage(exception.getMessage());

            exception.printStackTrace();
        }
    }
    //#endregion
}