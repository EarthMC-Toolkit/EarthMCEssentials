package net.emc.emce.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.config.ModConfig;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.ModUtils.State;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.emc.emce.EarthMCEssentials.instance;

public class OverlayRenderer {
    private static MinecraftClient client;
    private static TextRenderer renderer;
    private static MatrixStack matrixStack;

    private static ModConfig config;
    private static State townlessState, nearbyState;
    private static List<String> townless = new CopyOnWriteArrayList<>();

    private static final int currentNewsID = 0;
    private static final int color = 16777215;

    public static void Init() {
        config = ModConfig.instance();
        client = MinecraftClient.getInstance();
        renderer = client.textRenderer;

        townlessState = config.townless.positionState;
        nearbyState = config.nearby.positionState;

        UpdateStates(true, true);
    }

    public static void Clear() {
        instance().setNearbyPlayers(new JsonArray());
        townless = new CopyOnWriteArrayList<>();
    }

    public static JsonArray nearby() {
        return instance().getNearbyPlayers();
    }

    public static void SetTownless(List<String> townlessResidents) {
        townless = new CopyOnWriteArrayList<>(townlessResidents);
    }

    public static void UpdateStates(boolean updateTownless, boolean updateNearby) {
        // Fail-safe
        if (client.player == null || townless == null || nearby() == null) return;

        if (updateTownless) UpdateTownlessState();
        if (updateNearby) UpdateNearbyState();
    }

    public static void Render(MatrixStack ms) {
        if (!instance().shouldRender()) return;

        matrixStack = ms;

        if (config.townless.enabled) RenderTownless(config.townless.presetPositions);
        if (config.nearby.enabled) RenderNearby(config.nearby.presetPositions);
    }

//    public static void sendNews(NewsState pos, NewsData news) {
//        if (news.getID() == currentNewsID) return;
//        currentNewsID = news.getID();
//
//        TextComponent text = Component.text(news.getMsg(), NamedTextColor.AQUA);
//        switch(pos) {
//            case CHAT -> Messaging.send(text);
//            case ACTION_BAR -> Messaging.sendActionBar(text);
//        }
//    }

    private static void RenderTownless(boolean usingPreset) {
        int townlessSize = townless.size();
        int maxLen = config.townless.maxLength;

        int x = townlessState.getX();
        int y = townlessState.getY();

        Formatting playerTextFormatting = Formatting.byName(config.townless.playerTextColour.name());
        Formatting townlessTextFormatting = Formatting.byName(config.townless.headingTextColour.name());
        MutableText townlessText = Text.translatable("text_townless_header", townlessSize).formatted(townlessTextFormatting);

        if (usingPreset) {
            // Draw heading.
            renderer.drawWithShadow(matrixStack, townlessText, x, y - 10, color);

            int index = 0;
            Iterator<String> it = townless.iterator();

            while (it.hasNext()) {
                String townlessName = it.next();

                if (maxLen > 0 && index >= maxLen) {
                    MutableText remainingText = Text.translatable("text_townless_remaining", townlessSize - index).formatted(playerTextFormatting);
                    renderer.drawWithShadow(matrixStack, remainingText, x, y + index*10, color);
                    break;
                }

                MutableText playerName = Text.translatable(townlessName).formatted(playerTextFormatting);
                renderer.drawWithShadow(matrixStack, playerName, x, y + index++*10, color);
            }
        }
        else {
            // Position of the first player, who determines where the list will be.
            int playerOffset = config.townless.yPos;
            int xOffset = config.townless.xPos;

            // Draw heading.
            renderer.drawWithShadow(matrixStack, townlessText, xOffset, playerOffset - 15, color);

            if (townlessSize > 0) {
                int index = 0;
                Iterator<String> it = townless.iterator();

                while (it.hasNext()) {
                    String name = it.next();

                    if (maxLen >= 1) {
                        if (index >= maxLen) {
                            MutableText remainingText = Text.translatable("text_townless_remaining", townlessSize-index).formatted(playerTextFormatting);
                            renderer.drawWithShadow(matrixStack, remainingText, xOffset, playerOffset, color);
                            break;
                        }

                        index++;
                    }

                    MutableText playerName = Text.translatable(name).formatted(playerTextFormatting);
                    renderer.drawWithShadow(matrixStack, playerName, xOffset, playerOffset, color);

                    // Add offset for the next player.
                    playerOffset += 10;
                }
            }
        }
    }

