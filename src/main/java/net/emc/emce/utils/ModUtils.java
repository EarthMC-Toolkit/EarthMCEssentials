package net.emc.emce.utils;

import io.github.emcw.entities.Player;
import io.github.emcw.entities.Resident;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.*;

import static net.emc.emce.EarthMCEssentials.instance;
import static net.minecraft.client.MinecraftClient.getInstance;

public class ModUtils {
    private static @NotNull String serverName = "";

    @SuppressWarnings("unused")
    public enum ScaleMethod {
        Independent,
        Proportionate
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

    public static int getNearbyLongestElement(@NotNull Map<String, Player> nearby) {
        int length = nearby.size();
        if (length < 1) return 0;

        int longestElement = 0;
        for (Player curPlayer : nearby.values()) {
            Player clientPlayer = instance().getClientPlayer();

            String name = curPlayer.getName();
            Integer x = curPlayer.getLocation().getX();
            Integer z = curPlayer.getLocation().getZ();

            if (z == null || x == null || name == null) continue;
            if (clientPlayer != null && name.equals(clientPlayer.getName())) continue;

            ClientPlayerEntity player = Objects.requireNonNull(getInstance().player);
            int distance = Math.abs(x - player.getBlockX()) +
                           Math.abs(z - player.getBlockZ());

            String prefix = "";

            if (instance().getConfig().nearby.showRank) {
                if (!curPlayer.isResident()) prefix = "(Townless) ";
                else {
                    Resident curRes = (Resident) curPlayer;
                    prefix = "(" + curRes.getRank() + ") ";
                }
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
            if (effect.shouldShowIcon()) {
                offset = effect.getEffectType().isBeneficial() ? Math.max(offset, 36) : 64;
            }
        }

        return offset;
    }

    public static boolean isConnectedToEMC() {
        return serverName.toLowerCase().contains("earthmc.net");
    }

    public static @NotNull String getServerName() {
        String serverName = "";

        try {
            MinecraftClient instance = getInstance();
            ServerInfo serverInfo = instance.getCurrentServerEntry();

            if (serverInfo != null) {
                if (serverInfo.isLocal()) serverName = serverInfo.name;
                else serverName = serverInfo.address;
            }
            else if (instance.isConnectedToRealms()) serverName = "Realms";
            else if (instance.isInSingleplayer()) serverName = "Singleplayer";
            else {
                ClientPlayNetworkHandler clientPlayNetworkHandler = instance.getNetworkHandler();

                if (clientPlayNetworkHandler != null) {
                    return ((InetSocketAddress) clientPlayNetworkHandler.getConnection().getAddress()).getHostName();
                }
            }
        } catch (Exception e) {
            Messaging.sendDebugMessage("Error getting server name.", e);
        }

        return serverName;
    }

    public static void updateServerName() {
        serverName = getServerName().toLowerCase();
    }

    public static void setServerName(@NotNull String serverName) {
        ModUtils.serverName = serverName;
    }
}
