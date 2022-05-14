package net.emc.emce.mixin;

import com.mojang.bridge.game.GameSession;
import net.emc.emce.object.APIData;
import net.emc.emce.object.APIRoute;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.emc.emce.EarthMCEssentials.instance;
import static net.emc.emce.utils.EarthMCAPI.*;

@Mixin(MinecraftClientGame.class)
public abstract class SessionEventListenerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method="onStartGameSession")
    public void onStartGameSession(CallbackInfo ci) {
        // Check API if client in Nova, if not, check Aurora, if not, assume in Queue.
        String clientName = client.player.getName().asString();
        if (playerOnline("nova", clientName)) {
            APIData.setMap("nova");
            return;
        }
        if (playerOnline("aurora", clientName)) APIData.setMap("aurora");
        
        instance().setShouldRender(ModUtils.shouldRender());
    }
}