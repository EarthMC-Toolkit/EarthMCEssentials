package net.earthmc.emc.mixin;

import com.google.gson.JsonObject;
import net.earthmc.emc.EMCMod;
import net.earthmc.emc.utils.ConfigUtils;
import net.earthmc.emc.utils.EmcApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.Timer;
import java.util.TimerTask;

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

        EMCMod.clientName = EMCMod.client.player.getName().asString();
        EMCMod.config.nearby.playerName = EMCMod.clientName;

        JsonObject resident = EmcApi.getResident(EMCMod.clientName);

        EMCMod.clientTownName = resident.get("town").getAsString();
        EMCMod.config.townInfo.townName = EMCMod.clientTownName;

        EMCMod.clientNationName = resident.get("nation").getAsString();
        EMCMod.config.nationInfo.nationName = EMCMod.clientNationName;

        ConfigUtils.serializeConfig(EMCMod.config);

        // #region Create Timers
        final Timer townlessTimer = new Timer();
        final Timer nearbyTimer = new Timer();

        townlessTimer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run() 
            {
                if (EMCMod.config.general.enableMod && EMCMod.config.townless.enabled) EMCMod.nearby = EmcApi.getTownless();
            }
        }, 0, 2 * 60 * 1000);

        nearbyTimer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run()
            {
                if (EMCMod.config.general.enableMod && EMCMod.config.nearby.enabled) EMCMod.nearby = EmcApi.getNearby(EMCMod.config);
            }
        }, 0, 10 * 1000);
        // #endregion
    }
}
