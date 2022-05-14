package net.emc.emce.mixin;

import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.launcher.SessionEventListener;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.utils.EventRegistry;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.Messaging;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;
import static net.emc.emce.utils.EarthMCAPI.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) throws InterruptedException {
        ModUtils.updateServerName();
        Messaging.sendDebugMessage("Connected to server. Is on EMC: " + ModUtils.isConnectedToEMC());

        OverlayRenderer.Init();

        EventRegistry.RegisterScreen();
        EventRegistry.RegisterHud();

        Thread.sleep(3000); // Extra time for dynmap to add the client.
    }

    @Inject(at = @At("HEAD"), method="onDisconnect")
    public void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        instance().scheduler().stop();
        ModUtils.setServerName("");
    }
}
