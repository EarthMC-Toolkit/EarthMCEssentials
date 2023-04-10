package net.emc.emce.mixins;

import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method="onDisconnect")
    private void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        System.out.println("EMCE > Disconnected.");

        ModUtils.setServerName("");
        OverlayRenderer.Clear();
        
        instance().sessionCounter = 0;
        instance().scheduler().setHasMap(null);
    }
}