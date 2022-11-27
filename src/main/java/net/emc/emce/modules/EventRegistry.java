package net.emc.emce.modules;

import com.mojang.brigadier.CommandDispatcher;
import me.shedaniel.autoconfig.AutoConfig;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.events.commands.*;
import net.emc.emce.events.screen.ScreenInit;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;

public class EventRegistry {
    public static void RegisterCommands(EarthMCEssentials instance, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Register client-sided commands.
        new InfoCommands(instance).register(dispatcher);
        new NearbyCommand(instance).register(dispatcher);
        new NetherCommand().register(dispatcher);
        new ToggleDebugCommand().register(dispatcher);
        new TownlessCommand(instance).register(dispatcher);
        new AllianceCommand(instance).register(dispatcher);
    }

    public static void RegisterClientTick() {
        // Every tick, see if we are pressing F4.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ScreenInit.configOpen()) return;

            if (EarthMCEssentials.configKeybinding.wasPressed()) {
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