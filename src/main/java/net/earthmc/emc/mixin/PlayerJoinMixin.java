package net.earthmc.emc.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.earthmc.emc.EMCMod;
import net.earthmc.emc.utils.EmcApi;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@org.spongepowered.asm.mixin.Mixin(PlayerManager.class)
public class PlayerJoinMixin
{
    @Inject(at = @At("TAIL"), method="onPlayerConnect")
    private void playerConnected(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info)
    {
        if (!EMCMod.config.townless.autoInvite || EMCMod.clientName == player.getName().asString()) return;

        JsonArray townless = EmcApi.getTownless();

        for (int i = 0; i < townless.size(); i++)
        {
            final JsonObject currentPlayer = (JsonObject) townless.get(i);
            final String currentPlayerName = currentPlayer.get("name").getAsString();

            if (currentPlayerName.equals(player.getName().asString()))
            {
                EMCMod.client.player.sendChatMessage("/t invite " + currentPlayerName);
                break;
            }
        }
    }
}