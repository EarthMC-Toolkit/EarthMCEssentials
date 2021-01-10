package net.earthmc.emc.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.earthmc.emc.utils.Timers.*;
import static net.earthmc.emc.EMCMod.*;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(CallbackInfo info)
    {
        client = MinecraftClient.getInstance();
        if (client.player != null) clientName = client.player.getName().asString();

        // Return if timers are already running.
        if (running) return;

        // Start the timers (also sets running to true)
        startAll();
    }
}
