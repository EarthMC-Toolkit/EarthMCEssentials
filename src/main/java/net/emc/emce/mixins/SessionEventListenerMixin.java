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

@Mixin(MinecraftClientGame.class)
public abstract class SessionEventListenerMixin {

    @Inject(at = @At("TAIL"), method="onStartGameSession")
    public void onStartGameSession(CallbackInfo ci) {
        instance().sessionCounter++;
        System.out.println("EMCE > Session counter: " + instance().sessionCounter);
    }

    @Inject(at = @At("TAIL"), method="onLeaveGameSession")
    public void onLeaveGameSession(CallbackInfo ci) {
        ModUtils.setServerName("");
        OverlayRenderer.Clear();

        instance().sessionCounter--;
    }
}