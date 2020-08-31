package net.earthmc.emc;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "emc-essentials")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
    General general = new General();
    TownlessText townlessText = new TownlessText();
    TownlessList townlessList = new TownlessList();

    static class General 
    {
        boolean enableMod = true;
    }

    static class TownlessText 
    {
        int townlessTextXOffset = 5;
        int townlessTextYOffset = 5;
    }

    static class TownlessList 
    {
        int townlessListXOffset = 5;
        int townlessListYOffset = 20;
    }
}