package net.earthmc.emc.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.earthmc.emc.EMCMod;
import net.earthmc.emc.utils.EmcApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Timer;
import java.util.TimerTask;

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

        // #region Create Timers
        final Timer twoMinuteTimer = new Timer();
        final Timer tenSecondTimer = new Timer();

        twoMinuteTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() 
            {
                if (!EMCMod.config.general.enableMod && EMCMod.townless.size() == 0) return;
                
                if (EMCMod.config.townless.enabled) EMCMod.townless = EmcApi.getTownless();

                JsonArray nations = EmcApi.getNations();
                JsonArray towns = EmcApi.getTowns();
                JsonObject resident = EmcApi.getResident(EMCMod.clientName);

                if (resident.has("name")) {
                    EMCMod.clientNationName = resident.get("nation").getAsString();
                    EMCMod.clientTownName = resident.get("town").getAsString();
                }

                if (nations.size() != 0) 
                    EMCMod.allNations = nations;

                if (towns.size() != 0) 
                    EMCMod.allTowns = towns;
            }
        }, 0, 2 * 60 * 1000);

        tenSecondTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (!EMCMod.config.general.enableMod && EMCMod.nearby.size() == 0) return;
                if (EMCMod.config.nearby.enabled) EMCMod.nearby = EmcApi.getNearby(EMCMod.config);

                JsonObject serverInfo = EmcApi.getServerInfo();
                JsonElement serverOnline = serverInfo.get("serverOnline");

                if (serverOnline != null && serverOnline.getAsBoolean()) EMCMod.queue = serverInfo.get("queue").getAsString();
            }
        }, 0, 10 * 1000);
        // #endregion
    }
}
