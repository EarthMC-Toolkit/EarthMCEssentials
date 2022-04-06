package net.emc.emce.utils;

import me.shedaniel.autoconfig.AutoConfig;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.events.commands.*;
import net.emc.emce.events.screen.ScreenInit;
import net.emc.emce.modules.OverlayRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;

public class EventRegistry
{
    public static void RegisterCommands() {
        // Register client-sided commands.
        InfoCommands.registerNationInfoCommand();
        InfoCommands.registerTownInfoCommand();
        NearbyCommand.register();
        NetherCommand.register();
        QueueCommand.register();
        ToggleDebugCommand.register();
        TownlessCommand.register();
        //AllianceCommand.register();
    }

    public static void RegisterScreen() {
        // Constructor automatically sets up the screen event listeners.
        new ScreenInit();
    }

    public static void RegisterClientTick() {
        // Every tick, see if we are pressing F4.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (EarthMCEssentials.configKeybinding.wasPressed())
            {
                Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get();
                client.setScreen(configScreen);
                ScreenInit.setConfigOpen(true);
            }
        });
    }

    public static void RegisterHud() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) ->
                OverlayRenderer.Render(matrixStack));
    }
}