package net.emc.emce.mixins;

import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.MinecraftClientGame;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;

import static net.emc.emce.utils.EarthMCAPI.fetchEndpoints;
import static net.emc.emce.utils.ModUtils.isConnectedToEMC;

import static net.emc.emce.modules.EventRegistry.RegisterScreen;
import static net.emc.emce.modules.EventRegistry.RegisterHud;

enum CounterType {
    UP,
    DOWN
}

@Mixin(MinecraftClientGame.class)
public abstract class SessionEventListenerMixin {
    @Inject(at = @At("TAIL"), method="onStartGameSession")
    public void onStartGameSession(CallbackInfo ci) {
        System.out.println("EMCE > Joined game.");
        updateSessionCounter(CounterType.UP);

        ModUtils.updateServerName();
        OverlayRenderer.Init();

        RegisterScreen();
        RegisterHud();

        if (isConnectedToEMC()) {
            fetchEndpoints();
            instance().setShouldRender(true);
        }
        else instance().setShouldRender(false);
    }

    @Inject(at = @At("TAIL"), method="onLeaveGameSession")
    public void onLeaveGameSession(CallbackInfo ci) {
        ModUtils.setServerName("");
        OverlayRenderer.Clear();

        updateSessionCounter(CounterType.DOWN);
    }

    void updateSessionCounter(CounterType type) {
        int oldCount = instance().sessionCounter;

        if (type == CounterType.UP) instance().sessionCounter--;
        else instance().sessionCounter++;

        System.out.println("EMCE > Updated session counter from " + oldCount + " to " + instance().sessionCounter);
    }
}