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

        ConfigUtils.serializeConfig(EMCMod.config);

        // #region Create Timers
        final Timer townlessTimer = new Timer();
        final Timer nearbyTimer = new Timer();
        final Timer townNationTimer = new Timer();

        townlessTimer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run() 
            {
                if (EMCMod.config.general.enableMod)
                {
                    if (EMCMod.config.townless.enabled) EMCMod.townless = EmcApi.getTownless();
                }
            }
        }, 0, 2 * 60 * 1000);

        nearbyTimer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run()
            {
                if (EMCMod.config.general.enableMod)
                {
                    if (EMCMod.config.nearby.enabled) EMCMod.nearby = EmcApi.getNearby(EMCMod.config);
                }
            }
        }, 0, 10 * 1000);

        townNationTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (EMCMod.config.general.enableMod)
                {
                    JsonObject resident = EmcApi.getResident(EMCMod.clientName);

                    // Resident exists
                    if (resident.has("name"))
                    {
                        if (EMCMod.config.townInfo.enabled)
                        {
                            EMCMod.clientTownName = resident.get("town").getAsString();
                            EMCMod.townInfo = EmcApi.getTown(EMCMod.clientTownName);
                        }

                        if (EMCMod.config.nationInfo.enabled)
                        {
                            EMCMod.clientNationName = resident.get("nation").getAsString();
                            EMCMod.nationInfo = EmcApi.getNation(EMCMod.clientNationName);

                            System.out.println(EMCMod.nationInfo);
                        }
                    }
                }
            }
        }, 0, 60 * 1000);
        // #endregion
    }
}
