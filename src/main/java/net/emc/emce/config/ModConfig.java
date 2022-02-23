package net.emc.emce.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.Colors;
import net.emc.emce.utils.ModUtils.State;

@Config(name = "emc-essentials")
@Config.Gui.Background(value = "cloth-config2:transparent")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.TransitiveObject()
    public General general = new General();

    @ConfigEntry.Category("Townless")
    @ConfigEntry.Gui.TransitiveObject()
    public Townless townless = new Townless();

    @ConfigEntry.Category("Nearby")
    @ConfigEntry.Gui.TransitiveObject()
    public Nearby nearby = new Nearby();

    @ConfigEntry.Category("Commands")
    @ConfigEntry.Gui.TransitiveObject()
    public Commands commands = new Commands();

    @ConfigEntry.Category("API")
    @ConfigEntry.Gui.TransitiveObject()
    public API api = new API();

    public static class General {
        @Comment("Toggles the mod on or off.")
        public boolean enableMod = true;
        @Comment("If enabled, overlays only render while you are on EarthMC.")
        public boolean emcOnly = true;
    }

    public static class Townless {
        @Comment("Toggles townless players on or off.")
        public boolean enabled = true;
        @Comment("The maximum length the townless list can be. < 1 for no limit.")
        public int maxLength = 0; // < 1 = No limit
        @Comment("Toggles the use of preset positions, uses sliders if off.")
        public boolean presetPositions = true;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The position of the Townless info.")
        public State positionState = State.TOP_LEFT;

        @Comment("Note: Only used if Use Preset Positions is off.")
        public int xPos = 1;
        @Comment("Note: Only used if Use Preset Positions is off.")
        public int yPos = 16;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The colour of the 'Townless Players' text.")
        public Colors headingTextColour = Colors.BLUE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The colour of the townless player names.")
        public Colors playerTextColour = Colors.BLUE;
    }

    public static class Nearby {
        @Comment("Toggles the nearby players overlay on or off.")
        public boolean enabled = true;
        @Comment("Toggles the showing of players ranks before their names.")
        public boolean showRank = false;
        @Comment("Toggles the use of preset positions, uses sliders if off.")
        public boolean presetPositions = true;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public State positionState = State.TOP_RIGHT;

        @Comment("The horizontal position on the HUD.")
        public int xPos = 100;
        @Comment("The vertical position on the HUD.")
        public int yPos = 16;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The colour of the 'Nearby Players' text.")
        public Colors headingTextColour = Colors.GOLD;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The colour of nearby player' names.")
        public Colors playerTextColour = Colors.GOLD;

        // Independent scaling - each axis can be same or different.
        @Comment("The amount of blocks to check on the X axis.")
        @ConfigEntry.BoundedDiscrete(min = 50, max = 10000)
        public int xBlocks = 500;
        @ConfigEntry.BoundedDiscrete(min = 50, max = 10000)
        @Comment("The amount of blocks to check on the Z axis.")
        public int zBlocks = 500;
    }

    public static class Commands {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The colour of the townless players text.")
        public Colors townlessTextColour = Colors.LIGHT_PURPLE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The colour of the town info text.")
        public Colors townInfoTextColour = Colors.GREEN;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @Comment("The colour of the nation info text.")
        public Colors nationInfoTextColour = Colors.AQUA;
    }

    public static class API {
        @Comment("The interval (in seconds) at which townless data will be updated.")
        @ConfigEntry.BoundedDiscrete(min = 15, max = 600)
        public int nearbyInterval = 30;
        @Comment("The interval (in seconds) at which nearby data will be updated.")
        @ConfigEntry.BoundedDiscrete(min = 30, max = 600)
        public int townlessInterval = 60;

        @ConfigEntry.Gui.CollapsibleObject
        @Comment("Configures routes for the API. Do not touch unless you know what you're doing!")
        public RouteSettings routes = new RouteSettings();

        public static class RouteSettings {
            public String domain = "http://earthmcstats.sly.io/api/v1/";
            public String townless = "townlessplayers/";
            public String nations = "nations/";
            public String towns = "towns/";
            public String resident = "residents/";
            public String nearby = "nearby/";
            public String serverInfo = "serverinfo/";
        }
    }

    public static ModConfig instance() {
        return EarthMCEssentials.instance().getConfig();
    }
}