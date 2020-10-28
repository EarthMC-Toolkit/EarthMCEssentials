package net.earthmc.emc;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "emc-essentials")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
	public General general = new General();
    public Townless townless = new Townless();
    public Nearby nearby = new Nearby();
    public TownInfo townInfo = new TownInfo();
    public NationInfo nationInfo = new NationInfo();

    public static class General
    {
        public boolean enableMod = true;
        public boolean emcOnly = true;
    }

    public static class Townless
    {
        public boolean enabled = true;
        public boolean showCoords = false;

        public int townlessListXPos = 1;
        public int townlessListYPos = 16;

        public String headingTextColour = "LIGHT_PURPLE";
        public String playerTextColour = "LIGHT_PURPLE";
    }

    public static class Nearby
    {
        public boolean enabled = true;
        
        public int nearbyListXPos = 100;
        public int nearbyListYPos = 16;

        public String headingTextColour = "GOLD";
        public String playerTextColour = "GOLD";

        public String playerName = "";

        public int xRadius = 500;
        public int zRadius = 500;
    }

    public static class TownInfo
    {
        public boolean enabled = true;

        public String headingTextColour = "GREEN";
        public String infoTextColour = "GREEN";

        public int townInfoXPos = 15;
        public int townInfoYPos = 275;
    }

    public static class NationInfo
    {
        public boolean enabled = true;

        public String headingTextColour = "AQUA";
        public String infoTextColour = "AQUA";

        public int nationInfoXPos = 15;
        public int nationInfoYPos = 375;
    }
}