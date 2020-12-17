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
        if (EMCMod.timersActivated) return;
        EMCMod.timersActivated = true;

        EMCMod.client = MinecraftClient.getInstance();
        EMCMod.clientName = EMCMod.client.player.getName().asString();

        ConfigUtils.serializeConfig(EMCMod.config);

        // #region Create Timers
        final Timer twoMinuteTimer = new Timer();
        final Timer tenSecondTimer = new Timer();

        twoMinuteTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() 
            {
                if (EMCMod.config.general.enableMod)
                {
                    if (EMCMod.config.townless.enabled) EMCMod.townless = EmcApi.getTownless();

                    JsonObject resident = EmcApi.getResident(EMCMod.clientName);

                    // Resident exists
                    if (resident.has("name"))
                    {
                        if (EMCMod.config.townInfo.enabled)
                        {
                            EMCMod.clientTownName = resident.get("town").getAsString();
                            JsonObject town = EmcApi.getTown(EMCMod.clientTownName);

                            if (town != null) EMCMod.townInfo = town;
                        }
                        
                        if (EMCMod.config.nationInfo.enabled)
                        {
                            EMCMod.clientNationName = resident.get("nation").getAsString();
                            JsonObject nation = EmcApi.getNation(EMCMod.clientNationName);

                            if (nation != null) EMCMod.nationInfo = nation;
                        }
                    }
                }
            }
        }, 0, 2 * 60 * 1000);

        tenSecondTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (EMCMod.config.general.enableMod)
                {
                    if (EMCMod.config.nearby.enabled) EMCMod.nearby = EmcApi.getNearby(EMCMod.config);
                    //if (EMCMod.config.townEvents.enabled) EMCMod.towns = EmcApi.getTowns();
                }
            }
        }, 0, 10 * 1000);
        // #endregion
    }
}
