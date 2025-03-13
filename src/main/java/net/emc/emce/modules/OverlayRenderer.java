package net.emc.emce.modules;

import io.github.emcw.Squaremap;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.squaremap.entities.SquaremapLocation;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;
import io.github.emcw.squaremap.entities.SquaremapResident;

import net.emc.emce.EMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.utils.ModUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static io.github.emcw.utils.GsonUtil.streamEntries;
import static java.util.stream.Collectors.toMap;

import static net.emc.emce.EMCEssentials.*;
import static net.emc.emce.utils.ModUtils.*;
import static net.minecraft.text.Text.translatable;

@SuppressWarnings("UnusedReturnValue")
public class OverlayRenderer {
    private static MinecraftClient client;
    private static TextRenderer renderer;
    private static DrawContext drawCtx;

    private static ModConfig config;
    private static State townlessState, nearbyState;
    private static List<String> townless = new CopyOnWriteArrayList<>();

    // #FFFFFF (White)
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
        EMCEssentials.instance().setNearbyPlayers(Map.of());
        townless = new CopyOnWriteArrayList<>();
    }

    public static void SetTownless(Set<String> townlessNames) {
        townless = new CopyOnWriteArrayList<>(townlessNames);
    }

    public static void UpdateStates(boolean updateTownless, boolean updateNearby) {
        // Fail-safe
        var nearby = EMCEssentials.instance().getNearbyPlayers();
        if (client.player == null || townless == null || nearby == null) return;

        if (updateTownless) UpdateTownlessState();
        if (updateNearby) UpdateNearbyState();
    }

    public static void RenderAllOverlays(DrawContext ctx, RenderTickCounter tickCounter) {
        if (!EMCEssentials.instance().shouldRender()) return;

        drawCtx = ctx;

        if (config.townless.enabled) RenderTownless(config.townless.presetPositions);
        if (config.nearby.enabled) RenderNearby(config.nearby.presetPositions);
    }

    public static String getRankPrefix(SquaremapOnlinePlayer op) throws MissingEntryException {
        String prefix = "(Townless) ";
        
        if (config.nearby.showRank) {
            Squaremap curMap = EMCEssentials.instance().getCurrentMap();
            SquaremapResident opRes = squaremapPlayerToResident(curMap, op);
            if (opRes != null) {
                prefix = String.format("(%s) ", opRes.getRank());
            }
        }

        return prefix;
    }

    public static int distFromClientPlayer(int x, int z) {
        assert client.player != null;
        return Math.abs(x - (int) client.player.getX()) +
               Math.abs(z - (int) client.player.getZ());
    }

    public static int closest(SquaremapOnlinePlayer p1, SquaremapOnlinePlayer p2) {
        SquaremapLocation loc1 = p1.getLocation();
        Integer dist1 = distFromClientPlayer(loc1.getX(), loc1.getZ());

        SquaremapLocation loc2 = p2.getLocation();
        Integer dist2 = distFromClientPlayer(loc2.getX(), loc2.getZ());

        return dist1.compareTo(dist2);
    }

    public static <K, V> Map<K, V> collectSorted(Stream<Map.Entry<K, V>> entries) {
        return entries.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
    }

    public static @Nullable MutableText prefixedPlayerDistance(@NotNull SquaremapOnlinePlayer player) throws MissingEntryException {
        Formatting playerTextFormatting = Formatting.byName(config.nearby.playerTextColour.name());

        Integer x = player.getLocation().getX();
        Integer z = player.getLocation().getZ();
        if (x == null || z == null) return null;

        String name = player.getName();
        if (name == null || name.equals(clientName())) return null;

        int distance = distFromClientPlayer(x, z);
        String prefix = getRankPrefix(player);

        return translatable(prefix + name + ": " + distance + "m").formatted(playerTextFormatting);
    }

    static Map<String, SquaremapOnlinePlayer> sortByDistance(Map<String, SquaremapOnlinePlayer> players, boolean ascending) {
        return collectSorted(streamEntries(players).sorted((o1, o2) ->
            closest(o1.getValue(), o2.getValue())
        ));
    }

    static Map<String, SquaremapOnlinePlayer> sortByTownless(Map<String, SquaremapOnlinePlayer> players) {
        Map<String, SquaremapOnlinePlayer> townless = EMCEssentials.instance().fetchTownless();
        
        var sorted = players.entrySet().stream().sorted((a, b) -> {
            SquaremapOnlinePlayer opA = a.getValue();
            SquaremapOnlinePlayer opB = b.getValue();
            
            // Check if players are townless by checking if they're in the 'townless' map
            boolean aIsTownless = townless.containsKey(a.getKey());
            boolean bIsTownless = townless.containsKey(b.getKey());
            
            // Both are residents, keep below townless.
            if (!aIsTownless && !bIsTownless) {
                return -1;
            }
            
            // Both are townless, put closest first.
            if (aIsTownless && bIsTownless) {
                return closest(opA, opB);
            }
            
            // One player is townless, other is a resident.
            return Boolean.compare(aIsTownless, bIsTownless);
        });

        return collectSorted(sorted);
    }

    public static int drawWithoutShadow(Text text, int x, int y, int colour) {
        return drawCtx.drawText(renderer, text, x, y, colour, false);
    }

    public static int drawWithShadow(Text text, int x, int y, int colour) {
        return drawCtx.drawTextWithShadow(renderer, text, x, y, colour);
    }

    private static void RenderTownless(boolean usingPreset) {
        int townlessSize = townless.size();
        int maxLen = config.townless.maxLength;

        int x = townlessState.getX();
        int y = townlessState.getY();

        Formatting playerTextFormatting = Formatting.byName(config.townless.playerTextColour.name());
        Formatting townlessTextFormatting = Formatting.byName(config.townless.headingTextColour.name());
        MutableText townlessText = translatable("text_townless_header", townlessSize).formatted(townlessTextFormatting);

        if (usingPreset) {
            drawWithShadow(townlessText, x, y - 10, color);

            int index = 0;

            for (String townlessName : townless) {
                if (maxLen > 0 && index >= maxLen) {
                    MutableText remainingText = translatable("text_townless_remaining", townlessSize - index);

                    remainingText = remainingText.formatted(playerTextFormatting);
                    drawWithShadow(remainingText, x, y + index * 10, color);

                    break;
                }

                MutableText playerName = translatable(townlessName).formatted(playerTextFormatting);
                drawWithShadow(playerName, x, y + index++ * 10, color);
            }
        }
        else {
            // Position of the first player, who determines where the list will be.
            int playerOffset = config.townless.yPos;
            int xOffset = config.townless.xPos;

            // Draw heading.
            drawWithShadow(townlessText, xOffset, playerOffset - 15, color);

            if (townlessSize > 0) {
                int index = 0;

                for (String name : townless) {
                    if (maxLen >= 1) {
                        if (index >= maxLen) {
                            MutableText remainingText = translatable("text_townless_remaining", townlessSize - index);

                            remainingText = remainingText.formatted(playerTextFormatting);
                            drawWithShadow(remainingText, xOffset, playerOffset, color);

                            break;
                        }

                        index++;
                    }

                    MutableText playerName = translatable(name).formatted(playerTextFormatting);
                    drawWithShadow(playerName, xOffset, playerOffset, color);

                    // Add offset for the next player.
                    playerOffset += 10;
                }
            }
        }
    }

    private static void RenderNearby(boolean usingPreset) {
        Map<String, SquaremapOnlinePlayer> nearby = EMCEssentials.instance().getNearbyPlayers();

        switch (config.nearby.nearbySort) {
            case NEAREST -> nearby = sortByDistance(nearby, true);
            case FURTHEST -> nearby = sortByDistance(nearby, false);
            case TOWNLESS -> nearby = sortByTownless(nearby);
        }

        Formatting nearbyTextFormatting = Formatting.byName(config.nearby.headingTextColour.name());
        MutableText nearbyText = translatable("text_nearby_header", nearby.size()).formatted(nearbyTextFormatting);

        if (usingPreset) {
            // Draw heading.
            drawWithShadow(nearbyText, nearbyState.getX(), nearbyState.getY() - 10, color);

            if (client.player == null) return;
            if (!nearby.isEmpty()) {
                MutableText playerText;
                int i = 0;

                for (SquaremapOnlinePlayer curPlayer : nearby.values()) {
                    try {
                        playerText = prefixedPlayerDistance(curPlayer);
                        if (playerText == null) continue;
                    }
                    catch (MissingEntryException e) {
                        continue;
                    }

                    drawWithShadow(playerText, nearbyState.getX(), nearbyState.getY() + 10 * i, color);
                    ++i;
                }
            }
        }
        else {
            // Position of the first player, who determines where the list will be.
            int playerOffset = config.nearby.yPos;
            int xOffset = config.nearby.xPos;

            // Draw heading.
            drawWithShadow(nearbyText, xOffset, playerOffset - 15, color);

            if (client.player == null) return;
            if (!nearby.isEmpty()) {
                MutableText playerText;

                for (SquaremapOnlinePlayer curPlayer : nearby.values()) {
                    try {
                        playerText = prefixedPlayerDistance(curPlayer);
                        if (playerText == null) continue;
                    }
                    catch (MissingEntryException e) {
                        continue;
                    }

                    drawWithShadow(playerText, xOffset, playerOffset, color);

                    // Add 10 pixels to offset. (Where the next player will be rendered)
                    playerOffset += 10;
                }
            }
        }
    }

    private static void UpdateTownlessState() {
        // No advanced positioning, use preset states.
        int townlessLongest, nearbyLongest;

        townlessLongest = Math.max(getLongestElement(townless),
            getTextWidth(translatable("text_townless_header", townless.size()))
        );

        var nearby = EMCEssentials.instance().getNearbyPlayers();
        nearbyLongest = Math.max(getNearbyLongestElement(nearby),
            getTextWidth(translatable("text_nearby_header", nearby.size()))
        );

        int windowHeight = getWindowHeight();
        int windowWidth = getWindowWidth();

        int heightOffset = windowHeight - getTownlessArrayHeight(townless, config.townless.maxLength) - 22;
        int heightHalfOffset = windowHeight / 2 - getTownlessArrayHeight(townless, config.townless.maxLength) / 2;

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
                assert client.player != null;
                townlessState.setY(getStatusEffectOffset(client.player.getStatusEffects()));
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

        var nearby = EMCEssentials.instance().getNearbyPlayers();
        nearbyLongest = Math.max(getNearbyLongestElement(nearby),
            getTextWidth(translatable("text_nearby_header", nearby.size()))
        );

        townlessLongest = Math.max(getLongestElement(townless),
            getTextWidth(translatable("text_townless_header", townless.size()))
        );

        int windowHeight = getWindowHeight();
        int windowWidth = getWindowWidth();

        int nearbyArrayHeight = ModUtils.getArrayHeight(nearby);
        int windowHeightOffset = windowHeight - nearbyArrayHeight - 10;
        int windowHeightHalfOffset = windowHeight / 2 - nearbyArrayHeight / 2;

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

                assert client.player != null;
                nearbyState.setY(getStatusEffectOffset(client.player.getStatusEffects()));
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