    private static void RenderNearby(boolean usingPreset) {
        int nearbySize = nearby().size();

        Formatting playerTextFormatting = Formatting.byName(config.nearby.playerTextColour.name());
        Formatting nearbyTextFormatting = Formatting.byName(config.nearby.headingTextColour.name());
        MutableText nearbyText = Text.translatable("text_nearby_header", nearbySize).formatted(nearbyTextFormatting);

        if (usingPreset) {
            // Draw heading.
            renderer.drawWithShadow(matrixStack, nearbyText, nearbyState.getX(), nearbyState.getY() - 10, color);

            if (client.player == null) return;
            if (nearbySize >= 1) {
                for (int i = 0; i < nearbySize; i++) {
                    JsonObject currentPlayer = nearby().get(i).getAsJsonObject();

                    JsonElement xElement = currentPlayer.get("x");
                    JsonElement zElement = currentPlayer.get("z");
                    if (xElement == null || zElement == null) continue;

                    String currentPlayerName = currentPlayer.get("name").getAsString();
                    if (currentPlayerName.equals(client.player.getName().getString())) continue;

                    int distance = Math.abs(xElement.getAsInt() - (int) client.player.getX()) +
                            Math.abs(zElement.getAsInt() - (int) client.player.getZ());

                    String prefix = "";

                    if (config.nearby.showRank) {
                        if (!currentPlayer.has("town")) prefix = "(Townless) ";
                        else prefix = "(" + currentPlayer.get("rank").getAsString() + ") ";
                    }

                    MutableText playerText = Text.translatable(prefix + currentPlayerName + ": " + distance + "m").formatted(playerTextFormatting);
                    renderer.drawWithShadow(matrixStack, playerText, nearbyState.getX(), nearbyState.getY() + 10 * i, color);
                }
            }
        }
        else {
            // Position of the first player, who determines where the list will be.
            int playerOffset = config.nearby.yPos;
            int xOffset = config.nearby.xPos;

            // Draw heading.
            renderer.drawWithShadow(matrixStack, nearbyText, xOffset, playerOffset - 15, color);

            if (client.player == null) return;
            if (nearbySize >= 1) {
                for (int i = 0; i < nearbySize; i++) {
                    JsonObject currentPlayer = nearby().get(i).getAsJsonObject();

                    JsonElement xElement = currentPlayer.get("x");
                    JsonElement zElement = currentPlayer.get("z");
                    if (xElement == null || zElement == null) continue;

                    String currentPlayerName = currentPlayer.get("name").getAsString();
                    if (currentPlayerName.equals(client.player.getName().getString())) continue;

                    int distance = Math.abs(xElement.getAsInt() - (int) client.player.getX()) +
                                   Math.abs(zElement.getAsInt() - (int) client.player.getZ());

                    String prefix = "";
                    if (config.nearby.showRank) {
                        if (!currentPlayer.has("town")) prefix = "(Townless) ";
                        else prefix = "(" + currentPlayer.get("rank").getAsString() + ") ";
                    }

                    MutableText playerText = Text.translatable(prefix + currentPlayerName + ": " + distance + "m").formatted(playerTextFormatting);
                    renderer.drawWithShadow(matrixStack, playerText, xOffset, playerOffset, color);

                    // Add 10 pixels to offset. (Where the next player will be rendered)
                    playerOffset += 10;
                }
            }
        }
    }

