package net.earthmc.emc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import org.json.simple.parser.JSONParser;
import org.json.simple.*;

public class EMCMod implements ModInitializer 
{
	@Override
	public void onInitialize() // Called when Minecraft starts.
	{
        System.out.println("EarthMC Mod Initialized!");
        
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
                final JSONParser parse = new JSONParser();
                final JSONObject data_obj = (JSONObject) parse.parse(inline);

                System.out.println("Townless data: " + data_obj);

                // for (int i = 0; i < arr.size(); i++) 
                // {
                //     JSONObject new_obj = (JSONObject) arr.get(i);

                //     if (new_obj.get("Slug").equals("albania")) 
                // 	{
                //         System.out.println("Total Recovered: " + new_obj.get("TotalRecovered"));
                //         break;
                //     }
                // }
            }
        } 
        catch (final Exception exc) 
        {
            exc.printStackTrace();
        }

		HudRenderCallback.EVENT.register(e -> 
		{
            // Create client
            final MinecraftClient client = MinecraftClient.getInstance();

			// Create renderer
            final TextRenderer renderer = client.textRenderer;

			// Draw each player with offset from player before (will use for loop in future)
            renderer.draw("TownlessPlayer1", 1, 5, 0xffffff);
			renderer.draw("TownlessPlayer2", 1, 15, 0xffffff);
			renderer.draw("TownlessPlayer3", 1, 25, 0xffffff);
        });
    }
}