package net.emc.emce.mixins;

import net.emc.emce.modules.EventRegistry;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;
import static net.emc.emce.utils.EarthMCAPI.fetchEndpoints;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ModUtils.updateServerName();
        OverlayRenderer.Init();

        EventRegistry.RegisterScreen();
        EventRegistry.RegisterHud();

        // Joining EMC
        if (ModUtils.isConnectedToEMC()) {
            fetchEndpoints();
            instance().setShouldRender(true);
        }
        else instance().setShouldRender(false);
    }

    @Inject(at = @At("TAIL"), method="onDisconnect")
    private void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        ModUtils.setServerName("");
        OverlayRenderer.Clear();

        instance().scheduler().setHasMap(null);
    }
}
