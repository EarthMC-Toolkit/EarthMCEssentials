package net.emc.emce.mixin;

import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.EventRegistry;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        ModUtils.updateServerName();
        instance().setShouldRender(ModUtils.shouldRender());
        MsgUtils.sendDebugMessage("Connected to server. Is on EMC: " + ModUtils.isConnectedToEMC());

        OverlayRenderer.Init();
        instance().scheduler().start();

        EventRegistry.RegisterScreen();
        EventRegistry.RegisterHud();
    }

    @Inject(at = @At("HEAD"), method="onDisconnect")
    public void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci)
    {
        instance().scheduler().stop();
        ModUtils.setServerName("");
    }
}
