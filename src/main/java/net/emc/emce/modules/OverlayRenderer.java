package net.emc.emce.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.NewsData;
import net.emc.emce.object.NewsState;
import net.emc.emce.utils.ModUtils;
import net.emc.emce.utils.ModUtils.State;
import net.emc.emce.utils.Messaging;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class OverlayRenderer {
    private final EarthMCEssentials mod;
    private int townlessX;
    private int townlessY;
    private int nearbyX;
    private int nearbyY;

    // TODO: unused? also duplicated by newsData in main mod class
    private final NewsData news = new NewsData();
    private int currentNewsID = 0;

    public OverlayRenderer(EarthMCEssentials mod) {
        this.mod = mod;
    }

    public void updateStates() {
        updateTownlessState();
        updateNearbyState();
    }

    public void render(MatrixStack stack) {
        if (MinecraftClient.getInstance().player == null || !mod.getConfig().general.enableMod || !EarthMCEssentials.instance().shouldRender())
            return;

        if (mod.getConfig().townless.enabled)
            renderTownless(stack);

        if (mod.getConfig().nearby.enabled && ModUtils.isConnectedToEMC())
            renderNearby(stack);
    }

    public void sendNews(NewsState pos, NewsData news) {
        if (news.getID() == currentNewsID) return;
        currentNewsID = news.getID();

        switch(pos) {
            case CHAT -> Messaging.sendMessage(Component.text(news.getMsg(), NamedTextColor.AQUA));
            case ACTION_BAR -> Messaging.sendActionBar(Component.text(news.getMsg(), NamedTextColor.AQUA));
        }
    }

    private void renderTownless(MatrixStack matrixStack) {
        Formatting townlessTextFormatting = Formatting.byName(mod.getConfig().townless.headingTextColour.name());
        MutableText townlessText = new TranslatableText("text_townless_header", mod.getTownlessPlayers().size()).formatted(townlessTextFormatting);

        // Draw heading.
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, townlessText, townlessX, townlessY - 10, 16777215);

        int rendered = 0;

        for (String name : mod.getTownlessPlayers()) {
            Formatting playerTextFormatting = Formatting.byName(mod.getConfig().townless.playerTextColour.name());

            if (mod.getConfig().townless.maxLength > 0 && rendered >= mod.getConfig().townless.maxLength) {
                MutableText remainingText = new TranslatableText("text_townless_remaining", mod.getTownlessPlayers().size() - rendered).formatted(playerTextFormatting);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, remainingText, townlessX, townlessY + rendered*10, 16777215);
                break;
            }

            MutableText playerName = new LiteralText(name).formatted(playerTextFormatting);
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, playerName, townlessX, townlessY + rendered++*10, 16777215);
        }
    }

    private void updateTownlessState() {
        if (MinecraftClient.getInstance().player == null)
            return;

        if (!mod.getConfig().townless.presetPositions) {
            townlessX = mod.getConfig().townless.xPos;
            townlessY = mod.getConfig().townless.yPos;
            return;
        }

        // No advanced positioning, use preset states.
        int townlessLongest, nearbyLongest;

        townlessLongest = Math.max(ModUtils.getLongestElement(mod.getTownlessPlayers()), ModUtils.getTextWidth(new TranslatableText("text_townless_header", mod.getTownlessPlayers().size())));
        nearbyLongest = Math.max(ModUtils.getNearbyLongestElement(EarthMCEssentials.instance().getNearbyPlayers()), ModUtils.getTextWidth(new TranslatableText("text_nearby_header", mod.getNearbyPlayers().size())));

        State nearbyState = mod.getConfig().nearby.positionState;

        switch (mod.getConfig().townless.positionState) {
            case TOP_MIDDLE -> {
                if (nearbyState.equals(State.TOP_MIDDLE))
                    townlessX = ModUtils.getWindowWidth() / 2 - (townlessLongest + nearbyLongest) / 2;
                else
                    townlessX = ModUtils.getWindowWidth() / 2 - townlessLongest / 2;

                townlessY = 16;
            }
            case TOP_RIGHT -> {
                townlessX = ModUtils.getWindowWidth() - townlessLongest - 5;
                townlessY = ModUtils.getStatusEffectOffset();
            }
            case LEFT -> {
                townlessX = 5;
                townlessY = ModUtils.getWindowHeight() / 2 - ModUtils.getTownlessArrayHeight(mod.getTownlessPlayers(), mod.getConfig().townless.maxLength) / 2;
            }
            case RIGHT -> {
                townlessX = ModUtils.getWindowWidth() - townlessLongest - 5;
                townlessY = ModUtils.getWindowHeight() / 2 - ModUtils.getTownlessArrayHeight(mod.getTownlessPlayers(), mod.getConfig().townless.maxLength) / 2;
            }
            case BOTTOM_RIGHT -> {
                townlessX = ModUtils.getWindowWidth() - townlessLongest - 5;
                townlessY = ModUtils.getWindowHeight() - ModUtils.getTownlessArrayHeight(mod.getTownlessPlayers(), mod.getConfig().townless.maxLength) - 22;
            }
            case BOTTOM_LEFT -> {
                townlessX = 5;
                townlessY = ModUtils.getWindowHeight() - ModUtils.getTownlessArrayHeight(mod.getTownlessPlayers(), mod.getConfig().townless.maxLength) - 22;
            }
            default -> // Defaults to top left
            {
                townlessX = 5;
                townlessY = 16;
            }
        }
    }

    private void renderNearby(MatrixStack matrixStack) {
        if (MinecraftClient.getInstance().player == null)
            return;

        final JsonArray nearby = mod.getNearbyPlayers();

        Formatting nearbyTextFormatting = Formatting.byName(mod.getConfig().nearby.headingTextColour.name());
        MutableText nearbyText = new TranslatableText("text_nearby_header", mod.getNearbyPlayers().size()).formatted(nearbyTextFormatting);

        // Draw heading.
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, nearbyText, nearbyX, nearbyY - 10, 16777215);

        for (int i = 0; i < nearby.size(); i++) {
            JsonObject currentPlayer = nearby.get(i).getAsJsonObject();

            JsonElement xElement = currentPlayer.get("x");
            JsonElement zElement = currentPlayer.get("z");
            if (xElement == null || zElement == null) continue;

            String currentPlayerName = currentPlayer.get("name").getAsString();
            if (currentPlayerName.equals(MinecraftClient.getInstance().player.getName().getString())) continue;

            int distance = Math.abs(xElement.getAsInt() - (int) MinecraftClient.getInstance().player.getX()) +
                    Math.abs(zElement.getAsInt() - (int) MinecraftClient.getInstance().player.getZ());

            String prefix = "";

            if (mod.getConfig().nearby.showRank) {
                if (!currentPlayer.has("town")) prefix = "(Townless) ";
                else prefix = "(" + currentPlayer.get("rank").getAsString() + ") ";
            }

            Formatting playerTextFormatting = Formatting.byName(mod.getConfig().nearby.playerTextColour.name());
            MutableText playerText = new TranslatableText(prefix + currentPlayerName + ": " + distance + "m").formatted(playerTextFormatting);

            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, playerText, nearbyX, nearbyY + 10 * i, 16777215);
        }
    }

    private void updateNearbyState() {
        if (MinecraftClient.getInstance().player == null)
            return;

        if (!mod.getConfig().nearby.presetPositions) {
            nearbyX = mod.getConfig().nearby.xPos;
            nearbyY = mod.getConfig().nearby.yPos;
            return;
        }

        int nearbyLongest, townlessLongest;

        nearbyLongest = Math.max(ModUtils.getNearbyLongestElement(EarthMCEssentials.instance().getNearbyPlayers()), ModUtils.getTextWidth(new TranslatableText("text_nearby_header", mod.getNearbyPlayers().size())));
        townlessLongest = Math.max(ModUtils.getLongestElement(mod.getTownlessPlayers()), ModUtils.getTextWidth(new TranslatableText("text_townless_header", mod.getTownlessPlayers().size())));

        final State townlessState = mod.getConfig().townless.positionState;

        switch (mod.getConfig().nearby.positionState) {
            case TOP_MIDDLE -> {
                if (townlessState.equals(State.TOP_MIDDLE)) {
                    nearbyX = ModUtils.getWindowWidth() / 2 - (townlessLongest + nearbyLongest) / 2 + townlessLongest + 5;
                    nearbyY = townlessY;
                } else {
                    nearbyX = ModUtils.getWindowWidth() / 2 - nearbyLongest / 2;
                    nearbyY = 16;
                }
            }
            case TOP_RIGHT -> {
                if (townlessState.equals(State.TOP_RIGHT))
                    nearbyX = ModUtils.getWindowWidth() - townlessLongest - nearbyLongest - 15;
                else
                    nearbyX = ModUtils.getWindowWidth() - nearbyLongest - 5;

                nearbyY = ModUtils.getStatusEffectOffset();
            }
            case LEFT -> {
                if (townlessState.equals(State.LEFT)) {
                    nearbyX = townlessLongest + 10;
                    nearbyY = townlessY;
                } else {
                    nearbyX = 5;
                    nearbyY = ModUtils.getWindowHeight() / 2 - ModUtils.getArrayHeight(mod.getNearbyPlayers()) / 2;
                }
            }
            case RIGHT -> {
                if (townlessState.equals(State.RIGHT)) {
                    nearbyX = ModUtils.getWindowWidth() - townlessLongest - nearbyLongest - 15;
                    nearbyY = townlessY;
                } else {
                    nearbyX = ModUtils.getWindowWidth() - nearbyLongest - 5;
                    nearbyY = ModUtils.getWindowHeight() / 2 - ModUtils.getArrayHeight(mod.getNearbyPlayers()) / 2;
                }
            }
            case BOTTOM_RIGHT -> {
                if (townlessState.equals(State.BOTTOM_RIGHT)) {
                    nearbyX = ModUtils.getWindowWidth() - townlessLongest - nearbyLongest - 15;
                    nearbyY = townlessY;
                } else {
                    nearbyX = ModUtils.getWindowWidth() - nearbyLongest - 15;
                    nearbyY = ModUtils.getWindowHeight() - ModUtils.getArrayHeight(mod.getNearbyPlayers()) - 10;
                }
            }
            case BOTTOM_LEFT -> {
                if (townlessState.equals(State.BOTTOM_LEFT)) {
                    nearbyX = townlessLongest + 15;
                    nearbyY = townlessY;
                } else {
                    nearbyX = 5;
                    nearbyY = ModUtils.getWindowHeight() - ModUtils.getArrayHeight(mod.getNearbyPlayers()) - 10;
                }
            }
            default -> // Defaults to top left
            {
                if (townlessState.equals(State.TOP_LEFT)) nearbyX = townlessLongest + 15;
                else nearbyX = 5;

                nearbyY = 16;
            }
        }
    }
}
