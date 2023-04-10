package net.emc.emce.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.Config.Gui.Background;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.objects.Colors;

import net.emc.emce.utils.ModUtils.NearbySort;
import net.emc.emce.utils.ModUtils.State;

import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON;
import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN;

@SuppressWarnings("CanBeFinal")
@Config(name = "emc-essentials")
@Background(value = "cloth-config2:transparent")
public class ModConfig implements ConfigData {
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

    @Category("Intervals")
    @TransitiveObject()
    public Intervals intervals = new Intervals();

    public static class General {
        @Comment("Toggles the mod on or off.")
        public boolean enableMod = true;

        @Comment("Toggles logging debug messages in chat.")
        public boolean debugLog = false;
    }

    public static class Townless {
        @Comment("Toggles townless players on or off.")
        public boolean enabled = true;
        @Comment("The maximum length the townless list can be. < 1 for no limit.")
        public int maxLength = 10; // < 1 = No limit
        @Comment("Toggles the use of preset positions, uses sliders if off.")
        public boolean presetPositions = true;

        @EnumHandler(option = BUTTON)
        @Comment("The position of the Townless info.")
        public State positionState = State.RIGHT;

        @Comment("Note: Only used if Use Preset Positions is off.")
        public int xPos = 1;
        @Comment("Note: Only used if Use Preset Positions is off.")
        public int yPos = 16;

        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of the 'Townless Players' text.")
        public Colors headingTextColour = Colors.DARK_PURPLE;
        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of the townless player names.")
        public Colors playerTextColour = Colors.DARK_PURPLE;
    }

    public static class Nearby {
        @Comment("Toggle nearby overlay on or off.")
        public boolean enabled = true;
        @Comment("Toggle if players' ranks should show before their name.")
        public boolean showRank = false;
        @Comment("Toggle between a preset or custom position.")
        public boolean presetPositions = true;

        @EnumHandler(option = BUTTON)
        public State positionState = State.TOP_RIGHT;

        @EnumHandler(option = BUTTON)
        @Comment("Determines order of the nearby players list before rendering.")
        public NearbySort nearbySort = NearbySort.NEAREST;

        @Comment("The horizontal position on the HUD.")
        public int xPos = 100;
        @Comment("The vertical position on the HUD.")
        public int yPos = 16;

        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of the 'Nearby Players' text.")
        public Colors headingTextColour = Colors.GOLD;
        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of nearby players' names.")
        public Colors playerTextColour = Colors.GOLD;

        // Independent scaling - either axis can be the same or different.
        @Comment("The amount of blocks to check on the X axis.")
        @BoundedDiscrete(min = 32, max = 10240)
        public int xBlocks = 500;
        @BoundedDiscrete(min = 32, max = 10240)
        @Comment("The amount of blocks to check on the Z axis.")
        public int zBlocks = 500;
    }

    public static class Commands {
        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of the townless players text.")
        public Colors townlessTextColour = Colors.LIGHT_PURPLE;

        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of the town info text.")
        public Colors townInfoTextColour = Colors.GREEN;

        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of the nation info text.")
        public Colors nationInfoTextColour = Colors.AQUA;

        @EnumHandler(option = DROPDOWN)
        @Comment("The colour of the alliance info text.")
        public Colors allianceInfoTextColour = Colors.GOLD;
    }

    public static class Intervals {
        @Comment("Fairly harmless on performance, can be lowered without much overhead.")
        @BoundedDiscrete(min = 10, max = 200)
        public int townless = 30;

        @Comment("Small but frequent payload, if you don't rely on it much, turn it up.")
        @BoundedDiscrete(min = 2, max = 30)
        public int nearby = 5;
    }

    public static ModConfig instance() {
        return EarthMCEssentials.instance().config();
    }
}