    private static void UpdateTownlessState() {
        // No advanced positioning, use preset states.
        int townlessLongest, nearbyLongest;

        townlessLongest = Math.max(ModUtils.getLongestElement(townless),
                ModUtils.getTextWidth(Text.translatable("text_townless_header", townless.size())));

        nearbyLongest = Math.max(ModUtils.getNearbyLongestElement(instance().getNearbyPlayers()),
                ModUtils.getTextWidth(Text.translatable("text_nearby_header", nearby().size())));

        int windowHeight = ModUtils.getWindowHeight();
        int windowWidth = ModUtils.getWindowWidth();

        int heightOffset = windowHeight - ModUtils.getTownlessArrayHeight(townless, config.townless.maxLength) - 22;
        int heightHalfOffset = windowHeight / 2 - ModUtils.getTownlessArrayHeight(townless, config.townless.maxLength) / 2;

        int widthOffset = windowWidth - townlessLongest - 5;

        switch (townlessState) {
            case TOP_MIDDLE -> {
                if (nearbyState.equals(State.TOP_MIDDLE))
                    townlessState.setX(windowWidth/2 - (townlessLongest + nearbyLongest) / 2);
                else
                    townlessState.setX(windowWidth/2 - townlessLongest/2);

                townlessState.setY(16);
            }
            case TOP_RIGHT -> {
                townlessState.setX(widthOffset);
                townlessState.setY(ModUtils.getStatusEffectOffset(client.player.getStatusEffects()));
            }
            case LEFT -> {
                townlessState.setX(5);
                townlessState.setY(heightHalfOffset);
            }
            case RIGHT -> {
                townlessState.setX(widthOffset);
                townlessState.setY(heightHalfOffset);
            }
            case BOTTOM_RIGHT -> {
                townlessState.setX(widthOffset);
                townlessState.setY(heightOffset);
            }
            case BOTTOM_LEFT -> {
                townlessState.setX(5);
                townlessState.setY(heightOffset);
            }
            default -> { // Defaults to top left
                townlessState.setX(5);
                townlessState.setY(16);
            }
        }
    }

    private static void UpdateNearbyState() {
        int nearbyLongest, townlessLongest;

        nearbyLongest = Math.max(ModUtils.getNearbyLongestElement(instance().getNearbyPlayers()),
                ModUtils.getTextWidth(Text.translatable("text_nearby_header", nearby().size())));

        townlessLongest = Math.max(ModUtils.getLongestElement(townless),
                ModUtils.getTextWidth(Text.translatable("text_townless_header", townless.size())));

        int windowHeight = ModUtils.getWindowHeight();
        int windowWidth = ModUtils.getWindowWidth();

        int nearbyArrayHeight = ModUtils.getArrayHeight(nearby());
        int windowHeightOffset = windowHeight - nearbyArrayHeight - 10;
        int windowHeightHalfOffset = windowHeight/2 - nearbyArrayHeight/2;

        int xRightOffset = windowWidth - townlessLongest - nearbyLongest - 15;

        switch (nearbyState) {
            case TOP_MIDDLE -> {
                if (townlessState.equals(State.TOP_MIDDLE)) {
                    nearbyState.setX(windowWidth / 2 - (townlessLongest + nearbyLongest) / 2 + townlessLongest + 5);
                    nearbyState.setY(townlessState.getY());
                } else {
                    nearbyState.setX(windowWidth / 2 - nearbyLongest / 2);
                    nearbyState.setY(16);
                }
            }
            case TOP_RIGHT -> {
                if (townlessState.equals(State.TOP_RIGHT)) nearbyState.setX(xRightOffset);
                else nearbyState.setX(windowWidth - nearbyLongest - 5);

                nearbyState.setY(ModUtils.getStatusEffectOffset(client.player.getStatusEffects()));
            }
            case LEFT -> {
                if (townlessState.equals(State.LEFT)) {
                    nearbyState.setX(townlessLongest + 10);
                    nearbyState.setY(townlessState.getY());
                } else {
                    nearbyState.setX(5);
                    nearbyState.setY(windowHeightHalfOffset);
                }
            }
            case RIGHT -> {
                if (townlessState.equals(State.RIGHT)) {
                    nearbyState.setX(xRightOffset);
                    nearbyState.setY(townlessState.getY());
                } else {
                    nearbyState.setX(windowWidth - nearbyLongest - 5);
                    nearbyState.setY(windowHeightHalfOffset);
                }
            }
            case BOTTOM_RIGHT -> {
                if (townlessState.equals(State.BOTTOM_RIGHT)) {
                    nearbyState.setX(xRightOffset);
                    nearbyState.setY(townlessState.getY());
                } else {
                    nearbyState.setX(windowWidth - nearbyLongest - 15);
                    nearbyState.setY(windowHeightOffset);
                }
            }
            case BOTTOM_LEFT -> {
                if (townlessState.equals(State.BOTTOM_LEFT)) {
                    nearbyState.setX(townlessLongest + 15);
                    nearbyState.setY(townlessState.getY());
                } else {
                    nearbyState.setX(5);
                    nearbyState.setY(windowHeightOffset);
                }
            }
            default -> { // Defaults to top left
                if (townlessState.equals(State.TOP_LEFT)) nearbyState.setX(townlessLongest + 15);
                else nearbyState.setX(5);

                nearbyState.setY(16);
            }
        }
    }
}
