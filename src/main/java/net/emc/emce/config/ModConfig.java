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
import net.emc.emce.utils.ModUtils.State;

import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON;

@Config(name = "emc-essentials")
@Background(value = "cloth-config2:transparent")
public class ModConfig implements ConfigData {
    @Category("General")
    @TransitiveObject()
    public final General general = new General();

    @Category("Townless")
    @TransitiveObject()
    public final Townless townless = new Townless();

    @Category("Nearby")
    @TransitiveObject()
    public final Nearby nearby = new Nearby();

    @Category("Commands")
    @TransitiveObject()
    public final Commands commands = new Commands();

    @Category("Intervals")
    @TransitiveObject()
    public Intervals intervals = new Intervals();

    public static class General {
        @Comment("Toggles the mod on or off.")
        public final boolean enableMod = true;
    }

    public static class Townless {
        @Comment("Toggles townless players on or off.")
        public final boolean enabled = true;
        @Comment("The maximum length the townless list can be. < 1 for no limit.")
        public final int maxLength = 10; // < 1 = No limit
        @Comment("Toggles the use of preset positions, uses sliders if off.")
        public final boolean presetPositions = true;

        @EnumHandler(option = BUTTON)
        @Comment("The position of the Townless info.")
        public final State positionState = State.RIGHT;

        @Comment("Note: Only used if Use Preset Positions is off.")
        public final int xPos = 1;
        @Comment("Note: Only used if Use Preset Positions is off.")
        public final int yPos = 16;

        @EnumHandler(option = BUTTON)
        @Comment("The colour of the 'Townless Players' text.")
        public final Colors headingTextColour = Colors.DARK_PURPLE;
        @EnumHandler(option = BUTTON)
        @Comment("The colour of the townless player names.")
        public final Colors playerTextColour = Colors.DARK_PURPLE;
    }

    public static class Nearby {
        @Comment("Toggle nearby overlay on or off.")
        public final boolean enabled = true;
        @Comment("Toggle if players' ranks should show before their name.")
        public final boolean showRank = false;
        @Comment("Toggle between a preset or custom position.")
        public final boolean presetPositions = true;

        @EnumHandler(option = BUTTON)
        public final State positionState = State.TOP_RIGHT;

        @Comment("The horizontal position on the HUD.")
        public final int xPos = 100;
        @Comment("The vertical position on the HUD.")
        public final int yPos = 16;

        @EnumHandler(option = BUTTON)
        @Comment("The colour of the 'Nearby Players' text.")
        public final Colors headingTextColour = Colors.GOLD;
        @EnumHandler(option = BUTTON)
        @Comment("The colour of nearby players' names.")
        public final Colors playerTextColour = Colors.GOLD;

        // Independent scaling - either axis can be the same or different.
        @Comment("The amount of blocks to check on the X axis.")
        @BoundedDiscrete(min = 32, max = 10240)
        public final int xBlocks = 500;
        @BoundedDiscrete(min = 32, max = 10240)
        @Comment("The amount of blocks to check on the Z axis.")
        public final int zBlocks = 500;
    }

    public static class Commands {
        @EnumHandler(option = BUTTON)
        @Comment("The colour of the townless players text.")
        public final Colors townlessTextColour = Colors.LIGHT_PURPLE;

        @EnumHandler(option = BUTTON)
        @Comment("The colour of the town info text.")
        public final Colors townInfoTextColour = Colors.GREEN;

        @EnumHandler(option = BUTTON)
        @Comment("The colour of the nation info text.")
        public final Colors nationInfoTextColour = Colors.AQUA;

        @EnumHandler(option = BUTTON)
        @Comment("The colour of the alliance info text.")
        public final Colors allianceInfoTextColour = Colors.GOLD;
    }

    public static class Intervals {
        @Comment("Fairly harmless on performance, can be lowered without much overhead.")
        @BoundedDiscrete(min = 10, max = 200)
        public int townless = 60;

        @Comment("Small but frequent payload, if you don't rely on it much, turn it up.")
        @BoundedDiscrete(min = 3, max = 15)
        public int nearby = 5;
    }

    public static ModConfig instance() {
        return EarthMCEssentials.instance().getConfig();
    }
}