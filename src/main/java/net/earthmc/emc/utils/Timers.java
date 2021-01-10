package net.earthmc.emc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import static net.earthmc.emc.EMCMod.*;

public class Timers
{
    public static boolean running;

    public static Timer queueTimer;
    public static Timer nearbyTimer;
    public static Timer townlessTimer;
    public static Timer infoTimer;

    public static void setRunning(boolean value){
        running = value;
    }

    public static void startTownless(int delay, int period)
    {
        townlessTimer = new Timer();

        townlessTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
                if (!config.general.enableMod && townless.size() == 0) return;
                if (config.townless.enabled) townless = EmcApi.getTownless();
            }
        }, delay, period);
    }

    public static void startNearby(int delay, int period)
    {
        nearbyTimer = new Timer();

        nearbyTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
                if (!config.general.enableMod && nearby.size() == 0) return;
                if (config.nearby.enabled) nearby = EmcApi.getNearby(config);
            }
        }, delay, period);
    }

    public static void startQueue(int delay, int period)
    {
        queueTimer = new Timer();

        queueTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                JsonObject serverInfo = EmcApi.getServerInfo();
                JsonElement serverOnline = serverInfo.get("serverOnline");

                if (serverOnline != null && serverOnline.getAsBoolean()) queue = serverInfo.get("queue").getAsString();
            }
        }, delay, period);
    }

    public static void startInfo(int delay, int period)
    {
        infoTimer = new Timer();

        infoTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                JsonArray nations = EmcApi.getNations();
                JsonArray towns = EmcApi.getTowns();
                JsonObject resident = EmcApi.getResident(clientName);

                if (resident.has("name")) {
                    clientNationName = resident.get("nation").getAsString();
                    clientTownName = resident.get("town").getAsString();
                }

                if (nations.size() != 0)
                    allNations = nations;

                if (towns.size() != 0)
                    allTowns = towns;
            }
        }, delay, period);
    }

    public static void startAll()
    {
        setRunning(true);

        startInfo(0, 2*60*1000);
        startTownless(0, 60*1000);
        startNearby(0, 10*1000);
        startQueue(0, 10*1000);
    }

    public static void restart(Timer timer)
    {
        timer.cancel();

        if (timer.equals(infoTimer)) startInfo(0, 2*60*1000);
        else if (timer.equals(townlessTimer)) startTownless(0, 60*1000);
        else if (timer.equals(nearbyTimer)) startNearby(0, 10*1000);
        else if (timer.equals(queueTimer)) startQueue(0, 10*1000);
    }

    public static void restartAll()
    {
        infoTimer.cancel();
        townlessTimer.cancel();
        nearbyTimer.cancel();
        queueTimer.cancel();

        startAll();
    }

    public static void stop()
    {
        infoTimer.cancel();
        townlessTimer.cancel();
        nearbyTimer.cancel();
        queueTimer.cancel();

        setRunning(false);
    }
}
