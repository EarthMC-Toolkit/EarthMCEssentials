package net.earthmc.emc;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.earthmc.emc.utils.ModUtils;

@Config(name = "emc-essentials")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
	public General general = new General();
    public Townless townless = new Townless();
    public Nearby nearby = new Nearby();
    public Commands commands = new Commands();

    public static class General
    {
        public boolean enableMod = true;
        public boolean emcOnly = true;
    }

    public static class Townless
    {
        public boolean enabled = true;
        public boolean presetPositions = true;

        public int xPos = 1;
        public int yPos = 16;
        public int maxLength = 0; // < 1 = No limit

        public String headingTextColour = "LIGHT_PURPLE";
        public String playerTextColour = "LIGHT_PURPLE";

        public ModUtils.State positionState = ModUtils.State.TOP_LEFT;
    }

    public static class Nearby
    {
        public boolean enabled = true;
        public boolean presetPositions = true;
        
        public int xPos = 100;
        public int yPos = 16;

        public String headingTextColour = "GOLD";
        public String playerTextColour = "GOLD";

        public int xRadius = 500;
        public int zRadius = 500;

        public ModUtils.State positionState = ModUtils.State.TOP_RIGHT;
    }

    public static class Commands
    {
        public String townInfoTextColour = "GREEN";
        public String nationInfoTextColour = "AQUA";
    }
}