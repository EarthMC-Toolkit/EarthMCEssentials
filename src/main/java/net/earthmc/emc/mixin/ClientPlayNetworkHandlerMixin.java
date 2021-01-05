package net.earthmc.emc.mixin;

import net.earthmc.emc.EMCMod;
import net.earthmc.emc.utils.TimerTasks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(CallbackInfo info)
    {
        EMCMod.client = MinecraftClient.getInstance();
        if (EMCMod.client.player != null) EMCMod.clientName = EMCMod.client.player.getName().asString();

        // Return if timers are already running.
        if (EMCMod.timersActivated) return;
        EMCMod.timersActivated = true;

        TimerTasks.startTimers();
    }
}
