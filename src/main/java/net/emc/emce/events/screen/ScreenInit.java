package net.emc.emce.events.screen;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.OverlayRenderer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.emc.emce.EarthMCEssentials.instance;

public class ScreenInit {
    private static void Refresh(Screen screen) {
        EarthMCEssentials instance = instance();
        ModConfig.General gen = instance.config().general;

        instance.setShouldRender(gen.enableMod);
        instance.setDebugEnabled(gen.debugLog);

        OverlayRenderer.Init();
    }

    public static void before(MinecraftClient client, Screen newScreen, int scaledWidth, int scaledHeight) {
        OverlayRenderer.UpdateStates(true, true);
    }

    public static void after(MinecraftClient client, Screen newScreen, int scaledWidth, int scaledHeight) {
        if (newScreen instanceof ClothConfigScreen) {
            ScreenEvents.remove(newScreen).register(ScreenInit::Refresh);
        }
    }
}