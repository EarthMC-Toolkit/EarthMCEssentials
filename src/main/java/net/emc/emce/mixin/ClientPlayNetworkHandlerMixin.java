package net.emc.emce.mixin;

import net.emc.emce.EMCE;
import net.emc.emce.utils.ModUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.LiteralText;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EMCE.*;
import static net.emc.emce.utils.Timers.*;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(CallbackInfo info) {
        client = MinecraftClient.getInstance();
        if (client.player != null) {
            clientName = client.player.getName().asString();
        }

        boolean shouldRender = ModUtils.shouldRender();
        EMCE.shouldRender = shouldRender;

        if (shouldRender && config.general.disableVoxelMap && !client.isInSingleplayer() && FabricLoader.getInstance().isModLoaded("voxelmap") && config.general.enableMod) {
            client.player.sendMessage(new LiteralText("§3 §6 §3 §6 §3 §6 §d§3 §6 §3 §6 §3 §6 §e"), false);
        }

        // If the timers aren't running, start them.
        if (!getRunning()) startAll();
    }
}
