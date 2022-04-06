package net.emc.emce.events.screen;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.emc.emce.modules.OverlayRenderer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ScreenInit
{
    private static boolean configOpen = false;

    public ScreenInit() {
        ScreenEvents.BEFORE_INIT.register((client, newScreen, scaledWidth, scaledHeight) ->
                OverlayRenderer.UpdateStates(true, true));

        ScreenEvents.AFTER_INIT.register(ScreenInit::afterInit);
    }

    public boolean configOpen() { return configOpen; }
    public static void setConfigOpen(boolean value) { configOpen = value; }
    private static void Refresh(Screen screen) { OverlayRenderer.Init(); }

    private static void afterInit(MinecraftClient client, Screen newScreen, int scaledWidth, int scaledHeight) {
        if (newScreen instanceof ClothConfigScreen) {
            ScreenExtensions configSE = ScreenExtensions.getExtensions(newScreen);
            configSE.fabric_getRemoveEvent().register(ScreenInit::Refresh);

            setConfigOpen(false);
        }
    }
}
