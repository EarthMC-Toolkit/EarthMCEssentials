package net.emc.emce.modules;

import com.mojang.brigadier.CommandDispatcher;
import me.shedaniel.autoconfig.AutoConfig;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.events.commands.*;
import net.emc.emce.events.screen.ScreenInit;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

public class EventRegistry {
    public static void RegisterCommands(EarthMCEssentials instance, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Register client-sided commands.
        new InfoCommands(instance).register(dispatcher);
        new NearbyCommand(instance).register(dispatcher);
        new TownlessCommand(instance).register(dispatcher);
        new AllianceCommand(instance).register(dispatcher);

        new NetherCommand().register(dispatcher);
        new ToggleDebugCommand().register(dispatcher);
    }

    public static void RegisterClientTick() {
        // Every tick, see if we are pressing F4.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (EarthMCEssentials.configKeybinding.wasPressed()) {
                if (!ScreenInit.configOpen()) {
                    try {
                        Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get();
                        client.setScreen(configScreen);

                        ScreenInit.setConfigOpen(true);
                    } catch (Exception e) {
                        Messaging.sendDebugMessage("EMCE > Error opening config screen.", e);
                    }
                }
            }
        });
    }

    public static void RegisterScreen() {
        ScreenEvents.BEFORE_INIT.register(ScreenInit::before);
        ScreenEvents.AFTER_INIT.register(ScreenInit::after);
    }

    public static void RegisterHud() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) ->
            OverlayRenderer.Render(matrixStack));
    }
}