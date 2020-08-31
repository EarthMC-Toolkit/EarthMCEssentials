package net.earthmc.emc;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "emc")
public class ModConfig implements ConfigData
{
    boolean toggleA = true;
    boolean toggleB = false;
        
    @ConfigEntry.Gui.CollapsibleObject
    InnerStuff stuff = new InnerStuff();
        
    @ConfigEntry.Gui.Excluded
    InnerStuff invisibleStuff = new InnerStuff();
        
    static class InnerStuff
    {
        int a = 0;
        int b = 1;
    }
}