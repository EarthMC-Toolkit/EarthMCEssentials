package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import static net.emc.emce.EMCE.*;
import static net.emc.emce.utils.EmcApi.*;

public class Timers
{
    public static Timer queueTimer, nearbyTimer, townlessTimer, residentInfoTimer, townNationInfo;

    private static boolean running;

    public static void setRunning(boolean value){
        running = value;
    }
    public static boolean getRunning(){
        return running;
    }

    public static void startTownless(int delay, int period)
    {
        setRunning(true);
        townlessTimer = new Timer("townless", true);

        townlessTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
                if (!config.general.enableMod && townless.size() == 0) return;
                if (config.townless.enabled) townless = getTownless();
            }
        }, delay, period);
    }

    public static void startNearby(int delay, int period)
    {
        setRunning(true);
        nearbyTimer = new Timer("nearby", true);

        nearbyTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
                if (!config.general.enableMod && nearby.size() == 0) return;
                if (config.nearby.enabled)
                    nearby = getNearby(config.nearby.xBlocks, config.nearby.zBlocks);
                    nearbySurrounding = getNearby(32, 32);
            }
        }, delay, period);
    }

    public static void startQueue(int delay, int period)
    {
        setRunning(true);
        queueTimer = new Timer("queue", true);

        queueTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                JsonObject serverInfo = getServerInfo();
                JsonElement serverOnline = serverInfo.get("serverOnline");

                if (serverOnline != null && serverOnline.getAsBoolean()) queue = serverInfo.get("queue").getAsInt();
            }
        }, delay, period);
    }

    public static void startTownNationInfo(int delay, int period)
    {
        setRunning(true);
        townNationInfo = new Timer("townNationInfo", true);

        townNationInfo.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                JsonArray nations = getNations();
                JsonArray towns = getTowns();

                if (nations.size() != 0)
                    allNations = nations;

                if (towns.size() != 0)
                    allTowns = towns;
            }
        }, delay, period);
    }

    public static void startResidentInfo(int delay, int period)
    {
        setRunning(true);
        residentInfoTimer = new Timer("residentInfo", true);

        residentInfoTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                JsonObject resident = getResident(clientName);

                if (resident.has("name")) {
                    clientNationName = resident.get("nation").getAsString();
                    clientTownName = resident.get("town").getAsString();
                }
            }
        }, delay, period);
    }

    public static void startAll()
    {
        if (running) return;
        setRunning(true);

        startTownNationInfo(0, 2*60*1000);
        startResidentInfo(0, 60*1000);
        startTownless(0, 60*1000);
        startNearby(0, 10*1000);
        startQueue(0, 5*1000);
    }

    public static void stopAll()
    {
        if (!running) return;

        townNationInfo.cancel();
        residentInfoTimer.cancel();
        townlessTimer.cancel();
        nearbyTimer.cancel();
        queueTimer.cancel();

        setRunning(false);
    }

    public static void restartTimer(Timer timer)
    {
        timer.cancel();

        if (timer.equals(townNationInfo)) startTownNationInfo(0, 2*60*1000);
        else if (timer.equals(residentInfoTimer)) startResidentInfo(0, 60*1000);
        else if (timer.equals(townlessTimer)) startTownless(0, 60*1000);
        else if (timer.equals(nearbyTimer)) startNearby(0, 10*1000);
        else if (timer.equals(queueTimer)) startQueue(0, 5*1000);
        else throw new IllegalStateException("Unexpected value: " + timer.getClass().getName());
    }

    public static void restartAll()
    {
        stopAll();
        startAll();
    }
}
