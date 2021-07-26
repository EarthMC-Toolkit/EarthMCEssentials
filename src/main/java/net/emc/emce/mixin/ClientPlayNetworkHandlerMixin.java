package net.emc.emce.mixin;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.emc.emce.tasks.Timers;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {

        ModUtils.updateServerName();
        EarthMCEssentials.setShouldRender(ModUtils.shouldRender());
        MsgUtils.sendDebugMessage("Connected to server. Is on EMC: " + ModUtils.isConnectedToEMC());

        Timers.startAll();
    }

    @Inject(at = @At("HEAD"), method="onDisconnect")
    public void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        Timers.stopAll();
        ModUtils.setServerName("");
    }
}
