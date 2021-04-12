package net.emc.emce.mixin;

import net.emc.emce.EMCE;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
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
        MsgUtils.sendDebugMessage("Connected to server. Is on EMC: " + ModUtils.isConnectedToEMC(ModUtils.getServerName()));

        if (shouldRender && config.general.enableMod) {
            if (config.general.disableVoxelMap && !client.isInSingleplayer() && FabricLoader.getInstance().isModLoaded("voxelmap")) {
                if (client.player != null) {
                    client.player.sendMessage(new LiteralText("§3 §6 §3 §6 §3 §6 §d§3 §6 §3 §6 §3 §6 §e"), false);
                    MsgUtils.sendPlayer("msg_voxelmap_disabled", false, Formatting.AQUA, true);
                }
            }
        }

        // If the timers aren't running, start them.
        if (!getRunning()) startAll();
    }
}
