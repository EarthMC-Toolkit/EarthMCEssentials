package net.emc.emce;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.emc.emce.utils.ConfigUtils;
import net.emc.emce.utils.EmcApi;
import net.emc.emce.utils.ModUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class EMCE implements ModInitializer
{
    int townlessPlayerOffset, nearbyPlayerOffset;

    public static String[] colors;

    public static Integer queue = null;

    public static String clientName = "";
    public static String clientTownName = "";
    public static String clientNationName = "";

    public static MinecraftClient client;
    public static Screen screen;
    public static ModConfig config;

    public static JsonArray townless, nearby, allNations, allTowns;

    KeyBinding configKeybind;

    @Override
    public void onInitialize() // Called when Minecraft starts.
    {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        configKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        colors = new String[] { "BLUE", "DARK_BLUE", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "RED", "DARK_RED",
                "LIGHT_PURPLE", "DARK_PURPLE", "YELLOW", "GOLD", "GRAY", "DARK_GRAY", "BLACK", "WHITE" };

        townless = EmcApi.getTownless();
        allNations = new JsonArray();
        allTowns = new JsonArray();
        nearby = new JsonArray(); // 'new' because the client cant be near anyone yet.

        //#region ClientTickEvents
        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            // Pressed F4 (Config Menu)
            if (configKeybind.wasPressed())
            {
                screen = ConfigUtils.getConfigBuilder().build();

                client.openScreen(screen);
		    }
        });
        //#endregion

        //#region HudRenderCallback
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) ->
        {
            if (!config.general.enableMod || !ModUtils.shouldRender()) return;

            final TextRenderer renderer = client.textRenderer;

            ModUtils.State townlessState = config.townless.positionState;
            ModUtils.State nearbyState = config.nearby.positionState;

            if (config.townless.enabled)
            {
                if (!config.townless.presetPositions)
                {
                    // Position of the first player, who determines where the list will be.
                    townlessPlayerOffset = config.townless.yPos;

                    Formatting townlessTextFormatting = Formatting.byName(config.townless.headingTextColour);
                    MutableText townlessText = new TranslatableText("text_townless_header", townless.size()).formatted(townlessTextFormatting);

                    // Draw heading.
                    renderer.drawWithShadow(matrixStack, townlessText, config.townless.xPos, config.townless.yPos - 15, 16777215);

                    if (townless.size() >= 1)
                    {
                        for (int i = 0; i < townless.size(); i++)
                        {
                            Formatting playerTextFormatting = Formatting.byName(config.townless.playerTextColour);

                            if (config.townless.maxLength >= 1)
                            {
                                if (i >= config.townless.maxLength)
                                {
                                    MutableText remainingText = new TranslatableText("text_townless_remaining", townless.size()-i).formatted(playerTextFormatting);
                                    renderer.drawWithShadow(matrixStack, remainingText, config.townless.xPos, townlessPlayerOffset, 16777215);
                                    break;
                                }
                            }

                            final JsonObject currentPlayer = (JsonObject) townless.get(i);
                            MutableText playerName = new TranslatableText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting);

                            renderer.drawWithShadow(matrixStack, playerName, config.townless.xPos, townlessPlayerOffset, 16777215);

                            // Add offset for the next player.
                            townlessPlayerOffset += 10;
                        }
                    }
                }
                else // No advanced positioning, use preset states.
                {
                    int townlessLongest, nearbyLongest;

                    townlessLongest = Math.max(ModUtils.getLongestElement(townless), ModUtils.getTextWidth(new TranslatableText("text_townless_header", townless.size())));
                    nearbyLongest = Math.max(ModUtils.getNearbyLongestElement(nearby), ModUtils.getTextWidth(new TranslatableText("text_nearby_header", nearby.size())));

                    switch(townlessState)
                    {
                        case TOP_MIDDLE:
                        {
                            if (nearbyState.equals(ModUtils.State.TOP_MIDDLE))
                                townlessState.setX(ModUtils.getWindowWidth() / 2 - (townlessLongest + nearbyLongest) / 2 );
                            else
                                townlessState.setX(ModUtils.getWindowWidth() / 2 - townlessLongest / 2);

                            townlessState.setY(16);
                            break;
                        }
                        case TOP_RIGHT:
                        {
                            townlessState.setX(ModUtils.getWindowWidth() - townlessLongest - 5);
                            if (client.player != null) townlessState.setY(ModUtils.getStatusEffectOffset(client.player.getStatusEffects()));
                            break;
                        }
                        case LEFT:
                        {
                            townlessState.setX(5);
                            townlessState.setY(ModUtils.getWindowHeight() / 2 - ModUtils.getTownlessArrayHeight(townless, config.townless.maxLength) / 2);
                            break;
                        }
                        case RIGHT:
                        {
                            townlessState.setX(ModUtils.getWindowWidth() - townlessLongest - 5);
                            townlessState.setY(ModUtils.getWindowHeight() / 2 - ModUtils.getTownlessArrayHeight(townless, config.townless.maxLength) / 2);
                            break;
                        }
                        case BOTTOM_RIGHT:
                        {
                            townlessState.setX(ModUtils.getWindowWidth() - townlessLongest - 5);
                            townlessState.setY(ModUtils.getWindowHeight() - ModUtils.getTownlessArrayHeight(townless, config.townless.maxLength) - 22);
                            break;
                        }
                        case BOTTOM_LEFT:
                        {
                            townlessState.setX(5);
                            townlessState.setY(ModUtils.getWindowHeight() - ModUtils.getTownlessArrayHeight(townless, config.townless.maxLength) - 22);
                            break;
                        }
                        default: // Defaults to top left
                        {
                            townlessState.setX(5);
                            townlessState.setY(16);
                            break;
                        }
                    }

                    Formatting townlessTextFormatting = Formatting.byName(config.townless.headingTextColour);
                    MutableText townlessText = new TranslatableText("text_townless_header", townless.size()).formatted(townlessTextFormatting);

                    // Draw heading.
                    renderer.drawWithShadow(matrixStack, townlessText, townlessState.getX(), townlessState.getY() - 10, 16777215);

                    if (townless.size() >= 1)
                    {
                        for (int i = 0; i < townless.size(); i++)
                        {
                            Formatting playerTextFormatting = Formatting.byName(config.townless.playerTextColour);

                            if (config.townless.maxLength >= 1)
                            {
                                if (i >= config.townless.maxLength)
                                {
                                    MutableText remainingText = new TranslatableText("text_townless_remaining", townless.size()-i).formatted(playerTextFormatting);
                                    renderer.drawWithShadow(matrixStack, remainingText, townlessState.getX(), townlessState.getY() + i*10, 16777215);
                                    break;
                                }
                            }

                            final JsonObject currentPlayer = (JsonObject) townless.get(i);
                            MutableText playerName = new TranslatableText(currentPlayer.get("name").getAsString()).formatted(playerTextFormatting);

                            renderer.drawWithShadow(matrixStack, playerName, townlessState.getX(), townlessState.getY() + i*10, 16777215);
                        }
                    }
                }
            }

            if (config.nearby.enabled)
            {
                if (!config.nearby.presetPositions) // Not using preset positions
                {
                    // Position of the first player, who determines where the list will be.
                    nearbyPlayerOffset = config.nearby.yPos;

                    Formatting nearbyTextFormatting = Formatting.byName(config.nearby.headingTextColour);
                    MutableText nearbyText = new TranslatableText("text_nearby_header", nearby.size()).formatted(nearbyTextFormatting);

                    // Draw heading.
                    renderer.drawWithShadow(matrixStack, nearbyText, config.nearby.xPos, config.nearby.yPos - 15, 16777215);

                    if (nearby.size() >= 1)
                    {
                        if (client.player == null) return;

                        for (int i = 0; i < nearby.size(); i++)
                        {
                            JsonObject currentPlayer = (JsonObject) nearby.get(i);
                            int distance = Math.abs(currentPlayer.get("x").getAsInt() - (int) EMCE.client.player.getX()) +
                                           Math.abs(currentPlayer.get("z").getAsInt() - (int) EMCE.client.player.getZ());

                            if (currentPlayer.get("name").getAsString().equals(clientName)) continue;

                            Formatting playerTextFormatting = Formatting.byName(config.nearby.playerTextColour);
                            MutableText playerText = new TranslatableText("text_nearby_name", currentPlayer.get("name").getAsString(), distance).formatted(playerTextFormatting);

                            renderer.drawWithShadow(matrixStack, playerText, config.nearby.xPos, nearbyPlayerOffset, 16777215);

                            // Add offset for the next player.
                            nearbyPlayerOffset += 10;
                        }
                    }
                }
                else
                {
                    int nearbyLongest, townlessLongest;

                    nearbyLongest = Math.max(ModUtils.getNearbyLongestElement(nearby), ModUtils.getTextWidth(new TranslatableText("text_nearby_header", nearby.size())));
                    townlessLongest = Math.max(ModUtils.getLongestElement(townless), ModUtils.getTextWidth(new TranslatableText("text_townless_header", townless.size())));

                    switch(nearbyState)
                    {
                        case TOP_MIDDLE:
                        {
                            if (townlessState.equals(ModUtils.State.TOP_MIDDLE)) {
                                nearbyState.setX(ModUtils.getWindowWidth() / 2 - (townlessLongest + nearbyLongest) / 2 + townlessLongest + 5);
                                nearbyState.setY(townlessState.getY());
                            }
                            else {
                                nearbyState.setX(ModUtils.getWindowWidth() / 2 - nearbyLongest / 2);
                                nearbyState.setY(16);
                            }

                            break;
                        }
                        case TOP_RIGHT:
                        {
                            if (townlessState.equals(ModUtils.State.TOP_RIGHT))
                                nearbyState.setX(ModUtils.getWindowWidth() - townlessLongest - nearbyLongest - 15);
                            else
                                nearbyState.setX(ModUtils.getWindowWidth() - nearbyLongest - 5);

                            if (client.player != null) nearbyState.setY(ModUtils.getStatusEffectOffset(client.player.getStatusEffects()));

                            break;
                        }
                        case LEFT:
                        {
                            if (townlessState.equals(ModUtils.State.LEFT)) {
                                nearbyState.setX(townlessLongest + 10);
                                nearbyState.setY(townlessState.getY());
                            }
                            else {
                                nearbyState.setX(5);
                                nearbyState.setY(ModUtils.getWindowHeight() / 2 - ModUtils.getArrayHeight(nearby) / 2);
                            }

                            break;
                        }
                        case RIGHT:
                        {
                            if (townlessState.equals(ModUtils.State.RIGHT)) {
                                nearbyState.setX(ModUtils.getWindowWidth() - townlessLongest - nearbyLongest - 15);
                                nearbyState.setY(townlessState.getY());
                            }
                            else {
                                nearbyState.setX(ModUtils.getWindowWidth() - nearbyLongest - 5);
                                nearbyState.setY(ModUtils.getWindowHeight() / 2 - ModUtils.getArrayHeight(nearby) / 2);
                            }

                            break;
                        }
                        case BOTTOM_RIGHT:
                        {
                            if (townlessState.equals(ModUtils.State.BOTTOM_RIGHT))
                            {
                                nearbyState.setX(ModUtils.getWindowWidth() - townlessLongest - nearbyLongest - 15);
                                nearbyState.setY(townlessState.getY());
                            }
                            else {
                                nearbyState.setX(ModUtils.getWindowWidth() - nearbyLongest - 15);
                                nearbyState.setY(ModUtils.getWindowHeight() - ModUtils.getArrayHeight(nearby) - 10);
                            }

                            break;
                        }
                        case BOTTOM_LEFT:
                        {
                            if (townlessState.equals(ModUtils.State.BOTTOM_LEFT)) {
                                nearbyState.setX(townlessLongest + 15);
                                nearbyState.setY(townlessState.getY());
                            }
                            else {
                                nearbyState.setX(5);
                                nearbyState.setY(ModUtils.getWindowHeight() - ModUtils.getArrayHeight(nearby) - 10);
                            }

                            break;
                        }
                        default: // Defaults to top left
                        {
                            if (townlessState.equals(ModUtils.State.TOP_LEFT))
                                nearbyState.setX(townlessLongest + 15);
                            else
                                nearbyState.setX(5);

                            nearbyState.setY(16);

                            break;
                        }
                    }

                    Formatting nearbyTextFormatting = Formatting.byName(config.nearby.headingTextColour);
                    MutableText nearbyText = new TranslatableText("text_nearby_header", nearby.size()).formatted(nearbyTextFormatting);

                    // Draw heading.
                    renderer.drawWithShadow(matrixStack, nearbyText, nearbyState.getX(), nearbyState.getY() - 10, 16777215);

                    if (nearby.size() >= 1)
                    {
                        if (client.player == null) return;

                        for (int i = 0; i < nearby.size(); i++)
                        {
                            JsonObject currentPlayer = (JsonObject) nearby.get(i);
                            int distance = Math.abs(currentPlayer.get("x").getAsInt() - (int) EMCE.client.player.getX()) +
                                           Math.abs(currentPlayer.get("z").getAsInt() - (int) EMCE.client.player.getZ());

                            if (currentPlayer.get("name").getAsString().equals(clientName)) continue;

                            Formatting playerTextFormatting = Formatting.byName(config.nearby.playerTextColour);
                            MutableText playerText = new TranslatableText(currentPlayer.get("name").getAsString() + ": " + distance + "m").formatted(playerTextFormatting);

                            renderer.drawWithShadow(matrixStack, playerText, nearbyState.getX(), nearbyState.getY() + 10*i, 16777215);
                        }
                    }
                }
            }
        });
        //#endregion
    }
}