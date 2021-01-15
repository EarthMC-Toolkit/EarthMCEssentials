package net.emc.emce.utils;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.emc.emce.ModConfig;
import net.minecraft.text.TranslatableText;

import static net.emc.emce.EMCE.colors;
import static net.emc.emce.EMCE.config;

public class ConfigUtils
{
    private ConfigUtils() { }

    public static void serializeConfig(ModConfig config)
    {
        try
        {
            ((ConfigManager<ModConfig>) AutoConfig.getConfigHolder(ModConfig.class)).getSerializer().serialize(config);
        } catch (ConfigSerializer.SerializationException serializeException) {
            serializeException.printStackTrace();
        }
    }

    public static ConfigBuilder getConfigBuilder()
    {
        ConfigBuilder builder = ConfigBuilder.create().setTitle(new TranslatableText("EarthMC Essentials Config")).setTransparentBackground(true);

        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("General"));
        ConfigCategory townless = builder.getOrCreateCategory(new TranslatableText("Townless"));
        ConfigCategory nearby = builder.getOrCreateCategory(new TranslatableText("Nearby"));
        ConfigCategory commands = builder.getOrCreateCategory(new TranslatableText("Commands"));        

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // #region Add Entries
        // Enable Mod
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enable Mod"), config.general.enableMod)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the mod on or off."))
                .setSaveConsumer(newValue -> config.general.enableMod = newValue)
                .build());

        // Enable EMC Only
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("EMC Only"), config.general.emcOnly)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("While enabled, overlays only render while you are on EarthMC."))
                .setSaveConsumer(newValue -> config.general.emcOnly = newValue)
                .build());

        // Enable Townless
        townless.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), config.townless.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles townless players on or off."))
                .setSaveConsumer(newValue -> config.townless.enabled = newValue)
                .build());

        // Townless Preset positions
        townless.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Use Preset Positions"), config.townless.presetPositions)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the use of preset positions, uses sliders if off."))
                .setSaveConsumer(newValue -> config.townless.presetPositions = newValue)
                .build());

        // If advanced positioning isn't toggled, use preset position.
        if (config.townless.presetPositions)
        {
            // Townless Preset Position
            townless.addEntry(entryBuilder.startEnumSelector(new TranslatableText("Preset Position"), ModUtils.State.class, config.townless.positionState)
                    .setDefaultValue(ModUtils.State.TOP_LEFT)
                    .setTooltip(new TranslatableText("The position of the Townless info."))
                    .setSaveConsumer(newValue -> config.townless.positionState = newValue)
                    .build());
        }
        else
        {
            // Townless Horizontal Position
            townless.addEntry(entryBuilder.startIntField(new TranslatableText("Horizontal Position (X)"), config.townless.xPos)
                    .setDefaultValue(770)
                    .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                    .setSaveConsumer(newValue -> config.townless.xPos = newValue)
                    .build());

            // Townless Vertical Position
            townless.addEntry(entryBuilder.startIntField(new TranslatableText("Vertical Position (Y)"), config.townless.yPos)
                    .setDefaultValue(375)
                    .setTooltip(new TranslatableText("The vertical position on the HUD."))
                    .setSaveConsumer(newValue -> config.townless.yPos = newValue)
                    .build());

        }

        // Townless Text Color
        townless.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), colors, config.townless.headingTextColour)
                .setDefaultValue(colors[8])
                .setTooltip(new TranslatableText("The colour of the 'Townless Players' text."))
                .setSaveConsumer(newValue -> config.townless.headingTextColour = newValue)
                .build());

        // Townless Player Color
        townless.addEntry(entryBuilder.startSelector(new TranslatableText("Player Colour"), colors, config.townless.playerTextColour)
                .setDefaultValue(colors[8])
                .setTooltip(new TranslatableText("The colour of the townless player names."))
                .setSaveConsumer(newValue -> config.townless.playerTextColour = newValue)
                .build());

        // Townless Max length
        townless.addEntry(entryBuilder.startIntField(new TranslatableText("Maximum Length"), config.townless.maxLength)
                .setDefaultValue(0)
                .setTooltip(new TranslatableText("The maximum length the townless list can be. Enter anything under 1 for no limit."))
                .setSaveConsumer(newValue -> config.townless.maxLength = newValue)
                .build());

        // Enable nearby
        nearby.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), config.nearby.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles nearby players on or off."))
                .setSaveConsumer(newValue -> config.nearby.enabled = newValue)
                .build());

        // Show nearby player rank
        nearby.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Show Rank"), config.nearby.showRank)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the showing of players ranks before their names."))
                .setSaveConsumer(newValue -> config.nearby.showRank = newValue)
                .build());

        // Nearby preset positions
        nearby.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Use Preset Positions"), config.nearby.presetPositions)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the use of preset positions, uses sliders if off."))
                .setSaveConsumer(newValue -> config.nearby.presetPositions = newValue)
                .build());

        if (config.nearby.presetPositions)
        {
            // Nearby Preset Position
            nearby.addEntry(entryBuilder.startEnumSelector(new TranslatableText("Preset Position"), ModUtils.State.class, config.nearby.positionState)
                    .setDefaultValue(ModUtils.State.TOP_RIGHT)
                    .setTooltip(new TranslatableText("The position of the Nearby info."))
                    .setSaveConsumer(newValue -> config.nearby.positionState = newValue)
                    .build());
        }
        else
        {
            // Nearby Player Horizontal Position
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Horizontal Position (X)"), config.nearby.xPos, 1, 1000)
                    .setDefaultValue(770)
                    .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                    .setSaveConsumer(newValue -> config.nearby.xPos = newValue)
                    .build());

            // Nearby Player Vertical Position
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Vertical Position (Y)"), config.nearby.yPos, 16, 1000)
                    .setDefaultValue(275)
                    .setTooltip(new TranslatableText("The vertical position on the HUD."))
                    .setSaveConsumer(newValue -> config.nearby.yPos = newValue)
                    .build());
        }

        // Nearby Player Text Color
        nearby.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), colors, config.nearby.headingTextColour)
                .setDefaultValue(colors[11])
                .setTooltip(new TranslatableText("The colour of the 'Nearby Players' text."))
                .setSaveConsumer(newValue -> config.nearby.headingTextColour = newValue)
                .build());

        // Nearby Player Player Color
        nearby.addEntry(entryBuilder.startSelector(new TranslatableText("Player Colour"), colors, config.nearby.playerTextColour)
                .setDefaultValue(colors[11])
                .setTooltip(new TranslatableText("The colour of the nearby player names."))
                .setSaveConsumer(newValue -> config.nearby.playerTextColour = newValue)
                .build());

        // Nearby Scale Method
        nearby.addEntry(entryBuilder.startEnumSelector(new TranslatableText("Scale Method"), ModUtils.ScaleMethod.class, config.nearby.scaleMethod)
                .setDefaultValue(ModUtils.ScaleMethod.Proportionate)
                .setTooltip(new TranslatableText("The method of scaling used for the nearby radius"))
                .setSaveConsumer(newValue -> config.nearby.scaleMethod = newValue)
                .build());

        if (config.nearby.scaleMethod == ModUtils.ScaleMethod.Proportionate)
        {
            // Nearby Radius (X and Y)
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Radius"), config.nearby.radius, 50, 10000)
                    .setDefaultValue(500)
                    .setTooltip(new TranslatableText("The radius (in blocks) to check inside."))
                    .setSaveConsumer(newValue -> config.nearby.radius = newValue)
                    .build());
        }
        else
        {
            // Nearby X Blocks
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("X Blocks"), config.nearby.xBlocks, 50, 10000)
                    .setDefaultValue(500)
                    .setTooltip(new TranslatableText("The amount of blocks to check on the X axis."))
                    .setSaveConsumer(newValue -> config.nearby.xBlocks = newValue)
                    .build());

            // Nearby Z Blocks
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Z Blocks"), config.nearby.zBlocks, 50, 10000)
                    .setDefaultValue(500)
                    .setTooltip(new TranslatableText("The amount of blocks to check on the Z axis."))
                    .setSaveConsumer(newValue -> config.nearby.zBlocks = newValue)
                    .build());
        }

        // Townless Information Colour
        commands.addEntry(entryBuilder.startSelector(new TranslatableText("Town Info Colour"), colors, config.commands.townlessTextColour)
                .setDefaultValue("LIGHT_PURPLE")
                .setTooltip(new TranslatableText("The colour of the townless players text."))
                .setSaveConsumer(newValue -> config.commands.townlessTextColour = newValue)
                .build());

        // Town Information Colour
        commands.addEntry(entryBuilder.startSelector(new TranslatableText("Town Info Colour"), colors, config.commands.townInfoTextColour)
                .setDefaultValue("GREEN")
                .setTooltip(new TranslatableText("The colour of the town info text."))
                .setSaveConsumer(newValue -> config.commands.townInfoTextColour = newValue)
                .build());

        // Nation Information Colour
        commands.addEntry(entryBuilder.startSelector(new TranslatableText("Nation Info Colour"), colors, config.commands.nationInfoTextColour)
                .setDefaultValue("AQUA")
                .setTooltip(new TranslatableText("The colour of the nation info text."))
                .setSaveConsumer(newValue -> config.commands.nationInfoTextColour = newValue)
                .build());

        builder.setSavingRunnable(() -> ConfigUtils.serializeConfig(config));

        return builder;
    }
}