package net.earthmc.emc;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

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

        // Townless Horizontal Position
        townless.addEntry(entryBuilder.startBooleanToggle("Show Coordinates", EMCMod.config.townless.showCoords)
                .setDefaultValue(true)
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
        townless.addEntry(entryBuilder.startSelector("Townless Text Color", EMCMod.colors, EMCMod.config.townless.townlessTextColor)
                .setDefaultValue(EMCMod.colors[8])
                .setTooltip("The color of the 'Townless Players' text.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.townlessTextColor = newValue)
                .build());

        // Townless Player Color
        townless.addEntry(entryBuilder.startSelector("Townless Player Color", EMCMod.colors, EMCMod.config.townless.townlessPlayerColor)
                .setDefaultValue(EMCMod.colors[8])
                .setTooltip("The color of the townless player names.")
                .setSaveConsumer(newValue -> EMCMod.config.townless.townlessPlayerColor = newValue)
                .build());

        // Nearby Player Horizontal Position
        nearby.addEntry(entryBuilder.startIntSlider("Horizontal Position (X)", EMCMod.config.nearby.nearbyListXPos, 100, 1000)
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
        nearby.addEntry(entryBuilder.startSelector("Nearby Text Color", EMCMod.colors, EMCMod.config.nearby.nearbyTextColor)
                .setDefaultValue(EMCMod.colors[11])
                .setTooltip("The color of the 'Nearby Players' text.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.nearbyTextColor = newValue)
                .build());

        // Nearby Player Player Color
        nearby.addEntry(entryBuilder.startSelector("Nearby Player Color", EMCMod.colors, EMCMod.config.nearby.nearbyPlayerColor)
                .setDefaultValue(EMCMod.colors[11])
                .setTooltip("The color of the nearby player names.")
                .setSaveConsumer(newValue -> EMCMod.config.nearby.nearbyPlayerColor = newValue)
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

        return builder;
    }
}