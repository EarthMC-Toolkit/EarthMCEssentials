package net.emc.emce.mixins;

import com.google.gson.JsonElement;

import net.emc.emce.EMCEssentials;
import net.emc.emce.utils.OAPIV3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("TAIL"), method="<init>")
    private void onInit(RunArgs args, CallbackInfo ci) {
        String clientName = args.network.session.getUsername();
        JsonElement clientPlayer = OAPIV3.getPlayer(clientName);

        if (clientPlayer != null) {
            EMCEssentials.instance().setClientPlayer(clientPlayer);
            System.out.println("onInit: Set clientPlayer");

            return;
        }

        System.out.println("Could not find player by client name: " + clientName);
    }
}