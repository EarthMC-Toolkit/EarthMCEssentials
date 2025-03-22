package net.emc.emce.events.screen;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.emc.emce.EMCEssentials;

import net.emc.emce.modules.OverlayRenderer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ScreenInit {
    public static void before(MinecraftClient client, Screen newScreen, int scaledWidth, int scaledHeight) {
        OverlayRenderer.UpdateStates();
    }
    
    // TODO:
    //  Find out if the screen is 'initialized' every time its opened as we don't want to be
    //  registering unnecessary listeners every time - if it is, add a check to prevent this.
    //
    /** Called when a screen is initialized to its default state. */
    public static void after(MinecraftClient client, Screen newScreen, int scaledWidth, int scaledHeight) {
        // Check initialized screen is one from ClothConfig lib.
        if (newScreen instanceof ClothConfigScreen) {
            // Add listener to its remove event so `Refresh` is called every time we exit the screen.
            ScreenEvents.remove(newScreen).register(screen -> Refresh());
        }
    }
    
    public static void Refresh() {
        EMCEssentials.instance().updateDebugEnabled();
        
        // TODO: Do we rly need to update states if shouldRender is false?
        OverlayRenderer.UpdateStates();
    }
}