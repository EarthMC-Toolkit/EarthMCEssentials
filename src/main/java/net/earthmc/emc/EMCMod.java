package net.earthmc.emc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.Timer;
import java.util.TimerTask;

public class EMCMod implements ModInitializer 
{
    JsonArray townless;
    int currentYOffset;

    @Override
    public void onInitialize() // Called when Minecraft starts.
    {
        System.out.println("EarthMC Mod Initialized!");

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
            currentYOffset = 20;

            // Create client
            final MinecraftClient client = MinecraftClient.getInstance();

            // Create renderer
            final TextRenderer renderer = client.textRenderer;

            renderer.draw("Townless Players", 1, 5, 0xffffff);

            if (townless.size() >= 1)
            {            
                for (int i = 0; i < townless.size(); i++) 
                {
                    final JsonObject currentPlayer = (JsonObject) townless.get(i);

                    final String playerName = currentPlayer.get("name").getAsString();

                    final Integer playerX = currentPlayer.get("x").getAsInt();
                    final Integer playerY = currentPlayer.get("y").getAsInt();
                    final Integer playerZ = currentPlayer.get("z").getAsInt();

                    // Draw each player with offset
                    renderer.draw(playerName + " - " + playerX + ", " + playerY + ", " + playerZ, 5, currentYOffset, 0xffffff);

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