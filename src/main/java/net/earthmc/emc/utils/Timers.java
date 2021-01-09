package net.earthmc.emc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.earthmc.emc.EMCMod;

import java.util.Timer;
import java.util.TimerTask;

public class Timers
{
    public static boolean running;

    static Timer twoMinuteTimer = new Timer();
    static Timer tenSecondTimer = new Timer();

    public static void setRunning(boolean value){
        running = value;
    }

    public static void start()
    {
        setRunning(true);

        twoMinuteTimer = new Timer();
        tenSecondTimer = new Timer();

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
        }, 0L, 2 * 60 * 1000L);

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
    } 

    public static void restart()
    {
        twoMinuteTimer.cancel();
        tenSecondTimer.cancel();
        start();
    }

    public static void stop()
    {
        twoMinuteTimer.cancel();
        tenSecondTimer.cancel();

        setRunning(false);
    }
}
