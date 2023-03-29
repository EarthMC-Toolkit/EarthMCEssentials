package net.emc.emce.events.screen;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.emc.emce.modules.OverlayRenderer;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ScreenInit {
    private static boolean configOpen = false;

    public static boolean configOpen() {
        return configOpen;
    }

    public static void setConfigOpen(boolean value) {
        configOpen = value;
    }

    private static void Refresh(Screen screen) {
        OverlayRenderer.Init();
        setConfigOpen(false);
    }

    public static void before(MinecraftClient client, Screen newScreen, int scaledWidth, int scaledHeight) {
        OverlayRenderer.UpdateStates(true, true);
    }

    public static void after(MinecraftClient client, Screen newScreen, int scaledWidth, int scaledHeight) {
        if (newScreen instanceof ClothConfigScreen) {
            ScreenExtensions configSE = ScreenExtensions.getExtensions(newScreen);
            configSE.fabric_getRemoveEvent().register(ScreenInit::Refresh);
        }
    }
}
