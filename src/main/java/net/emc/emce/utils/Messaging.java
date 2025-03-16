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

@SuppressWarnings("unused")
public class Messaging {
    //#region Helper Methods
    static Audience clientAudience() {
        return MinecraftClientAudiences.of().audience();
    }
    
    @Contract("_, _, -> new")
    public static @NotNull Component create(String key, NamedTextColor keyColour) {
        return translatable().key(key).color(keyColour).build();
    }
    
    @Contract("_, _, _ -> new")
    public static @NotNull Component create(String key, NamedTextColor keyColour, Component... args) {
        return translatable().key(key).color(keyColour).arguments(args).build();
    }
    
    public void createAndSend(String key, NamedTextColor keyColour) {
        send(create(key, keyColour));
    }
    
    public static void createAndSend(String key, NamedTextColor keyColour, Component... args) {
        send(create(key, keyColour, args));
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
        clientAudience().sendMessage(text);
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
        clientAudience().sendActionBar(text);
    }

    public static void sendPrefixedActionBar(Component text) {
        clientAudience().sendActionBar(empty().append(prefix()).append(text));
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
            EMCEssentials.LOGGER.info(message);
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