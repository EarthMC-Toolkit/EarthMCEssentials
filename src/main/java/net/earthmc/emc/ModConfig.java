package net.earthmc.emc;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "emc-essentials")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
    General general = new General();
    Townless townless = new Townless();

    static class General 
    {
        boolean enableMod = true;
    }

    static class Townless
    {
        boolean enableTownless = true;
        int townlessTextXOffset = 5;
        int townlessListYOffset = 20;
        int townlessListXOffset = 5;
        int townlessTextYOffset = 5;
    }
}