package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;

import static net.emc.emce.EarthMCEssentials.instance;

public class ModUtils {
    private static @NotNull String serverName = "";

    public enum State {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        RIGHT,
        TOP_LEFT,
        TOP_MIDDLE,
        TOP_RIGHT;
    }

    public static int getStringWidth(String string) {
        return MinecraftClient.getInstance().textRenderer.getWidth(string);
    }

    public static int getTextWidth(MutableText text) {
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }

    public static int getStringHeight(String string) {
        return MinecraftClient.getInstance().textRenderer.getWrappedLinesHeight(string, 1000);
    }

    public static int getWindowWidth() {
        return MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    public static int getWindowHeight() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight();
    }

    public static int getLongestElement(JsonArray array) {
        if (array == null || array.size() == 0) return 0;

        int longestElement = 0;     
        for (int i = 0; i < array.size(); i++) {
            int currentWidth = getStringWidth(array.get(i).getAsJsonObject().get("name").getAsString());
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

    public static int getArrayHeight(JsonArray array) {
        if (array.size() == 0) return 0;

        int totalLength = 0;
        for (int i = 0; i < array.size(); i++)
            totalLength += getStringHeight(array.get(i).getAsJsonObject().get("name").getAsString());

        return totalLength;
    }

    public static int getTownlessArrayHeight(Collection<String> townless, int maxLength) {
        if (townless.size() == 0) return 0;

        int totalLength = 0;
        int processed = 0;
        for (String name : townless) {
            if (processed++ >= maxLength && maxLength != 0) {
                String maxLengthString = "And " + (townless.size() - processed) + " more...";
                totalLength += getStringHeight(maxLengthString);
                return totalLength-10;
            } else {
                totalLength += getStringHeight(name);
            }
        }

        return totalLength;
    }

    public static int getNearbyLongestElement(JsonArray nearbyResidents) {
        if (nearbyResidents.size() == 0 || MinecraftClient.getInstance().player == null)
            return 0;

        int longestElement = 0;
        for (int i = 0; i < nearbyResidents.size(); i++) {
            JsonObject currentObj = nearbyResidents.get(i).getAsJsonObject();
            if (currentObj.get("name") == null || currentObj.get("x") == null || currentObj.get("z") == null) continue;
            if (instance().getClientResident() != null && currentObj.get("name").getAsString()
                    .equals(instance().getClientResident().getName())) continue;

            int distance = Math.abs(currentObj.get("x").getAsInt() - MinecraftClient.getInstance().player.getBlockX()) +
                           Math.abs(currentObj.get("z").getAsInt() - MinecraftClient.getInstance().player.getBlockZ());

            String prefix = "";

            if (instance().getConfig().nearby.showRank) {
                if (!currentObj.has("town")) prefix = "(Townless) ";
                else prefix = "(" + currentObj.get("rank").getAsString() + ") ";
            }

            MutableText nearbyText = new TranslatableText(prefix + currentObj.get("name").getAsString() + ": " + distance + "m");
            longestElement = Math.max(getTextWidth(nearbyText), longestElement);
        }

        return longestElement;
    }

    public static int getStatusEffectOffset() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        Collection<StatusEffectInstance> effects = player != null ? player.getStatusEffects() : Collections.emptyList();
        if (effects.isEmpty())
            return 16;

        int offset = 0;
        for (StatusEffectInstance effect : effects) {
            if (effect.shouldShowIcon()) {
                if (effect.getEffectType().isBeneficial()) offset = Math.max(offset, 36);
                else offset = 64;
            }
        }

        return offset;
    }

    public static boolean shouldRender() {
        if (!isConnectedToEMC() && instance().getConfig().general.emcOnly) return false;
        else return (!serverName.equals("singleplayer") && !serverName.equals("realms")) || !instance().getConfig().general.emcOnly;
    }

    public static boolean isConnectedToEMC() { return serverName.toLowerCase().contains("earthmc.net"); }

    public static @NotNull String getServerName() {
        String serverName = "";

        try {
            ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();

            if (serverInfo != null) {
                if (serverInfo.isLocal())
                    serverName = serverInfo.name;
                else
                    serverName = serverInfo.address;
            }
            else if (MinecraftClient.getInstance().isConnectedToRealms())
                serverName = "Realms";
            else if (MinecraftClient.getInstance().isInSingleplayer())
                serverName = "Singleplayer";
            else {
                ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();

                if (clientPlayNetworkHandler != null) {
                    return ((InetSocketAddress) clientPlayNetworkHandler.getConnection().getAddress()).getHostName();
                }
            }
        } catch (Exception exception) {
            Messaging.sendDebugMessage("Error getting serverName.", exception);
        }

        return serverName;
    }

    public static void updateServerName() { serverName = getServerName().toLowerCase(); }

    public static void setServerName(@NotNull String serverName) {
        ModUtils.serverName = serverName;
    }
}
