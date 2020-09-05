package net.earthmc.emc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.lwjgl.glfw.GLFW;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import java.util.Timer;
import java.util.TimerTask;

import net.earthmc.emc.utils.*;

public class EMCMod implements ModInitializer 
{
    JsonArray townless;
    JsonArray nearby;

    int townlessPlayerOffset;
    int nearbyPlayerOffset;

    ModConfig config;
    String[] colors;

    KeyBinding configKeybind;

    @Override
    public void onInitialize() // Called when Minecraft starts.
    {
        System.out.println("EarthMC Essentials Initialized!");

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        configKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        colors = new String[] { "BLUE", "DARK_BLUE", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "RED", "DARK_RED",
                "LIGHT_PURPLE", "DARK_PURPLE", "YELLOW", "GOLD", "GRAY", "DARK_GRAY", "BLACK", "WHITE" };

        townless = getTownless();
        nearby = new JsonArray();

        final Timer townlessTimer = new Timer();
        final Timer nearbyTimer = new Timer();

        // #region Create Timers
        townlessTimer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run() 
            {
                if (config.general.enableMod) townless = getTownless();
            }
        }, 0, 2 * 60 * 1000);

        nearbyTimer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run() 
            {
                if (config.general.enableMod) nearby = getNearby(config);
            }
        }, 0, 10 * 1000);
        // #endregion

        ClientTickEvents.END_CLIENT_TICK.register(client -> 
        {
            // Pressed F4 (Config Menu)
            if (configKeybind.wasPressed())
            {
                ConfigBuilder builder = ConfigBuilder.create().setTitle("EarthMC Essentials Config").setTransparentBackground(true);

                ConfigCategory general = builder.getOrCreateCategory("General");
                ConfigCategory townless = builder.getOrCreateCategory("Townless");
                ConfigCategory nearby = builder.getOrCreateCategory("Nearby");

                ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                // #region Add Entries
                // Enable Mod
                general.addEntry(entryBuilder.startBooleanToggle("Enable Mod", config.general.enableMod)
                .setDefaultValue(true)
                .setTooltip("Toggles the mod on or off.")
                .setSaveConsumer(newValue -> config.general.enableMod = newValue)
                .build());

                // Enable EMC Only
                general.addEntry(entryBuilder.startBooleanToggle("EMC Only", config.general.emcOnly)
                .setDefaultValue(true)
                .setTooltip("Toggles EMC Only on or off. NOT YET IMPLEMENTED.")
                .setSaveConsumer(newValue -> config.general.emcOnly = newValue)
                .build());

                // Townless Horizontal Position
                townless.addEntry(entryBuilder.startIntSlider("Horizontal Position (X)", config.townless.townlessListXPos, 10, 770)
                .setDefaultValue(770)
                .setTooltip("The horizontal position on the HUD.")
                .setSaveConsumer(newValue -> config.townless.townlessListXPos = newValue)
                .build());

                // Townless Vertical Position
                townless.addEntry(entryBuilder.startIntSlider("Vertical Position (Y)", config.townless.townlessListYPos, 20, 500)
                .setDefaultValue(375)
                .setTooltip("The vertical position on the HUD.")
                .setSaveConsumer(newValue -> config.townless.townlessListYPos = newValue)
                .build());

                // Townless Text Color
                townless.addEntry(entryBuilder.startSelector("Townless Text Color", colors, config.townless.townlessTextColor)
                .setDefaultValue("LIGHT_PURPLE")
                .setTooltip("The color of the 'Townless Players' text.")
                .setSaveConsumer(newValue -> config.townless.townlessTextColor = newValue)
                .build());

                // Townless Player Color
                townless.addEntry(entryBuilder.startSelector("Townless Player Color", colors, config.townless.townlessPlayerColor)
                .setDefaultValue("LIGHT_PURPLE")
                .setTooltip("The color of the townless player names.")
                .setSaveConsumer(newValue -> config.townless.townlessPlayerColor = newValue)
                .build());

                 // Nearby Player Horizontal Position
                 nearby.addEntry(entryBuilder.startIntSlider("Horizontal Position (X)", config.nearby.nearbyListXPos, 10, 770)
                 .setDefaultValue(770)
                 .setTooltip("The horizontal position on the HUD.")
                 .setSaveConsumer(newValue -> config.nearby.nearbyListXPos = newValue)
                 .build());

                 // Nearby Player Vertical Position
                 nearby.addEntry(entryBuilder.startIntSlider("Vertical Position (Y)", config.nearby.nearbyListYPos, 20, 500)
                 .setDefaultValue(275)
                 .setTooltip("The vertical position on the HUD.")
                 .setSaveConsumer(newValue -> config.nearby.nearbyListYPos = newValue)
                 .build());

                 // Nearby Player Text Color
                 nearby.addEntry(entryBuilder.startSelector("Nearby Text Color", colors, config.nearby.nearbyTextColor)
                 .setDefaultValue("GOLD")
                 .setTooltip("The color of the 'Nearby Players' text.")
                 .setSaveConsumer(newValue -> config.nearby.nearbyTextColor = newValue)
                 .build());

                 // Nearby Player Player Color
                 nearby.addEntry(entryBuilder.startSelector("Nearby Player Color", colors, config.nearby.nearbyPlayerColor)
                 .setDefaultValue("GOLD")
                 .setTooltip("The color of the nearby player names.")
                 .setSaveConsumer(newValue -> config.nearby.nearbyPlayerColor = newValue)
                 .build());

                 // Nearby Player Name
                 nearby.addEntry(entryBuilder.startStrField("Player Name", config.nearby.playerName)
                 .setDefaultValue("")
                 .setTooltip("The name of the player to check nearby.")
                 .setSaveConsumer(newValue -> config.nearby.playerName = newValue)
                 .build());

                 // Nearby X Radius
                 nearby.addEntry(entryBuilder.startIntSlider("X Radius", config.nearby.xRadius, 50, 10000)
                 .setDefaultValue(500)
                 .setTooltip("The x radius (in blocks) to check inside.")
                 .setSaveConsumer(newValue -> config.nearby.xRadius = newValue)
                 .build());

                 // Nearby Z Radius
                 nearby.addEntry(entryBuilder.startIntSlider("Z Radius", config.nearby.zRadius, 50, 10000)
                 .setDefaultValue(500)
                 .setTooltip("The z radius (in blocks) to check inside.")
                 .setSaveConsumer(newValue -> config.nearby.zRadius = newValue)
                 .build());
                //#endregion

                Screen screen = builder.build();
                MinecraftClient.getInstance().openScreen(screen);

                builder.setSavingRunnable(() -> 
                {  
                    ConfigUtils.serializeConfig(config);
                });
			}
        });

        //#region HUDRenderCallback
        HudRenderCallback.EVENT.register(e -> 
        {     
            if (!config.general.enableMod) return;

            // Create client & renderer
            final MinecraftClient client = MinecraftClient.getInstance();
            final TextRenderer renderer = client.textRenderer;

            //#region Draw Townless List
            townlessPlayerOffset = config.townless.townlessListYPos; // Position of the first player, who determines where the list will be.
             
            Formatting townlessTextFormatting = Formatting.byName(config.townless.townlessTextColor);
            String townlessText = new LiteralText("Townless Players").formatted(townlessTextFormatting).asFormattedString();
 
            // Draw 'Townless Players' text.
            renderer.drawWithShadow(townlessText, config.townless.townlessListXPos, config.townless.townlessListYPos - 15, Formatting.WHITE.getColorValue());
 
            if (townless.size() >= 1)
            {            
                for (int i = 0; i < townless.size(); i++) 
                {
                    final JsonObject currentPlayer = (JsonObject) townless.get(i);
 
                    Formatting playerTextFormatting = Formatting.byName(config.townless.townlessPlayerColor);
                    String playerName = new LiteralText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting).asFormattedString();
 
                    final Integer playerX = currentPlayer.get("x").getAsInt();
                    final Integer playerY = currentPlayer.get("y").getAsInt();
                    final Integer playerZ = currentPlayer.get("z").getAsInt();
 
                    // If underground, display "Underground" instead of their position
                    if (playerX == 0 && playerZ == 0)
                    {
                        renderer.drawWithShadow(playerName + " Underground", config.townless.townlessListXPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                    }
                    else 
                    {                   
                        renderer.drawWithShadow(playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.townless.townlessListXPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                    }
 
                    // Add offset for the next player.
                    townlessPlayerOffset += 10;
                }
            }
            //#endregion

            //#region Draw Nearby List
            nearbyPlayerOffset = config.nearby.nearbyListYPos; // Position of the first player, who determines where the list will be.
             
            Formatting nearbyTextFormatting = Formatting.byName(config.nearby.nearbyTextColor);
            String nearbyText = new LiteralText("Nearby Players").formatted(nearbyTextFormatting).asFormattedString();
 
            // Draw 'Nearby Players' text.
            renderer.drawWithShadow(nearbyText, config.nearby.nearbyListXPos, config.nearby.nearbyListYPos - 15, Formatting.WHITE.getColorValue());
 
            if (nearby.size() >= 1)
            {            
                for (int i = 0; i < nearby.size(); i++) 
                {
                    final JsonObject currentPlayer = (JsonObject) nearby.get(i);
 
                    Formatting playerTextFormatting = Formatting.byName(config.nearby.nearbyPlayerColor);
                    String playerName = new LiteralText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting).asFormattedString();
 
                    final Integer playerX = currentPlayer.get("x").getAsInt();
                    final Integer playerY = currentPlayer.get("y").getAsInt();
                    final Integer playerZ = currentPlayer.get("z").getAsInt();
 
                    renderer.drawWithShadow(playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.nearby.nearbyListXPos, nearbyPlayerOffset, Formatting.WHITE.getColorValue());
 
                    // Add offset for the next player.
                    nearbyPlayerOffset += 10;
                }
            }
            //#endregion
        });
        //#endregion
    }

    //#region API Calls
    static JsonArray getTownless()
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/townlessplayers");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200) 
            {
                String inline = "";
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) 
                {
                    inline += scanner.nextLine();
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                final JsonArray townlessArray = (JsonArray) parse.parse(inline);
                    
                return townlessArray;
            }
        }
        catch (final Exception exc) 
        {
            return new JsonArray();
        }

        return new JsonArray();
    }

    static JsonArray getNearby(ModConfig config)
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/onlineplayers/" + config.nearby.playerName + "/nearby/" + config.nearby.xRadius + "/" + config.nearby.zRadius);

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200) 
            {
                String inline = "";
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) 
                {
                    inline += scanner.nextLine();
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                final JsonArray nearbyArray = (JsonArray) parse.parse(inline);
                    
                return nearbyArray;
            }
        }
        catch (final Exception exc) 
        {
            return new JsonArray();
        }

        return new JsonArray();
    }
    //#endregion
}