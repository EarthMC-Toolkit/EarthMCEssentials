package net.emc.emce.utils;

import net.emc.emce.EMCEssentials;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.*;

import static net.minecraft.client.MinecraftClient.getInstance;

public class Messaging {
    //#region Helper Methods
    @Contract("_, _, _ -> new")
    public static @NotNull Component create(String key, NamedTextColor keyColour, Component... args) {
        return translatable().key(key).color(keyColour).arguments(args).build();
    }

    static Audience getAudience() {
        return MinecraftClientAudiences.of().audience();
    }
    //#endregion

    @Contract(value = " -> new", pure = true)
    private static @NotNull Component prefix() {
        return translatable("mod_prefix");
    }

    //#region Regular Msg
    public static void send(String text) {
        send(translatable(text));
    }

    public static void send(Component text) {
        getAudience().sendMessage(text);
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
        getAudience().sendActionBar(text);
    }

    public static void sendPrefixedActionBar(Component text) {
        getAudience().sendActionBar(empty().append(prefix()).append(text));
    }
    //#endregion

    //#region Send Command
    public static void performCommand(String cmd) {
        ClientPlayerEntity pl = getInstance().player;
        if (pl != null) pl.networkHandler.sendCommand(cmd);
    }
    //endregion

    //#region Debug Methods
    public static void sendDebugMessage(String message) {
        if (EMCEssentials.instance().debugEnabled()) {
            send(translatable("debug_format", text(message).color(NamedTextColor.GRAY)));
            EMCEssentials.logger().info(message);
        }
    }
    
    public static void sendDebugMessage(String message, Exception exception) {
        if (EMCEssentials.instance().debugEnabled()) {
            sendDebugMessage(message);
            sendDebugMessage(exception.getMessage());
            
            // TODO: Replace with something else?
            exception.printStackTrace(System.err);
        }
    }
    //#endregion
}