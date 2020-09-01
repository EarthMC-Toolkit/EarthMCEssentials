package net.earthmc.emc;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

import net.minecraft.util.Formatting;

@Config(name = "emc-essentials")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
    General general = new General();
    Townless townless = new Townless();

    static class General 
    {
        boolean enableMod = true;
        boolean enableTownless = true;
        boolean enableNearTo = true;
    }

    static class Townless
    {
        int townlessTextXOffset = 5;
        int townlessListYOffset = 20;
        int townlessListXOffset = 5;
        int townlessTextYOffset = 5;

        String townlessTextColor = Formatting.LIGHT_PURPLE.getName();
        String townlessPlayerColor = Formatting.LIGHT_PURPLE.getName();
    }
}