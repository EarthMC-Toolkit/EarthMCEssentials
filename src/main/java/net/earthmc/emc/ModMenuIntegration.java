package net.earthmc.emc;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.earthmc.emc.utils.ConfigUtils;
import net.minecraft.text.TranslatableText;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public String getModId()
    {
        return "emc-essentials";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> getConfigBuilder().build();
    }

    public static ConfigBuilder getConfigBuilder()
    {
        ConfigBuilder builder = ConfigBuilder.create().setTitle(new TranslatableText("EarthMC Essentials Config")).setTransparentBackground(true);

        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("General"));
        ConfigCategory townless = builder.getOrCreateCategory(new TranslatableText("Townless"));
        ConfigCategory nearby = builder.getOrCreateCategory(new TranslatableText("Nearby"));
        ConfigCategory townInfo = builder.getOrCreateCategory(new TranslatableText("Town Info"));
        ConfigCategory nationInfo = builder.getOrCreateCategory(new TranslatableText("Nation Info"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // #region Add Entries
        // Enable Mod
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enable Mod"), EMCMod.config.general.enableMod)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles the mod on or off."))
                .setSaveConsumer(newValue -> EMCMod.config.general.enableMod = newValue)
                .build());

        // Enable EMC Only
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("EMC Only"), EMCMod.config.general.emcOnly)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles EMC Only on or off. NOT YET IMPLEMENTED."))
                .setSaveConsumer(newValue -> EMCMod.config.general.emcOnly = newValue)
                .build());

        // Enable Townless
        townless.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), EMCMod.config.townless.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles townless players on or off."))
                .setSaveConsumer(newValue -> EMCMod.config.townless.enabled = newValue)
                .build());

        // Townless Horizontal Position
        townless.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Show Coordinates"), EMCMod.config.townless.showCoords)
                .setDefaultValue(false)
                .setTooltip(new TranslatableText("Toggles coordinates for townless players on or off."))
                .setSaveConsumer(newValue -> EMCMod.config.townless.showCoords = newValue)
                .build());

        // Townless Horizontal Position
        townless.addEntry(entryBuilder.startIntSlider(new TranslatableText("Horizontal Position (X)"), EMCMod.config.townless.xPos, 1, 1000)
                .setDefaultValue(770)
                .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.townless.xPos = newValue)
                .build());

        // Townless Vertical Position
        townless.addEntry(entryBuilder.startIntSlider(new TranslatableText("Vertical Position (Y)"), EMCMod.config.townless.yPos, 16, 1000)
                .setDefaultValue(375)
                .setTooltip(new TranslatableText("The vertical position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.townless.yPos = newValue)
                .build());

        // Townless Text Color
        townless.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), EMCMod.colors, EMCMod.config.townless.headingTextColour)
                .setDefaultValue(EMCMod.colors[8])
                .setTooltip(new TranslatableText("The colour of the 'Townless Players' text."))
                .setSaveConsumer(newValue -> EMCMod.config.townless.headingTextColour = newValue)
                .build());

        // Townless Player Color
        townless.addEntry(entryBuilder.startSelector(new TranslatableText("Player Colour"), EMCMod.colors, EMCMod.config.townless.playerTextColour)
                .setDefaultValue(EMCMod.colors[8])
                .setTooltip(new TranslatableText("The colour of the townless player names."))
                .setSaveConsumer(newValue -> EMCMod.config.townless.playerTextColour = newValue)
                .build());

        // Enable nearby
        nearby.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), EMCMod.config.nearby.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles nearby players on or off."))
                .setSaveConsumer(newValue -> EMCMod.config.nearby.enabled = newValue)
                .build());

        // Nearby Player Horizontal Position
        nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Horizontal Position (X)"), EMCMod.config.nearby.xPos, 1, 1000)
                .setDefaultValue(770)
                .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.nearby.xPos = newValue)
                .build());

        // Nearby Player Vertical Position
        nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Vertical Position (Y)"), EMCMod.config.nearby.yPos, 16, 1000)
                .setDefaultValue(275)
                .setTooltip(new TranslatableText("The vertical position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.nearby.yPos = newValue)
                .build());

        // Nearby Player Text Color
        nearby.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), EMCMod.colors, EMCMod.config.nearby.headingTextColour)
                .setDefaultValue(EMCMod.colors[11])
                .setTooltip(new TranslatableText("The colour of the 'Nearby Players' text."))
                .setSaveConsumer(newValue -> EMCMod.config.nearby.headingTextColour = newValue)
                .build());

        // Nearby Player Player Color
        nearby.addEntry(entryBuilder.startSelector(new TranslatableText("Player Colour"), EMCMod.colors, EMCMod.config.nearby.playerTextColour)
                .setDefaultValue(EMCMod.colors[11])
                .setTooltip(new TranslatableText("The colour of the nearby player names."))
                .setSaveConsumer(newValue -> EMCMod.config.nearby.playerTextColour = newValue)
                .build());
                
        // Nearby X Radius
        nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("X Radius"), EMCMod.config.nearby.xRadius, 50, 10000)
                .setDefaultValue(500)
                .setTooltip(new TranslatableText("The x radius (in blocks) to check inside."))
                .setSaveConsumer(newValue -> EMCMod.config.nearby.xRadius = newValue)
                .build());

        // Nearby Z Radius
        nearby.addEntry(entryBuilder.startIntSlider(new TranslatableText("Z Radius"), EMCMod.config.nearby.zRadius, 50, 10000)
                .setDefaultValue(500)
                .setTooltip(new TranslatableText("The z radius (in blocks) to check inside."))
                .setSaveConsumer(newValue -> EMCMod.config.nearby.zRadius = newValue)
                .build());

        // Enable Town Information
        townInfo.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), EMCMod.config.townInfo.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles town information on or off."))
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.enabled = newValue)
                .build());

        // Town Information Horizontal Position
        townInfo.addEntry(entryBuilder.startIntSlider(new TranslatableText("Horizontal Position (X)"), EMCMod.config.townInfo.xPos, 1, 1000)
                .setDefaultValue(15)
                .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.xPos = newValue)
                .build());

        // Town Information Vertical Position
        townInfo.addEntry(entryBuilder.startIntSlider(new TranslatableText("Vertical Position (Y)"), EMCMod.config.townInfo.yPos, 16, 1000)
                .setDefaultValue(275)
                .setTooltip(new TranslatableText("The vertical position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.yPos = newValue)
                .build());

        // Town Information Heading Colour
        townInfo.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), EMCMod.colors, EMCMod.config.townInfo.headingTextColour)
                .setDefaultValue("GREEN")
                .setTooltip(new TranslatableText("The colour of the header."))
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.headingTextColour = newValue)
                .build());

        // Town Information Info Colour
        townInfo.addEntry(entryBuilder.startSelector(new TranslatableText("Info Colour"), EMCMod.colors, EMCMod.config.townInfo.infoTextColour)
                .setDefaultValue("GREEN")
                .setTooltip(new TranslatableText("The colour of the information."))
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.infoTextColour = newValue)
                .build());

        // Enable Nation Information
        nationInfo.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Enabled"), EMCMod.config.nationInfo.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("Toggles town information on or off."))
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.enabled = newValue)
                .build());

        // Nation Information Horizontal Position
        nationInfo.addEntry(entryBuilder.startIntSlider(new TranslatableText("Horizontal Position (X)"), EMCMod.config.nationInfo.xPos, 1, 1000)
                .setDefaultValue(15)
                .setTooltip(new TranslatableText("The horizontal position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.xPos = newValue)
                .build());

        // Nation Information Vertical Position
        nationInfo.addEntry(entryBuilder.startIntSlider(new TranslatableText("Vertical Position (Y)"), EMCMod.config.nationInfo.yPos, 16, 1000)
                .setDefaultValue(375)
                .setTooltip(new TranslatableText("The vertical position on the HUD."))
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.yPos = newValue)
                .build());

        // Nation Information Heading Colour
        nationInfo.addEntry(entryBuilder.startSelector(new TranslatableText("Heading Colour"), EMCMod.colors, EMCMod.config.nationInfo.headingTextColour)
                .setDefaultValue("AQUA")
                .setTooltip(new TranslatableText("The colour of the header."))
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.headingTextColour = newValue)
                .build());

        // Nation Information Info Colour
        nationInfo.addEntry(entryBuilder.startSelector(new TranslatableText("Info Colour"), EMCMod.colors, EMCMod.config.nationInfo.infoTextColour)
                .setDefaultValue("AQUA")
                .setTooltip(new TranslatableText("The colour of the information."))
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.infoTextColour = newValue)
                .build());

        builder.setSavingRunnable(() -> ConfigUtils.serializeConfig(EMCMod.config));

        return builder;
    }
}