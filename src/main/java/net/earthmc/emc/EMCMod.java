package net.earthmc.emc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.Timer;
import java.util.TimerTask;

public class EMCMod implements ModInitializer {
    JsonArray townless;
    int currentYOffset;

    ModConfig config;
    ModMenuIntegration modMenu;

    @Override
    public void onInitialize() // Called when Minecraft starts.
    {
        System.out.println("EarthMC Mod Initialized!");

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        townless = getTownless();

        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run() 
            {
                townless = getTownless();
            }
        }, 0, 2*60*1000);

        HudRenderCallback.EVENT.register(e -> 
        {               
            // This is where the first player will be, who determines where the list will be.
            currentYOffset = config.townlessListYOffset;

            // Create client
            final MinecraftClient client = MinecraftClient.getInstance();

            // Create renderer
            final TextRenderer renderer = client.textRenderer;

            ConfigBuilder builder = ConfigBuilder.create().setTitle("EarthMC Config");
            ConfigCategory general = builder.getOrCreateCategory("category.emc-essentials.general");

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startIntField("Townless List Y Offset", config.townlessListYOffset)
            .setDefaultValue(20) // Recommended: Used when user click "Reset"
            .setTooltip("The vertical offset (in pixels) of the townless list.") // Optional: Shown when the user hover over this option
            .setSaveConsumer(newValue -> config.townlessListYOffset = newValue) // Recommended: Called when user save the config
            .build());

            Screen screen = builder.build();
            client.openScreen(screen);

            String townlessText = new LiteralText("Townless Players").formatted(Formatting.LIGHT_PURPLE).asFormattedString();
            renderer.draw(townlessText, config.townlessTextXOffset, config.townlessTextYOffset, 0xffffff);

            if (townless.size() >= 1)
            {            
                for (int i = 0; i < townless.size(); i++) 
                {
                    final JsonObject currentPlayer = (JsonObject) townless.get(i);

                    String playerName = new LiteralText(currentPlayer.get("name").getAsString()).formatted(Formatting.LIGHT_PURPLE).asFormattedString();

                    final Integer playerX = currentPlayer.get("x").getAsInt();
                    final Integer playerY = currentPlayer.get("y").getAsInt();
                    final Integer playerZ = currentPlayer.get("z").getAsInt();

                    // If underground, display "Underground" instead of their position
                    if (playerX == 0 && playerZ == 0)
                    {
                        renderer.draw(playerName + " Underground", config.townlessTextXOffset, currentYOffset, 0xffffff);
                    }
                    else 
                    {                   
                        renderer.draw(playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.townlessListXOffset, currentYOffset, 0xffffff);
                    }

                    // Add offset for the next player.
                    currentYOffset += 10;
                }
            }
        });
    }

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
}