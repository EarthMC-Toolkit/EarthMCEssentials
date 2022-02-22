package net.emc.emce.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.ModUtils.State;

@Config(name = "emc-essentials")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
	public General general = new General();
    public Townless townless = new Townless();
    public Nearby nearby = new Nearby();
    public Commands commands = new Commands();
    public API api = new API();

    public static class General {
        public boolean enableMod = true;
        public boolean emcOnly = true;
    }

    public static class Townless {
        public boolean enabled = true;
        public boolean presetPositions = true;

        public State positionState = State.TOP_LEFT;

        public int xPos = 1;
        public int yPos = 16;
        public int maxLength = 0; // < 1 = No limit

        public String headingTextColour = "BLUE";
        public String playerTextColour = "BLUE";
    }

    public static class Nearby {
        public boolean enabled = true;
        public boolean showRank = false;
        public boolean presetPositions = true;

        public State positionState = State.TOP_RIGHT;

        public int xPos = 100;
        public int yPos = 16;

        public String headingTextColour = "GOLD";
        public String playerTextColour = "GOLD";

        // Independent scaling - each axis can be same or different.
        public int xBlocks = 500;
        public int zBlocks = 500;
    }

    public static class Commands {
        public String townlessTextColour = "LIGHT_PURPLE";
        public String townInfoTextColour = "GREEN";
        public String nationInfoTextColour = "AQUA";
    }

    public static class API {
        public int nearbyInterval = 20;
        public int townlessInterval = 45;

        public String apiDomain = "http://earthmcstats.sly.io/api/v1/";
        public String townlessRoute = "townlessplayers/";
        public String nationsRoute = "nations/";
        public String townsRoute = "towns/";
        public String residentsRoute = "residents/";
        public String nearbyRoute = "nearby/";
        public String serverInfoRoute = "serverinfo/";
    }

    public static ModConfig instance() {
        return EarthMCEssentials.instance().getConfig();
    }
}