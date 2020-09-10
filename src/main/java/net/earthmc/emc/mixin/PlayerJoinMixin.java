package net.earthmc.emc.mixin;

import net.earthmc.emc.EMCMod;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@org.spongepowered.asm.mixin.Mixin(PlayerManager.class)
public class PlayerJoinMixin
{
    @Inject(at = @At("TAIL"), method="onPlayerConnect")
    private void playerConnected(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info)
    {
        if (EMCMod.client == null || !EMCMod.config.general.joinMessages) return;

        String playerName = new LiteralText(player.getName().getString()).formatted(Formatting.AQUA).asFormattedString();
        String joinedText = new LiteralText(" has joined the game.").formatted( Formatting.YELLOW).asFormattedString();

        EMCMod.client.player.sendMessage(new LiteralText(playerName + joinedText));
    }
}
