package net.earthmc.emc;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class EMCMod implements ModInitializer {
    @Override
    public void onInitialize() // Called when Minecraft starts.
    {
        System.out.println("EarthMC Mod Initialized!");

		HudRenderCallback.EVENT.register(e -> 
		{
            // Add timer then uncomment drawTownless
            // drawTownless();
        });
    }

    static void drawTownless()
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/townlessplayers");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode != 200) 
            {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            }
            else 
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
                final JsonObject townlessArray = (JsonObject) parse.parse(inline);

                // Create client
                final MinecraftClient client = MinecraftClient.getInstance();

                // Create renderer
                final TextRenderer renderer = client.textRenderer;

                // Draw each player with offset from player before (will use for loop in future)
                renderer.draw(townlessArray.toString(), 1, 5, 0xffffff);

                // for (int i = 0; i < townlessArray.size(); i++) 
                // {
                //     JsonObject currentPlayer = (JsonObject) Array.get(townlessArray, i);
                // }
            }
        } 
        catch (final Exception exc) 
        {
            exc.printStackTrace();
        }
    }
}