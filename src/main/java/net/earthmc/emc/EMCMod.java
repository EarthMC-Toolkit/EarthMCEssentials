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

        KeyBinding f4 = KeyBindingHelper.registerKeyBinding(new KeyBinding("Townless Players", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> 
        {
            if (f4.isPressed())
            {
                ConfigBuilder builder = ConfigBuilder.create().setTitle("EarthMC Essentials Config");

                ConfigCategory general = builder.getOrCreateCategory("category.emc-essentials.general");
                ConfigCategory townless = builder.getOrCreateCategory("category.emc-essentials.townless");
    
                ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                // Townless List Y Offset
                general.addEntry(entryBuilder.startBooleanToggle("Enable Mod", config.general.enableMod)
                .setDefaultValue(true)
                .setTooltip("Toggles the mod on or off.")
                .setSaveConsumer(newValue -> config.general.enableMod = newValue)
                .build());

                // Townless List Y Offset
                townless.addEntry(entryBuilder.startIntField("Townless List Y Offset", config.townlessList.townlessListYOffset)
                .setDefaultValue(20)
                .setTooltip("The vertical offset (in pixels) of the townless list.")
                .setSaveConsumer(newValue -> config.townlessList.townlessListYOffset = newValue)
                .build());

                // Townless List X Offset
                townless.addEntry(entryBuilder.startIntField("Townless List X Offset", config.townlessList.townlessListXOffset)
                .setDefaultValue(5)
                .setTooltip("The horizontal offset (in pixels) of the townless list.")
                .setSaveConsumer(newValue -> config.townlessList.townlessListXOffset = newValue)
                .build());

                // Townless Text Y Offset
                townless.addEntry(entryBuilder.startIntField("Townless Text Y Offset", config.townlessText.townlessTextYOffset)
                .setDefaultValue(5)
                .setTooltip("The vertical offset (in pixels) of the 'Townless Players' text.")
                .setSaveConsumer(newValue -> config.townlessText.townlessTextYOffset = newValue)
                .build());

                // Townless Text X Offset
                townless.addEntry(entryBuilder.startIntField("Townless Text X Offset", config.townlessText.townlessTextXOffset)
                .setDefaultValue(5) 
                .setTooltip("The horizontal offset (in pixels) of the 'Townless Players' text.")
                .setSaveConsumer(newValue -> config.townlessText.townlessTextXOffset = newValue)
                .build());

                // builder.setSavingRunnable(() -> 
                // {
                    
                // });
    
                Screen screen = builder.build();
                MinecraftClient.getInstance().openScreen(screen);
			}
        });

        HudRenderCallback.EVENT.register(e -> 
        {               
            // This is where the first player will be, who determines where the list will be.
            currentYOffset = config.townlessList.townlessListYOffset;

            // Create client
            final MinecraftClient client = MinecraftClient.getInstance();

            // Create renderer
            final TextRenderer renderer = client.textRenderer;

            String townlessText = new LiteralText("Townless Players").formatted(Formatting.LIGHT_PURPLE).asFormattedString();
            renderer.draw(townlessText, config.townlessText.townlessTextXOffset, config.townlessText.townlessTextYOffset, Formatting.WHITE.getColorValue());

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
                        renderer.draw(playerName + " Underground", config.townlessList.townlessListXOffset, currentYOffset, Formatting.WHITE.getColorValue());
                    }
                    else 
                    {                   
                        renderer.draw(playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.townlessList.townlessListXOffset, currentYOffset, Formatting.WHITE.getColorValue());
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