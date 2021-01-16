package net.emc.emce;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.emc.emce.utils.ModUtils;

@Config(name = "emc-essentials")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
	public General general = new General();
    public Townless townless = new Townless();
    public Nearby nearby = new Nearby();
    public Commands commands = new Commands();
    public API api = new API();

    public static class General
    {
        public boolean enableMod = true;
        public boolean emcOnly = true;
    }

    public static class Townless
    {
        public boolean enabled = true;
        public boolean presetPositions = true;

        public ModUtils.State positionState = ModUtils.State.TOP_LEFT;

        public int xPos = 1;
        public int yPos = 16;
        public int maxLength = 0; // < 1 = No limit

        public String headingTextColour = "BLUE";
        public String playerTextColour = "BLUE";
    }

    public static class Nearby
    {
        public boolean enabled = true;
        public boolean showRank = true;
        public boolean presetPositions = true;

        public ModUtils.State positionState = ModUtils.State.TOP_RIGHT;

        public int xPos = 100;
        public int yPos = 16;

        public String headingTextColour = "GOLD";
        public String playerTextColour = "GOLD";

        public ModUtils.ScaleMethod scaleMethod = ModUtils.ScaleMethod.Proportionate;

        // Proportionate scaling - both axes must be same.
        public int radius = 500;

        // Independent scaling - each axis can be same or different.
        public int xBlocks = 500;
        public int zBlocks = 500;
    }

    public static class Commands
    {
        public String townlessTextColour = "LIGHT_PURPLE";
        public String townInfoTextColour = "GREEN";
        public String nationInfoTextColour = "AQUA";
    }

    public static class API
    {
        public int queueInterval = 5;
        public int nearbyInterval = 10;
        public int townlessInterval = 60;
        public int residentInfoInterval = 60;
        public int townNationInfoInterval = 120;
    }
}