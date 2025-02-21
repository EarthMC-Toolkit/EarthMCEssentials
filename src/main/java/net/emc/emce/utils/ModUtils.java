package net.emc.emce.utils;

import lombok.Setter;
import net.minecraft.entity.effect.StatusEffect;

import org.jetbrains.annotations.NotNull;

import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;
import io.github.emcw.exceptions.MissingEntryException;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.emc.emce.EarthMCEssentials.clientName;
import static net.emc.emce.modules.OverlayRenderer.dist;
import static net.emc.emce.modules.OverlayRenderer.getRankPrefix;

import static net.minecraft.client.MinecraftClient.getInstance;

public class ModUtils {
    @Setter private static String serverName = null;

    @SuppressWarnings("unused")
    public enum ScaleMethod {
        Independent,
        Proportionate
    }

    public enum NearbySort {
        NEAREST,
        FURTHEST,
        TOWNLESS
//        MAYOR,
//        NATION_LEADER
    }

    public enum State {
        BOTTOM_LEFT(0, 0),
        BOTTOM_RIGHT(0, 0),
        LEFT(0, 0),
        RIGHT(0, 0),
        TOP_LEFT(0, 0),
        TOP_MIDDLE(0, 0),
        TOP_RIGHT(0, 0);

        private int posX;
        private int posY;

        State(int posX, int posY) {
            this.posX = posX;
            this.posY = posY;
        }

        public int getX() {
            return posX;
        }

        public int getY() {
            return posY;
        }

        public void setX(int x) {
            posX = x;
        }

        public void setY(int y) {
            posY = y;
        }
    }

    @SuppressWarnings("unused")
    public static int getStringWidth(String string) { return getInstance().textRenderer.getWidth(string); }
    public static int getTextWidth(MutableText text) { return getInstance().textRenderer.getWidth(text); }
    public static int getStringHeight(String string) { return getInstance().textRenderer.getWrappedLinesHeight(string, 1000); }

    public static int getWindowWidth() { return getInstance().getWindow().getScaledWidth(); }
    public static int getWindowHeight() { return getInstance().getWindow().getScaledHeight(); }

    @SuppressWarnings("unused")
    public static int getLongestElement(Map<String, ?> map) {
        int length = map == null ? 0 : map.size();
        if (length < 1) return 0;

        List<String> keys = new ArrayList<>(map.keySet());

        int longestElement = 0, i = 0;
        for (; i < length; i++) {
            int currentWidth = getStringWidth(keys.get(i));
            longestElement = Math.max(currentWidth, longestElement);
        }

        return longestElement;
    }

    public static int getLongestElement(@NotNull Collection<String> collection) {
        int longestElement = 0;

        for (String string : collection)
            longestElement = Math.max(longestElement, getStringHeight(string));

        return longestElement;
    }

    public static int getArrayHeight(@NotNull Map<String, ?> map) {
        int length = map.size();
        if (length < 1) return 0;

        List<String> keys = new ArrayList<>(map.keySet());

        int totalLength = 0, i = 0;
        for (; i < length; i++) {
            totalLength += getStringHeight(keys.get(i));
        }

        return totalLength;
    }

    public static int getTownlessArrayHeight(@NotNull List<String> townless, int maxLength) {
        int length = townless.size();
        if (length < 1) return 0;

        int totalLength = 0, i = 0;
        for (; i < length; i++) {
            String name = townless.get(i);

            if (i >= maxLength && maxLength != 0) {
                String maxLengthString = "And " + (length-i) + " more...";
                return totalLength + getStringHeight(maxLengthString) - 10;
            }
            else totalLength += getStringHeight(name);
        }

        return totalLength;
    }

    public static int getNearbyLongestElement(@NotNull Map<String, SquaremapOnlinePlayer> nearby) {
        int length = nearby.size();
        if (length < 1) return 0;

        int longestElement = 0;
        for (SquaremapOnlinePlayer curOp : nearby.values()) {
            String name = curOp.getName();
            Integer x = curOp.getLocation().getX();
            Integer z = curOp.getLocation().getZ();

            if (z == null || x == null || name == null) continue;
            if (name.equals(clientName())) continue;

            ClientPlayerEntity player = Objects.requireNonNull(getInstance().player);
            int distance = dist(x, z);

            String prefix;
            try {
                prefix = getRankPrefix(curOp);
            } catch (MissingEntryException e) {
                continue;
            }

            MutableText nearbyText = Text.translatable(prefix + name + ": " + distance + "m");
            longestElement = Math.max(getTextWidth(nearbyText), longestElement);
        }

        return longestElement;
    }

    public static int getStatusEffectOffset(Collection<StatusEffectInstance> statusEffects) {
        if (statusEffects.isEmpty()) return 16;

        int offset = 0;
        for (StatusEffectInstance effect : statusEffects) {
            if (!effect.shouldShowIcon()) continue;

            // A 'beneficial' effect is a positive one as opposed to harmful or neutral.
            StatusEffect effectType = effect.getEffectType().value();
            offset = effectType.isBeneficial() ? Math.max(offset, 36) : 64;
        }

        return offset;
    }

    public static boolean configOpen() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        return screen instanceof ClothConfigScreen;
    }

    public static boolean isConnectedToEMC() {
        return serverName.toLowerCase().endsWith("earthmc.net");
    }

    public static void updateServerName() {
        String curServer = currentServer();
        if (curServer != null) setServerName(curServer);
    }

    @Nullable
    public static String currentServer() {
        try {
            MinecraftClient instance = getInstance();
            ServerInfo serverInfo = instance.getCurrentServerEntry();

            // If the server is Singleplayer, Realms, or LAN, return null
            if (serverInfo == null || serverInfo.isRealm() || instance.isInSingleplayer() || serverInfo.isLocal()) {
                return null;
            }

            // Otherwise, return the server's address (external server)
            return serverInfo.address;
        } catch (Exception e) {
            Messaging.sendDebugMessage("Error getting server name.", e);
            return null;
        }
    }
}