package net.emc.emce.utils;

import net.emc.emce.EarthMCEssentials;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

public class Messaging {
    public static void sendMessage(Component text) {
        FabricClientAudiences.of().audience().sendMessage(text);
    }

    public static void sendPrefixedMessage(Component text) {
        FabricClientAudiences.of().audience().sendMessage(Component.empty().append(prefix()).append(text));
    }

    public static void sendActionBar(Component text) {
        FabricClientAudiences.of().audience().sendActionBar(text);
    }

    public static void sendPrefixedActionBar(Component text) {
        FabricClientAudiences.of().audience().sendActionBar(Component.empty().append(prefix()).append(text));
    }

    public static void performCommand(String command) {
        MinecraftClient.getInstance().player.sendChatMessage(command.startsWith("/") ? command : "/" + command);
    }

    public static void sendDebugMessage(String message) {
        if (EarthMCEssentials.instance().isDebugModeEnabled()) {
            sendMessage(Component.translatable("debug_format", Component.text(message).color(NamedTextColor.GRAY)));

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

    private static Component prefix() {
        return Component.translatable("mod_prefix");
    }
}