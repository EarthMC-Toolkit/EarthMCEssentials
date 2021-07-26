package net.emc.emce.tasks;

import net.emc.emce.EarthMCEssentials;

import java.util.Timer;
import java.util.TimerTask;

public class Timers {
    public static Timer serverDataTimer, nearbyTimer, townlessTimer, townyData;
    private static boolean running;

    private static void setRunning(boolean value) {
        running = value;
    }

    public static boolean getRunning() {
        return running;
    }

    public static void startTownless(int delay, int period) {
        setRunning(true);
        townlessTimer = new Timer("townless", true);

        townlessTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
                if (EarthMCEssentials.getConfig().general.enableMod && EarthMCEssentials.getConfig().townless.enabled && shouldRun())
                    new TownlessTask().start();
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
                if (EarthMCEssentials.getConfig().general.enableMod && EarthMCEssentials.getConfig().nearby.enabled && shouldRun())
                    new NearbyTask().start();
            }
        }, delay, period);
    }

    public static void startServerData(int delay, int period) {
        setRunning(true);
        serverDataTimer = new Timer("queue", true);

        serverDataTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (EarthMCEssentials.getConfig().general.enableMod && shouldRun())
                    new ServerDataTask().start();
            }
        }, delay, period);
    }

    public static void startTownyData(int delay, int period)
    {
        setRunning(true);
        townyData = new Timer("townNationInfo", true);

        townyData.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (EarthMCEssentials.getConfig().general.enableMod && shouldRun())
                    new TownyDataTask().start();
            }
        }, delay, period);
    }

    public static void startAll() {
        if (running) return;
            setRunning(true);

        startTownyData(0, Math.max(EarthMCEssentials.getConfig().api.townyDataInterval * 1000, 90000));
        startTownless(0, Math.max(EarthMCEssentials.getConfig().api.townlessInterval * 1000, 45000));
        startNearby(0, Math.max(EarthMCEssentials.getConfig().api.nearbyInterval * 1000, 20000));
        startServerData(0, Math.max(EarthMCEssentials.getConfig().api.serverDataInterval * 1000, 90000));
    }

    public static void stopAll() {
        if (!running) return;

        townyData.cancel();
        townlessTimer.cancel();
        nearbyTimer.cancel();
        serverDataTimer.cancel();

        setRunning(false);
    }

    public static void restartTimer(Timer timer) {
        if (!running)
            return;
        
        timer.cancel();

        if (timer.equals(townyData)) startTownyData(0, Math.max(EarthMCEssentials.getConfig().api.townyDataInterval * 1000, 90000));
        else if (timer.equals(townlessTimer)) startTownless(0, Math.max(EarthMCEssentials.getConfig().api.townlessInterval * 1000, 45000));
        else if (timer.equals(nearbyTimer)) startNearby(0, Math.max(EarthMCEssentials.getConfig().api.nearbyInterval * 1000, 20000));
        else if (timer.equals(serverDataTimer)) startServerData(0, Math.max(EarthMCEssentials.getConfig().api.serverDataInterval * 1000, 90000));
        else throw new IllegalStateException("Unexpected value: " + timer.getClass().getName());
    }

    public static void restartAll() {
        stopAll();
        startAll();
    }

    private static boolean shouldRun() {
        return !EarthMCEssentials.getClient().isPaused() && EarthMCEssentials.getClient().isWindowFocused();
    }
}
