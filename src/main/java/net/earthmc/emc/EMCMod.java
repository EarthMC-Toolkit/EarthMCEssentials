package net.earthmc.emc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.earthmc.emc.utils.ConfigUtils;
import net.earthmc.emc.utils.EmcApi;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class EMCMod implements ModInitializer
{
    public static JsonArray townless;
    public static JsonArray nearby;

    int townlessPlayerOffset;
    int nearbyPlayerOffset;

    public static ModConfig config;

    public static String[] colors;
    KeyBinding configKeybind;

    public static MinecraftClient client;

    public static String clientName = "";
    public static String clientTownName = "";
    public static String clientNationName = "";

    public static Screen screen;

    @Override
    public void onInitialize() // Called when Minecraft starts.
    {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        configKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        colors = new String[] { "BLUE", "DARK_BLUE", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "RED", "DARK_RED",
                "LIGHT_PURPLE", "DARK_PURPLE", "YELLOW", "GOLD", "GRAY", "DARK_GRAY", "BLACK", "WHITE" };

        townless = EmcApi.getTownless();
        nearby = new JsonArray();

        //#region ClientTickEvents
        ClientTickEvents.END_CLIENT_TICK.register(client -> 
        {
            // Pressed F4 (Config Menu)
            if (configKeybind.wasPressed())
            {
                screen = ModMenuIntegration.getConfigBuilder().build();

                client.openScreen(screen);
		    }
        });
        //#endregion

        //#region HudRenderCallback
        HudRenderCallback.EVENT.register(event ->
        {
            if (!config.general.enableMod) return;

            final TextRenderer renderer = client.textRenderer;

            if (config.townless.enabled)
            {
                townlessPlayerOffset = config.townless.townlessListYPos; // Position of the first player, who determines where the list will be.

                Formatting townlessTextFormatting = Formatting.byName(config.townless.headingTextColour);
                String townlessText = new TranslatableText("Townless Players").formatted(townlessTextFormatting).asFormattedString();

                // Draw heading.
                renderer.drawWithShadow(townlessText, config.townless.townlessListXPos, config.townless.townlessListYPos - 15, Formatting.WHITE.getColorValue());

                if (townless.size() >= 1)
                {
                    for (int i = 0; i < townless.size(); i++)
                    {
                        final JsonObject currentPlayer = (JsonObject) townless.get(i);

                        Formatting playerTextFormatting = Formatting.byName(config.townless.playerTextColour);
                        String playerName = new TranslatableText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting).asFormattedString();

                        if (config.townless.showCoords)
                        {
                            final int playerX = currentPlayer.get("x").getAsInt();
                            final int playerY = currentPlayer.get("y").getAsInt();
                            final int playerZ = currentPlayer.get("z").getAsInt();

                            // If underground, display "Underground" instead of their position
                            if (playerX == 0 && playerZ == 0)
                            {
                                renderer.drawWithShadow(playerName + " Underground", config.townless.townlessListXPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                            }
                            else
                            {
                                renderer.drawWithShadow(playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.townless.townlessListXPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                            }
                        }
                        else
                        {
                            renderer.drawWithShadow(playerName, config.townless.townlessListXPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                        }

                        // Add offset for the next player.
                        townlessPlayerOffset += 10;
                    }
                }
            }

            if (config.nearby.enabled)
            {
                nearbyPlayerOffset = config.nearby.nearbyListYPos; // Position of the first player, who determines where the list will be.

                Formatting nearbyTextFormatting = Formatting.byName(config.nearby.headingTextColour);
                String nearbyText = new TranslatableText("Nearby Players").formatted(nearbyTextFormatting).asFormattedString();

                // Draw heading.
                renderer.drawWithShadow(nearbyText, config.nearby.nearbyListXPos, config.nearby.nearbyListYPos - 15, Formatting.WHITE.getColorValue());

                if (nearby.size() >= 1)
                {
                    for (int i = 0; i < nearby.size(); i++)
                    {
                        final JsonObject currentPlayer = (JsonObject) nearby.get(i);

                        Formatting playerTextFormatting = Formatting.byName(config.nearby.playerTextColour);
                        String playerName = new LiteralText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting).asFormattedString();

                        final int playerX = currentPlayer.get("x").getAsInt();
                        final int playerY = currentPlayer.get("y").getAsInt();
                        final int playerZ = currentPlayer.get("z").getAsInt();

                        renderer.drawWithShadow(playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.nearby.nearbyListXPos, nearbyPlayerOffset, Formatting.WHITE.getColorValue());

                        // Add offset for the next player.
                        nearbyPlayerOffset += 10;
                    }
                }
            }

            if (config.townInfo.enabled)
            {
                Formatting townInfoTextFormatting = Formatting.byName(config.townInfo.headingTextColour);
                String townInfoText = new TranslatableText("Town Information").formatted(townInfoTextFormatting).asFormattedString();

                // Draw heading.
                renderer.drawWithShadow(townInfoText, config.townInfo.townInfoXPos, config.townInfo.townInfoYPos - 15, Formatting.WHITE.getColorValue());

                // Draw info.

            }

            if (config.nationInfo.enabled)
            {
                Formatting nationInfoTextFormatting = Formatting.byName(config.nationInfo.headingTextColour);
                String nationInfoText = new TranslatableText("Nation Information").formatted(nationInfoTextFormatting).asFormattedString();

                // Draw heading.
                renderer.drawWithShadow(nationInfoText, config.nationInfo.nationInfoXPos, config.nationInfo.nationInfoYPos - 15, Formatting.WHITE.getColorValue());

                // Draw info.
            }
        });
        //#endregion
    }
}