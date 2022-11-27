package net.emc.emce.mixins;

import com.mojang.authlib.GameProfile;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.modules.EventRegistry;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;
import static net.emc.emce.utils.EarthMCAPI.fetchEndpoints;
import static net.emc.emce.utils.ModUtils.isConnectedToEMC;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(at = @At("TAIL"), method="<init>")
    private void onInit(MinecraftClient client, Screen screen, ClientConnection connection,
                        GameProfile profile, TelemetrySender telemetrySender, CallbackInfo ci) {
        EarthMCAPI.getResident(profile.getName()).thenAccept(res ->
                instance().setClientResident(res));
    }

    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ModUtils.updateServerName();
        OverlayRenderer.Init();

        //EventRegistry.RegisterScreen();
        EventRegistry.RegisterHud();

        if (isConnectedToEMC()) {
            fetchEndpoints();
            instance().setShouldRender(true);
        }
        else instance().setShouldRender(false);
    }

    @Inject(at = @At("TAIL"), method="onDisconnect")
    private void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        ModUtils.setServerName("");
        OverlayRenderer.Clear();
    }
}