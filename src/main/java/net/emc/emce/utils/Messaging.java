package net.emc.emce.utils;

import net.emc.emce.EMCEssentials;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

import static net.kyori.adventure.text.Component.*;

import static net.minecraft.client.MinecraftClient.getInstance;

@SuppressWarnings("unused")
public class Messaging {
    //#region Helper Methods
    static Audience clientAudience() {
        return MinecraftClientAudiences.of().audience();
    }
    
    @Contract("_, _, -> new")
    public static @NotNull Component create(String translationKey, NamedTextColor keyColour) {
        return translatable().key(translationKey).color(keyColour).build();
    }
    
    @Contract("_, _, _ -> new")
    public static @NotNull Component create(String translationKey, NamedTextColor keyColour, Component... args) {
        return translatable().key(translationKey).color(keyColour).arguments(args).build();
    }
    
    public void createAndSend(String translationKey, NamedTextColor keyColour) {
        send(create(translationKey, keyColour));
    }
    
    public static void createAndSend(String translationKey, NamedTextColor keyColour, Component... args) {
        send(create(translationKey, keyColour, args));
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
    
    public static void sendRegular(String text) {
        send(Component.text(text));
    }
    
    public static void sendRegular(String text, NamedTextColor colour) {
        send(Component.text(text).color(colour));
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
    /**
     * If <code>debugMode</code> is enabled in the mod config, this method will output the
     * specified error message to both the in-game chat and the logger.
     * @param message The msg to output.
     */
    public static void sendDebugMessage(String message, Level level) {
        sendDebugMessage(message, null, level);
    }
    
    /**
     * This method will output the given message and the exception message with its stack trace to the logger.
     * Additionally, if <code>debugMode</code> is enabled in the mod config, the message (non-exception) is sent in the chat.
     *
     * @param message The msg to output.
     * @param exception The exception whose msg will be output.
     */
    public static void sendDebugMessage(String message, Exception exception) {
        if (EMCEssentials.instance().debugModeEnabled()) {
            // Send text to chat.
            send(translatable("debug_format", Component.text(message).color(NamedTextColor.GRAY)));
        }
        
        EMCEssentials.LOGGER.error(message, exception);
    }
    
    /**
     * This method will output the given message and the exception message with its stack trace to the logger.
     * Additionally, if <code>debugMode</code> is enabled in the mod config, the message (non-exception) is sent in the chat.
     *
     * @param message The msg to output.
     * @param exception The exception whose msg will be output at TRACE level.
     * @param level The level at which to output <code>message</code>.
     */
    public static void sendDebugMessage(String message, Exception exception, Level level) {
        if (EMCEssentials.instance().debugModeEnabled()) {
            // Send text to chat.
            send(translatable("debug_format", Component.text(message).color(NamedTextColor.GRAY)));
        }
        
        // Output trace if exception is provided.
        if (exception != null) {
            EMCEssentials.LOGGER.trace(message, exception);
        } else {
            // Always output non-exception message.
            switch (level) {
                case TRACE -> EMCEssentials.LOGGER.trace(message);
                case DEBUG -> EMCEssentials.LOGGER.debug(message);
                case WARN -> EMCEssentials.LOGGER.warn(message);
                case ERROR -> EMCEssentials.LOGGER.error(message);
                default -> EMCEssentials.LOGGER.info(message);
            }
        }
    }
    //#endregion
}