package net.earthmc.emc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class EMCMod implements ModInitializer
{
    public static JsonArray towns;
    public static JsonArray oldTowns;
    public static JsonArray townless;
    public static JsonArray nearby;

    public static JsonObject townInfo;
    public static JsonObject nationInfo;

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

        oldTowns = EmcApi.getTowns();
        townless = EmcApi.getTownless();
        nearby = new JsonArray(); // New because the client cant be near anyone yet.

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
        HudRenderCallback.EVENT.register(new HudRenderCallback() {

            @Override
            public void onHudRender(MatrixStack matrixStack, float tickDelta) {
                if (!config.general.enableMod) return;

                final TextRenderer renderer = client.textRenderer;

                if (config.townEvents.enabled)
                {
                    if (towns.size() >= 1)
                    {
                        for (int i = 0; i < towns.size(); i++)
                        {
                            final JsonObject currentTown = (JsonObject) towns.get(i);
                            final JsonObject oldTown = null; // MAKE THIS INTO A .FIND

                            if (currentTown.get("pvp").getAsBoolean() != oldTown.get("pvp").getAsBoolean())
                            {

                            }

                            if (currentTown.get("mobs").getAsBoolean() != oldTown.get("mobs").getAsBoolean())
                            {

                            }

                            if (currentTown.get("public").getAsBoolean() != oldTown.get("public").getAsBoolean())
                            {

                            }

                            if (currentTown.get("explosion").getAsBoolean() != oldTown.get("explosion").getAsBoolean())
                            {

                            }

                            if (currentTown.get("fire").getAsBoolean() != oldTown.get("fire").getAsBoolean())
                            {

                            }

                            if (currentTown.get("capital").getAsBoolean() != oldTown.get("capital").getAsBoolean())
                            {

                            }
                        }

                        Formatting eventTextFormatting = Formatting.byName(config.townless.headingTextColour);
                        String eventText = new TranslatableText("Townless Players").formatted(eventTextFormatting).asString();

                        // Draw heading.
                        renderer.drawWithShadow(null, eventText, config.townless.xPos, config.townless.yPos - 15, Formatting.WHITE.getColorValue());
                    }
                }

                if (config.townless.enabled)
                {
                    // Position of the first player, who determines where the list will be.
                    townlessPlayerOffset = config.townless.yPos;

                    Formatting townlessTextFormatting = Formatting.byName(config.townless.headingTextColour);
                    String townlessText = new TranslatableText("Townless Players").formatted(townlessTextFormatting).asString();

                    // Draw heading.
                    renderer.drawWithShadow(null, townlessText, config.townless.xPos, config.townless.yPos - 15, Formatting.WHITE.getColorValue());

                    if (townless.size() >= 1)
                    {
                        for (int i = 0; i < townless.size(); i++)
                        {
                            final JsonObject currentPlayer = (JsonObject) townless.get(i);

                            Formatting playerTextFormatting = Formatting.byName(config.townless.playerTextColour);
                            String playerName = new TranslatableText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting).asString();

                            if (config.townless.showCoords)
                            {
                                final int playerX = currentPlayer.get("x").getAsInt();
                                final int playerY = currentPlayer.get("y").getAsInt();
                                final int playerZ = currentPlayer.get("z").getAsInt();

                                // If underground, display "Underground" instead of their position
                                if (playerX == 0 && playerZ == 0)
                                {
                                    renderer.drawWithShadow(null, playerName + " Underground", config.townless.xPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                                }
                                else
                                {
                                    renderer.drawWithShadow(null, playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.townless.xPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                                }
                            }
                            else
                            {
                                renderer.drawWithShadow(null, playerName, config.townless.xPos, townlessPlayerOffset, Formatting.WHITE.getColorValue());
                            }

                            // Add offset for the next player.
                            townlessPlayerOffset += 10;
                        }
                    }
                }

                if (config.nearby.enabled)
                {
                    // Position of the first player, who determines where the list will be.
                    nearbyPlayerOffset = config.nearby.yPos;

                    Formatting nearbyTextFormatting = Formatting.byName(config.nearby.headingTextColour);
                    String nearbyText = new TranslatableText("Nearby Players").formatted(nearbyTextFormatting).asString();

                    // Draw heading.
                    renderer.drawWithShadow(null, nearbyText, config.nearby.xPos, config.nearby.yPos - 15, Formatting.WHITE.getColorValue());

                    if (nearby.size() >= 1)
                    {
                        for (int i = 0; i < nearby.size(); i++)
                        {
                            final JsonObject currentPlayer = (JsonObject) nearby.get(i);

                            Formatting playerTextFormatting = Formatting.byName(config.nearby.playerTextColour);
                            String playerName = new LiteralText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting).asString();

                            final int playerX = currentPlayer.get("x").getAsInt();
                            final int playerY = currentPlayer.get("y").getAsInt();
                            final int playerZ = currentPlayer.get("z").getAsInt();

                            renderer.drawWithShadow(null, playerName + " " + playerX + ", " + playerY + ", " + playerZ, config.nearby.xPos, nearbyPlayerOffset, Formatting.WHITE.getColorValue());

                            // Add offset for the next player.
                            nearbyPlayerOffset += 10;
                        }
                    }
                }

                if (config.townInfo.enabled && townInfo != null)
                {
                    Formatting townInfoHeadingFormatting = Formatting.byName(config.townInfo.headingTextColour);
                    Formatting infoTextFormatting = Formatting.byName(config.townInfo.infoTextColour);

                    // Draw heading.
                    String townInfoText = new TranslatableText("Town Information - " + clientTownName).formatted(townInfoHeadingFormatting).asString();
                    renderer.drawWithShadow(null, townInfoText, config.townInfo.xPos, config.townInfo.yPos - 15, Formatting.WHITE.getColorValue());

                    // Draw info.
                    String mayorText = new TranslatableText("Mayor: ").formatted(infoTextFormatting).asString();
                    if (townInfo.has("mayor")) renderer.drawWithShadow(null, mayorText + townInfo.get("mayor").getAsString(), config.townInfo.xPos, config.townInfo.yPos + 10, Formatting.WHITE.getColorValue());

                    String areaText = new TranslatableText("Area/Chunks: ").formatted(infoTextFormatting).asString();
                    if (townInfo.has("area")) renderer.drawWithShadow(null, areaText + townInfo.get("area").getAsString(), config.townInfo.xPos, config.townInfo.yPos + 20, Formatting.WHITE.getColorValue());

                    String residentsText = new TranslatableText("Residents: ").formatted(infoTextFormatting).asString();
                    if (townInfo.has("residents")) renderer.drawWithShadow(null, residentsText + townInfo.get("residents").getAsJsonArray().size(), config.townInfo.xPos, config.townInfo.yPos + 30, Formatting.WHITE.getColorValue());

                    String locationText = new TranslatableText("Location: ").formatted(infoTextFormatting).asString();
                    if (townInfo.has("x") && townInfo.has("z")) renderer.drawWithShadow(null, locationText + townInfo.get("x").getAsString() + ", " + townInfo.get("z").getAsString(), config.townInfo.xPos, config.townInfo.yPos + 40, Formatting.WHITE.getColorValue());
                }

                if (config.nationInfo.enabled && nationInfo != null)
                {
                    Formatting nationInfoHeadingFormatting = Formatting.byName(config.nationInfo.headingTextColour);
                    Formatting nationInfoTextFormatting = Formatting.byName(config.nationInfo.infoTextColour);

                    // Draw heading.
                    String nationInfoText = new TranslatableText("Nation Information - " + clientNationName).formatted(nationInfoHeadingFormatting).asString();
                    renderer.drawWithShadow(null, nationInfoText, config.nationInfo.xPos, config.nationInfo.yPos - 15, Formatting.WHITE.getColorValue());

                    // Draw info.
                    String kingText = new TranslatableText("King: ").formatted(nationInfoTextFormatting).asString();
                    if (nationInfo.has("king")) renderer.drawWithShadow(null, kingText + nationInfo.get("king").getAsString(), config.nationInfo.xPos, config.nationInfo.yPos + 10, Formatting.WHITE.getColorValue());

                    String capitalText = new TranslatableText("Capital: ").formatted(nationInfoTextFormatting).asString();
                    if (nationInfo.has("capitalName")) renderer.drawWithShadow(null, capitalText + nationInfo.get("capitalName").getAsString(), config.nationInfo.xPos, config.nationInfo.yPos + 20, Formatting.WHITE.getColorValue());

                    String areaText = new TranslatableText("Area/Chunks: ").formatted(nationInfoTextFormatting).asString();
                    if (nationInfo.has("area")) renderer.drawWithShadow(null, areaText + nationInfo.get("area").getAsString(), config.nationInfo.xPos, config.nationInfo.yPos + 30, Formatting.WHITE.getColorValue());

                    String residentsText = new TranslatableText("Residents: ").formatted(nationInfoTextFormatting).asString();
                    if (nationInfo.has("residents")) renderer.drawWithShadow(null, residentsText + nationInfo.get("residents").getAsJsonArray().size(), config.nationInfo.xPos, config.nationInfo.yPos + 40, Formatting.WHITE.getColorValue());

                    String townsText = new TranslatableText("Towns: ").formatted(nationInfoTextFormatting).asString();
                    if (nationInfo.has("towns")) renderer.drawWithShadow(null, townsText + nationInfo.get("towns").getAsJsonArray().size(), config.nationInfo.xPos, config.nationInfo.yPos + 50, Formatting.WHITE.getColorValue());
                }
            }
        });
        //#endregion
    }            
}