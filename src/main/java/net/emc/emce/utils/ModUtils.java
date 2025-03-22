package net.emc.emce.utils;

import lombok.Setter;
import net.emc.emce.config.ModConfig;
import net.minecraft.entity.effect.StatusEffect;

import org.jetbrains.annotations.NotNull;

import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;
import io.github.emcw.exceptions.MissingEntryException;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.emc.emce.EMCEssentials.clientName;
import static net.emc.emce.modules.OverlayRenderer.distFromClientPlayer;
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
        int len = map == null ? 0 : map.size();
        if (len < 1) return 0;

        List<String> keys = new ArrayList<>(map.keySet());
        
        int i = 0;
        int longestElement = 0;
        
        for (; i < len; i++) {
            int currentWidth = getStringWidth(keys.get(i));
            longestElement = Math.max(currentWidth, longestElement);
        }

        return longestElement;
    }

    public static int getLongestElement(@NotNull Collection<String> collection) {
        int longestElement = 0;
        for (String string : collection) {
            longestElement = Math.max(longestElement, getStringHeight(string));
        }

        return longestElement;
    }

    public static int getArrayHeight(@NotNull Map<String, ?> map) {
        int len = map.size();
        if (len < 1) return 0;

        List<String> keys = new ArrayList<>(map.keySet());

        int i = 0;
        int totalLength = 0;
        
        for (; i < len; i++) {
            totalLength += getStringHeight(keys.get(i));
        }

        return totalLength;
    }

    public static int getTownlessArrayHeight(@NotNull List<String> townless, int maxLength) {
        int len = townless.size();
        if (len < 1) return 0;

        int totalLength = 0, i = 0;
        for (; i < len; i++) {
            String name = townless.get(i);

            if (i >= maxLength && maxLength != 0) {
                String maxLengthString = String.format("And %d more...", (len-i));
                return totalLength + getStringHeight(maxLengthString) - 10;
            }
            else totalLength += getStringHeight(name);
        }

        return totalLength;
    }

    public static int getNearbyLongestElement(@NotNull Map<String, SquaremapOnlinePlayer> nearby) {
        int longestElement = 0;
        for (SquaremapOnlinePlayer nearbyOp : nearby.values()) {
            String name = nearbyOp.getName();
            if (name == null) continue;
            if (name.equals(clientName())) continue;
            
            Integer x = nearbyOp.getLocation().getX();
            Integer z = nearbyOp.getLocation().getZ();
            if (x == null || z == null) continue;

            String prefix;
            try {
                prefix = getRankPrefix(nearbyOp);
            } catch (MissingEntryException e) {
                continue;
            }
            
            int distance = distFromClientPlayer(x, z);
            
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
        return getInstance().currentScreen instanceof ClothConfigScreen;
    }
    
    /**
     * Whether we have a server and the client's config settings allow the mod to run.<br><br>
     *
     * In LAN or Singleplayer, {@code enableInSingleplayer} must be toggled on.
     * On EarthMC, {@code enableMod} must be toggled on.<br><br>
     *
     * For Realms and other multiplayer servers, the mod will never be enabled.
     * @return
     */
    public static boolean enabledOnCurrentServer() {
        if (serverName == null) return false;
        
        ModConfig config = ModConfig.instance();
        if (!config.general.enableMod) return false;
        
        String serverNameLower = serverName.toLowerCase();
        return switch (serverNameLower) {
            case "lan", "singleplayer" -> config.general.enableInSingleplayer;
            default -> serverNameLower.endsWith("earthmc.net");
        };
    }
    
    public static boolean isConnectedToEMC() {
        return serverName.toLowerCase().endsWith("earthmc.net");
    }
    
    public static boolean isInSinglePlayer() {
        return serverName.equalsIgnoreCase("singleplayer");
    }
    
    /**
     * Attempts to get the name of the current <b>multiplayer</b> server if we are on one.
     * Returns {@code null} in the case that we are in Singleplayer, Realms, or LAN.
     */
    @Nullable
    public static String currentServer() {
        try {
            MinecraftClient instance = getInstance();
            if (instance.isInSingleplayer()) return "singleplayer";
            
            ServerInfo serverInfo = instance.getCurrentServerEntry();
            if (serverInfo == null) return null;
            
            if (serverInfo.isRealm()) return "realm";
            if (serverInfo.isLocal()) return "lan";
            
            // Otherwise, return the address of the external server.
            return serverInfo.address;
        } catch (Exception e) {
            Messaging.sendDebugMessage("Error getting server name.", e);
            return null;
        }
    }
    
    //    public static void updateServerName() {
    //        String curServer = currentServer();
    //        if (curServer != null) setServerName(curServer);
    //    }
}