package net.emc.emce.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.Config.Gui.Background;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.Colors;
import net.emc.emce.utils.EarthMCAPI;
import net.emc.emce.utils.ModUtils.State;

import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption.*;

@Config(name = "emc-essentials")
@Background(value = "cloth-config2:transparent")
public class ModConfig implements ConfigData
{
    @Category("General")
    @TransitiveObject()
    public General general = new General();

    @Category("Townless")
    @TransitiveObject()
    public Townless townless = new Townless();

    @Category("Nearby")
    @TransitiveObject()
    public Nearby nearby = new Nearby();

    @Category("Commands")
    @TransitiveObject()
    public Commands commands = new Commands();

    @Category("API")
    @TransitiveObject()
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

        @EnumHandler(option = BUTTON)
        @Comment("The position of the Townless info.")
        public State positionState = State.TOP_LEFT;

        @Comment("Note: Only used if Use Preset Positions is off.")
        public int xPos = 1;
        @Comment("Note: Only used if Use Preset Positions is off.")
        public int yPos = 16;

        @EnumHandler(option = BUTTON)
        @Comment("The colour of the 'Townless Players' text.")
        public Colors headingTextColour = Colors.BLUE;
        @EnumHandler(option = BUTTON)
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

        @EnumHandler(option = BUTTON)
        public State positionState = State.TOP_RIGHT;

        @Comment("The horizontal position on the HUD.")
        public int xPos = 100;
        @Comment("The vertical position on the HUD.")
        public int yPos = 16;

        @EnumHandler(option = BUTTON)
        @Comment("The colour of the 'Nearby Players' text.")
        public Colors headingTextColour = Colors.GOLD;
        @EnumHandler(option = BUTTON)
        @Comment("The colour of nearby player' names.")
        public Colors playerTextColour = Colors.GOLD;

        // Independent scaling - each axis can be same or different.
        @Comment("The amount of blocks to check on the X axis.")
        @BoundedDiscrete(min = 50, max = 10000)
        public int xBlocks = 500;
        @BoundedDiscrete(min = 50, max = 10000)
        @Comment("The amount of blocks to check on the Z axis.")
        public int zBlocks = 500;
    }

    public static class Commands {
        @EnumHandler(option = BUTTON)
        @Comment("The colour of the townless players text.")
        public Colors townlessTextColour = Colors.LIGHT_PURPLE;
        @EnumHandler(option = BUTTON)
        @Comment("The colour of the town info text.")
        public Colors townInfoTextColour = Colors.GREEN;
        @EnumHandler(option = BUTTON)
        @Comment("The colour of the nation info text.")
        public Colors nationInfoTextColour = Colors.AQUA;
    }

    public static class API {
        @Comment("The interval (in seconds) at which nearby data will be updated.")
        @BoundedDiscrete(min = 10, max = 120)
        public int nearbyInterval = 20;
        @Comment("The interval (in seconds) at which townless data will be updated.")
        @BoundedDiscrete(min = 30, max = 300)
        public int townlessInterval = 60;

        @CollapsibleObject
        @Comment("Main settings for the API. Do not touch unless you know what you're doing!")
        public Main main = new Main();

        @CollapsibleObject
        @Comment("Configures routes for the API. Do not touch unless you know what you're doing!")
        public Routes routes = new Routes();

        public static class Main {
            public String domain = "http://earthmcstats.ddns.net/api/v1/";

            public String domain() {
                if (!EarthMCAPI.urlSchemePattern.matcher(domain).find()) return "http://" + domain;
                else return domain;
            }
        }

        public static class Routes {
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