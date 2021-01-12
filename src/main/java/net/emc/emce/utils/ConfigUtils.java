package net.emc.emce.utils;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.emc.emce.ModConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;
import net.emc.emce.EMCE;
import net.minecraft.text.TranslatableText;

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
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enable Mod"), EMCE.config.general.enableMod)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the mod on or off."))
                .setSaveConsumer(newValue -> EMCE.config.general.enableMod = newValue)
                .build());

        // Enable EMC Only
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("EMC Only"), EMCE.config.general.emcOnly)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("While enabled, overlays only render while you are on EarthMC."))
                .setSaveConsumer(newValue -> EMCE.config.general.emcOnly = newValue)
                .build());

        // Enable Townless
        townless.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), EMCE.config.townless.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles townless players on or off."))
                .setSaveConsumer(newValue -> EMCE.config.townless.enabled = newValue)
                .build());

        // Townless Preset positions
        townless.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Use Preset Positions"), EMCE.config.townless.presetPositions)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the use of preset positions, uses sliders if off."))
                .setSaveConsumer(newValue -> EMCE.config.townless.presetPositions = newValue)
                .build());

        // If advanced positioning isn't toggled, use preset position.
        if (EMCE.config.townless.presetPositions)
        {
            // Townless Preset Position
            townless.addEntry(entryBuilder.startEnumSelector(new TranslatableText("Preset Position"), ModUtils.State.class, EMCE.config.townless.positionState)
                    .setDefaultValue(ModUtils.State.TOP_LEFT)
                    .setTooltip(new TranslatableText("The position of the Townless info."))
                    .setSaveConsumer(newValue -> EMCE.config.townless.positionState = newValue)
                    .build());
        }
        else
        {
            // Townless Horizontal Position
            townless.addEntry(entryBuilder.startIntField(new TranslatableText("Horizontal Position (X)"), EMCE.config.townless.xPos)
                    .setDefaultValue(770)
                    .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                    .setSaveConsumer(newValue -> EMCE.config.townless.xPos = newValue)
                    .build());

            // Townless Vertical Position
            townless.addEntry(entryBuilder.startIntField(new TranslatableText("Vertical Position (Y)"), EMCE.config.townless.yPos)
                    .setDefaultValue(375)
                    .setTooltip(new TranslatableText("The vertical position on the HUD."))
                    .setSaveConsumer(newValue -> EMCE.config.townless.yPos = newValue)
                    .build());

        }

        // Townless Text Color
        townless.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), EMCE.colors, EMCE.config.townless.headingTextColour)
                .setDefaultValue(EMCE.colors[8])
                .setTooltip(new TranslatableText("The colour of the 'Townless Players' text."))
                .setSaveConsumer(newValue -> EMCE.config.townless.headingTextColour = newValue)
                .build());

        // Townless Player Color
        townless.addEntry(entryBuilder.startSelector(new TranslatableText("Player Colour"), EMCE.colors, EMCE.config.townless.playerTextColour)
                .setDefaultValue(EMCE.colors[8])
                .setTooltip(new TranslatableText("The colour of the townless player names."))
                .setSaveConsumer(newValue -> EMCE.config.townless.playerTextColour = newValue)
                .build());

        // Townless Max length
        townless.addEntry(entryBuilder.startIntField(new TranslatableText("Maximum Length"), EMCE.config.townless.maxLength)
                .setDefaultValue(0)
                .setTooltip(new TranslatableText("The maximum length the townless list can be. Enter anything under 1 for no limit."))
                .setSaveConsumer(newValue -> EMCE.config.townless.maxLength = newValue)
                .build());

        // Enable nearby
        nearby.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), EMCE.config.nearby.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles nearby players on or off."))
                .setSaveConsumer(newValue -> EMCE.config.nearby.enabled = newValue)
                .build());

        // Nearby Preset positions
        nearby.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Use Preset Positions"), EMCE.config.nearby.presetPositions)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the use of preset positions, uses sliders if off."))
                .setSaveConsumer(newValue -> EMCE.config.nearby.presetPositions = newValue)
                .build());

        if (EMCE.config.nearby.presetPositions)
        {
            // Nearby Preset Position
            nearby.addEntry(entryBuilder.startEnumSelector(new TranslatableText("Preset Position"), ModUtils.State.class, EMCE.config.nearby.positionState)
                    .setDefaultValue(ModUtils.State.TOP_RIGHT)
                    .setTooltip(new TranslatableText("The position of the Nearby info."))
                    .setSaveConsumer(newValue -> EMCE.config.nearby.positionState = newValue)
                    .build());
        }
        else
        {
            // Nearby Player Horizontal Position
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Horizontal Position (X)"), EMCE.config.nearby.xPos, 1, 1000)
                    .setDefaultValue(770)
                    .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                    .setSaveConsumer(newValue -> EMCE.config.nearby.xPos = newValue)
                    .build());

            // Nearby Player Vertical Position
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Vertical Position (Y)"), EMCE.config.nearby.yPos, 16, 1000)
                    .setDefaultValue(275)
                    .setTooltip(new TranslatableText("The vertical position on the HUD."))
                    .setSaveConsumer(newValue -> EMCE.config.nearby.yPos = newValue)
                    .build());
        }

        // Nearby Player Text Color
        nearby.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), EMCE.colors, EMCE.config.nearby.headingTextColour)
                .setDefaultValue(EMCE.colors[11])
                .setTooltip(new TranslatableText("The colour of the 'Nearby Players' text."))
                .setSaveConsumer(newValue -> EMCE.config.nearby.headingTextColour = newValue)
                .build());

        // Nearby Player Player Color
        nearby.addEntry(entryBuilder.startSelector(new TranslatableText("Player Colour"), EMCE.colors, EMCE.config.nearby.playerTextColour)
                .setDefaultValue(EMCE.colors[11])
                .setTooltip(new TranslatableText("The colour of the nearby player names."))
                .setSaveConsumer(newValue -> EMCE.config.nearby.playerTextColour = newValue)
                .build());

        // Nearby Scale Method
        nearby.addEntry(entryBuilder.startEnumSelector(new TranslatableText("Scale Method"), ModUtils.ScaleMethod.class, EMCE.config.nearby.scaleMethod)
                .setDefaultValue(ModUtils.ScaleMethod.Proportionate)
                .setTooltip(new TranslatableText("The method of scaling used for the nearby radius"))
                .setSaveConsumer(newValue -> EMCE.config.nearby.scaleMethod = newValue)
                .build());

        if (EMCE.config.nearby.scaleMethod == ModUtils.ScaleMethod.Proportionate)
        {
            // Nearby Radius (X and Y)
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Radius"), EMCE.config.nearby.radius, 50, 10000)
                    .setDefaultValue(500)
                    .setTooltip(new TranslatableText("The radius (in blocks) to check inside."))
                    .setSaveConsumer(newValue -> EMCE.config.nearby.radius = newValue)
                    .build());
        }
        else
        {
            // Nearby X Blocks
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("X Blocks"), EMCE.config.nearby.xBlocks, 50, 10000)
                    .setDefaultValue(500)
                    .setTooltip(new TranslatableText("The amount of blocks to check on the X axis."))
                    .setSaveConsumer(newValue -> EMCE.config.nearby.xBlocks = newValue)
                    .build());

            // Nearby Z Blocks
            nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Z Blocks"), EMCE.config.nearby.zBlocks, 50, 10000)
                    .setDefaultValue(500)
                    .setTooltip(new TranslatableText("The amount of blocks to check on the Z axis."))
                    .setSaveConsumer(newValue -> EMCE.config.nearby.zBlocks = newValue)
                    .build());
        }

        // Town Information Colour
        commands.addEntry(entryBuilder.startSelector(new TranslatableText("Town Info Colour"), EMCE.colors, EMCE.config.commands.townInfoTextColour)
                .setDefaultValue("BLUE")
                .setTooltip(new TranslatableText("The colour of the town info text."))
                .setSaveConsumer(newValue -> EMCE.config.commands.townInfoTextColour = newValue)
                .build());

        // Nation Information Colour
        commands.addEntry(entryBuilder.startSelector(new TranslatableText("Nation Info Colour"), EMCE.colors, EMCE.config.commands.nationInfoTextColour)
                .setDefaultValue("AQUA")
                .setTooltip(new TranslatableText("The colour of the nation info text."))
                .setSaveConsumer(newValue -> EMCE.config.commands.nationInfoTextColour = newValue)
                .build());

        builder.setSavingRunnable(() -> ConfigUtils.serializeConfig(EMCE.config));

        return builder;
    }
}