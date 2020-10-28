package net.earthmc.emc;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.earthmc.emc.utils.ConfigUtils;

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
        ConfigBuilder builder = ConfigBuilder.create().setTitle("EarthMC Essentials Config").setTransparentBackground(true);

        ConfigCategory general = builder.getOrCreateCategory("General");
        ConfigCategory townless = builder.getOrCreateCategory("Townless");
        ConfigCategory nearby = builder.getOrCreateCategory("Nearby");
        ConfigCategory townInfo = builder.getOrCreateCategory("Town Info");
        ConfigCategory nationInfo = builder.getOrCreateCategory("Nation Info");

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // #region Add Entries
        // Enable Mod
        general.addEntry(entryBuilder.startBooleanToggle("Enable Mod", EMCMod.config.general.enableMod)
                .setDefaultValue(true)
                .setTooltip("Toggles the mod on or off.")
                .setSaveConsumer(newValue -> EMCMod.config.general.enableMod = newValue)
                .build());

        // Enable EMC Only
        general.addEntry(entryBuilder.startBooleanToggle("EMC Only", EMCMod.config.general.emcOnly)
                .setDefaultValue(true)
                .setTooltip("Toggles EMC Only on or off. NOT YET IMPLEMENTED.")
                .setSaveConsumer(newValue -> EMCMod.config.general.emcOnly = newValue)
                .build());

        // Enable Townless
        townless.addEntry(entryBuilder.startBooleanToggle("Enabled", EMCMod.config.townless.enabled)
                .setDefaultValue(true)
                .setTooltip("Toggles townless players on or off.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.enabled = newValue)
                .build());

        // Townless Horizontal Position
        townless.addEntry(entryBuilder.startBooleanToggle("Show Coordinates", EMCMod.config.townless.showCoords)
                .setDefaultValue(false)
                .setTooltip("Toggles coordinates for townless players on or off.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.showCoords = newValue)
                .build());

        // Townless Horizontal Position
        townless.addEntry(entryBuilder.startIntSlider("Horizontal Position (X)", EMCMod.config.townless.townlessListXPos, 1, 1000)
                .setDefaultValue(770)
                .setTooltip("The horizontal position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.townlessListXPos = newValue)
                .build());

        // Townless Vertical Position
        townless.addEntry(entryBuilder.startIntSlider("Vertical Position (Y)", EMCMod.config.townless.townlessListYPos, 16, 1000)
                .setDefaultValue(375)
                .setTooltip("The vertical position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.townlessListYPos = newValue)
                .build());

        // Townless Text Color
        townless.addEntry(entryBuilder.startSelector("Heading Colour", EMCMod.colors, EMCMod.config.townless.headingTextColour)
                .setDefaultValue(EMCMod.colors[8])
                .setTooltip("The colour of the 'Townless Players' text.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.headingTextColour = newValue)
                .build());

        // Townless Player Color
        townless.addEntry(entryBuilder.startSelector("Player Colour", EMCMod.colors, EMCMod.config.townless.playerTextColour)
                .setDefaultValue(EMCMod.colors[8])
                .setTooltip("The colour of the townless player names.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.playerTextColour = newValue)
                .build());

        // Enable nearby
        nearby.addEntry(entryBuilder.startBooleanToggle("Enabled", EMCMod.config.nearby.enabled)
                .setDefaultValue(true)
                .setTooltip("Toggles nearby players on or off.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.enabled = newValue)
                .build());

        // Nearby Player Horizontal Position
        nearby.addEntry(entryBuilder.startIntSlider("Horizontal Position (X)", EMCMod.config.nearby.nearbyListXPos, 1, 1000)
                .setDefaultValue(770)
                .setTooltip("The horizontal position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.nearbyListXPos = newValue)
                .build());

        // Nearby Player Vertical Position
        nearby.addEntry(entryBuilder.startIntSlider("Vertical Position (Y)", EMCMod.config.nearby.nearbyListYPos, 16, 1000)
                .setDefaultValue(275)
                .setTooltip("The vertical position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.nearbyListYPos = newValue)
                .build());

        // Nearby Player Text Color
        nearby.addEntry(entryBuilder.startSelector("Heading Colour", EMCMod.colors, EMCMod.config.nearby.headingTextColour)
                .setDefaultValue(EMCMod.colors[11])
                .setTooltip("The colour of the 'Nearby Players' text.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.headingTextColour = newValue)
                .build());

        // Nearby Player Player Color
        nearby.addEntry(entryBuilder.startSelector("Player Colour", EMCMod.colors, EMCMod.config.nearby.playerTextColour)
                .setDefaultValue(EMCMod.colors[11])
                .setTooltip("The colour of the nearby player names.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.playerTextColour = newValue)
                .build());

        // Nearby Player Name
        nearby.addEntry(entryBuilder.startStrField("Player Name", EMCMod.config.nearby.playerName)
                .setDefaultValue(EMCMod.clientName)
                .setTooltip("The name of the player to check nearby.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.playerName = newValue)
                .build());

        // Nearby X Radius
        nearby.addEntry(entryBuilder.startIntSlider("X Radius", EMCMod.config.nearby.xRadius, 50, 10000)
                .setDefaultValue(500)
                .setTooltip("The x radius (in blocks) to check inside.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.xRadius = newValue)
                .build());

        // Nearby Z Radius
        nearby.addEntry(entryBuilder.startIntSlider("Z Radius", EMCMod.config.nearby.zRadius, 50, 10000)
                .setDefaultValue(500)
                .setTooltip("The z radius (in blocks) to check inside.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.zRadius = newValue)
                .build());

        // Enable Town Information
        townInfo.addEntry(entryBuilder.startBooleanToggle("Enabled", EMCMod.config.townInfo.enabled)
                .setDefaultValue(true)
                .setTooltip("Toggles town information on or off.")
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.enabled = newValue)
                .build());

        // Town Information Horizontal Position
        townInfo.addEntry(entryBuilder.startIntSlider("Horizontal Position (X)", EMCMod.config.townInfo.townInfoXPos, 1, 1000)
                .setDefaultValue(15)
                .setTooltip("The horizontal position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.townInfoXPos = newValue)
                .build());

        // Town Information Vertical Position
        townInfo.addEntry(entryBuilder.startIntSlider("Vertical Position (Y)", EMCMod.config.townInfo.townInfoYPos, 16, 1000)
                .setDefaultValue(275)
                .setTooltip("The vertical position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.townInfoYPos = newValue)
                .build());

        // Town Information Heading Colour
        townInfo.addEntry(entryBuilder.startSelector("Heading Colour", EMCMod.colors, EMCMod.config.townInfo.headingTextColour)
                .setDefaultValue("GREEN")
                .setTooltip("The colour of the header.")
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.headingTextColour = newValue)
                .build());

        // Town Information Info Colour
        townInfo.addEntry(entryBuilder.startSelector("Info Colour", EMCMod.colors, EMCMod.config.townInfo.infoTextColour)
                .setDefaultValue("GREEN")
                .setTooltip("The colour of the information.")
                .setSaveConsumer(newValue -> EMCMod.config.townInfo.infoTextColour = newValue)
                .build());

        // Enable Nation Information
        nationInfo.addEntry(entryBuilder.startBooleanToggle("Enabled", EMCMod.config.nationInfo.enabled)
                .setDefaultValue(true)
                .setTooltip("Toggles town information on or off.")
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.enabled = newValue)
                .build());

        // Nation Information Horizontal Position
        nationInfo.addEntry(entryBuilder.startIntSlider("Horizontal Position (X)", EMCMod.config.nationInfo.nationInfoXPos, 1, 1000)
                .setDefaultValue(15)
                .setTooltip("The horizontal position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.nationInfoXPos = newValue)
                .build());

        // Nation Information Vertical Position
        nationInfo.addEntry(entryBuilder.startIntSlider("Vertical Position (Y)", EMCMod.config.nationInfo.nationInfoYPos, 16, 1000)
                .setDefaultValue(375)
                .setTooltip("The vertical position on the HUD.")
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.nationInfoYPos = newValue)
                .build());

        // Nation Information Heading Colour
        nationInfo.addEntry(entryBuilder.startSelector("Heading Colour", EMCMod.colors, EMCMod.config.nationInfo.headingTextColour)
                .setDefaultValue("AQUA")
                .setTooltip("The colour of the header.")
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.headingTextColour = newValue)
                .build());

        // Nation Information Info Colour
        nationInfo.addEntry(entryBuilder.startSelector("Info Colour", EMCMod.colors, EMCMod.config.nationInfo.infoTextColour)
                .setDefaultValue("AQUA")
                .setTooltip("The colour of the information.")
                .setSaveConsumer(newValue -> EMCMod.config.nationInfo.infoTextColour = newValue)
                .build());

        builder.setSavingRunnable(() -> ConfigUtils.serializeConfig(EMCMod.config));

        return builder;
    }
}