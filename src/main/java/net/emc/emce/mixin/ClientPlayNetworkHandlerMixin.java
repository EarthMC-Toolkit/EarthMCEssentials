package net.emc.emce.mixin;

import net.emc.emce.utils.Timers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EMCE.*;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(CallbackInfo info)
    {
        client = MinecraftClient.getInstance();

        if (client.player != null)
            clientName = client.player.getName().asString();

        // If the timers aren't running, start them.
        if (!Timers.getRunning()) Timers.startAll();
    }
}
