package net.earthmc.emc;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;

@Config(name = "earthmcessentials")
public class ModConfig implements ConfigData
{ 
    @ConfigEntry.Gui.CollapsibleObject
    int townlessTextXOffset = 5;
    int townlessTextYOffset = 5;

    @ConfigEntry.Gui.CollapsibleObject
    int townlessListXOffset = 5;
    int townlessListYOffset = 20;
}