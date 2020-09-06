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
    Nearby nearby = new Nearby();

    static class General
    {
        boolean enableMod = true;
        boolean emcOnly = true;
    }

    static class Townless
    {
        int townlessListXPos = 770;
        int townlessListYPos = 375;

        String townlessTextColor = Formatting.LIGHT_PURPLE.getName();
        String townlessPlayerColor = Formatting.LIGHT_PURPLE.getName();
    }

    static class Nearby
    {
        int nearbyListXPos = 770;
        int nearbyListYPos = 275;

        String nearbyTextColor = Formatting.GOLD.getName();
        String nearbyPlayerColor = Formatting.GOLD.getName();

        String playerName = "";

        int xRadius = 500;
        int zRadius = 500;
    }
}