package net.emc.emce.mixins;

import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.MinecraftClientGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;

import static net.emc.emce.utils.EarthMCAPI.fetchEndpoints;
import static net.emc.emce.utils.ModUtils.isConnectedToEMC;

import static net.emc.emce.modules.EventRegistry.RegisterScreen;
import static net.emc.emce.modules.EventRegistry.RegisterHud;

@Mixin(MinecraftClientGame.class)
public abstract class SessionEventListenerMixin {
    @Inject(at = @At("TAIL"), method="onStartGameSession")
    public void onStartGameSession(CallbackInfo ci) {
        System.out.println("EMCE > New game session detected.");

        ModUtils.updateServerName();
        OverlayRenderer.Init();

        RegisterScreen();
        RegisterHud();

        instance().setShouldRender(instance().getConfig().general.enableMod);
        if (isConnectedToEMC()) {
            updateSessionCounter('+');
            //fetchEndpoints();

            // Out of queue, begin map check.
            if (instance().sessionCounter > 1)
                instance().scheduler().initMap();
        }
    }

    @Inject(at = @At("TAIL"), method="onLeaveGameSession")
    public void onLeaveGameSession(CallbackInfo ci) {
        ModUtils.setServerName("");
        OverlayRenderer.Clear();

        if (isConnectedToEMC())
            updateSessionCounter('-');
    }

    void updateSessionCounter(char type) {
        int oldCount = instance().sessionCounter;

        if (type == '+') instance().sessionCounter++;
        else instance().sessionCounter--;

        String debugStr = "Updated session counter from " + oldCount + " to " + instance().sessionCounter;
        Messaging.sendDebugMessage(debugStr);
        System.out.println("EMCE > " + debugStr);
    }
}