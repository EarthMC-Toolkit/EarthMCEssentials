package net.earthmc.emc.mixin;

import com.google.gson.JsonObject;
import net.earthmc.emc.EMCMod;
import net.earthmc.emc.utils.ConfigUtils;
import net.earthmc.emc.utils.EmcApi;
import net.earthmc.emc.utils.ModUtils;
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
    String serverName;

    @Inject(at = @At("TAIL"), method="onGameJoin")
    private void onGameJoin(CallbackInfo info)
    {
        EMCMod.client = MinecraftClient.getInstance();

        serverName = ModUtils.getServerName();

        if (serverName.endsWith("earthmc.net") && EMCMod.config.general.emcOnly)
            EMCMod.shouldRender = true;
        else if (!serverName.endsWith("earthmc.net") && !EMCMod.config.general.emcOnly)
            EMCMod.shouldRender = true;
        else EMCMod.shouldRender = serverName.endsWith("earthmc.net") && EMCMod.config.general.emcOnly;

        if (EMCMod.client.player != null) EMCMod.clientName = EMCMod.client.player.getName().asString();

        ConfigUtils.serializeConfig(EMCMod.config);

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

                JsonObject resident = EmcApi.getResident(EMCMod.clientName);

                // Resident exists
                if (resident.has("name"))
                {
                    if (EMCMod.config.townInfo.enabled)
                    {
                        EMCMod.clientTownName = resident.get("town").getAsString();
                        JsonObject town = EmcApi.getTown(EMCMod.clientTownName);

                        if (!town.entrySet().isEmpty()) EMCMod.townInfo = town;
                    }
                        
                    if (EMCMod.config.nationInfo.enabled)
                    {
                        EMCMod.clientNationName = resident.get("nation").getAsString();
                        JsonObject nation = EmcApi.getNation(EMCMod.clientNationName);

                        if (!nation.entrySet().isEmpty()) EMCMod.nationInfo = nation;
                    }
                }
            }
        }, 0, 2 * 60 * 1000);

        tenSecondTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (!EMCMod.config.general.enableMod && EMCMod.nearby.size() == 0) return;
                if (EMCMod.config.nearby.enabled) EMCMod.nearby = EmcApi.getNearby(EMCMod.config);

                serverName = ModUtils.getServerName();

                // Uses endsWith because EMC has 2 valid IPs (earthmc.net & play.earthmc.net)
                if (serverName.endsWith("earthmc.net") && EMCMod.config.general.emcOnly)
                    EMCMod.shouldRender = true;
                else if (!serverName.endsWith("earthmc.net") && !EMCMod.config.general.emcOnly)
                    EMCMod.shouldRender = true;
                else EMCMod.shouldRender = serverName.endsWith("earthmc.net") && EMCMod.config.general.emcOnly;

                JsonObject serverInfo = EmcApi.getServerInfo();
                if (serverInfo.get("serverOnline").getAsBoolean()) EMCMod.queue = serverInfo.get("queue").getAsString();
            }
        }, 0, 10 * 1000);
        // #endregion
    }
}
