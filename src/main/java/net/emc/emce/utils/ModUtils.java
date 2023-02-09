package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emce.objects.Resident;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static net.emc.emce.EarthMCEssentials.instance;
import static net.minecraft.client.MinecraftClient.getInstance;

public class ModUtils {
    private static @NotNull String serverName = "";

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

    public static int getStringWidth(String string) { return getInstance().textRenderer.getWidth(string); }
    public static int getTextWidth(MutableText text) { return getInstance().textRenderer.getWidth(text); }
    public static int getStringHeight(String string) { return getInstance().textRenderer.getWrappedLinesHeight(string, 1000); }

    public static int getWindowWidth() { return getInstance().getWindow().getScaledWidth(); }
    public static int getWindowHeight() { return getInstance().getWindow().getScaledHeight(); }

    public static String elementAsString(JsonElement el, String name) {
        return el.getAsJsonObject().get(name).getAsString();
    }

    public static int getLongestElement(JsonArray array) {
        int length = array.size();
        if (array == null || length < 1) return 0;

        int longestElement = 0, i = 0;
        for (; i < length; i++) {
            int currentWidth = getStringWidth(elementAsString(array.get(i), "name"));
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
        int length = array.size();
        if (length < 1) return 0;

        int totalLength = 0, i = 0;
        for (; i < length; i++)
            totalLength += getStringHeight(elementAsString(array.get(i), "name"));

        return totalLength;
    }

    public static int getTownlessArrayHeight(List<String> townless, int maxLength) {
        int length = townless.size();
        if (length < 1) return 0;

        int totalLength = 0, i = 0;
        for (; i < length; i++) {
            String name = townless.get(i);

            if (i >= maxLength && maxLength != 0) {
                String maxLengthString = "And " + name + " more...";
                return totalLength + getStringHeight(maxLengthString) - 10;
            }
            else totalLength += getStringHeight(name);
        }

        return totalLength;
    }

    public static int getNearbyLongestElement(JsonArray nearbyResidents) {
        int length = nearbyResidents.size();
        if (length < 1) return 0;

        int longestElement = 0, i = 0;
        for (; i < length; i++) {
            JsonObject currentObj = nearbyResidents.get(i).getAsJsonObject();
            Resident clientRes = instance().getClientResident();

            if (currentObj.get("name") == null || currentObj.get("x") == null || currentObj.get("z") == null) continue;
            if (clientRes != null && currentObj.get("name").getAsString().equals(clientRes.getName())) continue;

            ClientPlayerEntity player = Objects.requireNonNull(getInstance().player);
            int distance = Math.abs(currentObj.get("x").getAsInt() - player.getBlockX()) +
                           Math.abs(currentObj.get("z").getAsInt() - player.getBlockZ());

            String prefix = "";

            if (instance().getConfig().nearby.showRank) {
                if (!currentObj.has("town")) prefix = "(Townless) ";
                else prefix = "(" + currentObj.get("rank").getAsString() + ") ";
            }

            MutableText nearbyText = Text.translatable(prefix + currentObj.get("name").getAsString() + ": " + distance + "m");
            longestElement = Math.max(getTextWidth(nearbyText), longestElement);
        }

        return longestElement;
    }

    public static int getStatusEffectOffset(Collection<StatusEffectInstance> statusEffectInstances) {
        if (statusEffectInstances.isEmpty()) return 16;

        int offset = 0;
        for (StatusEffectInstance statusEffectInstance : statusEffectInstances) {
            if (statusEffectInstance.shouldShowIcon()) {
                if (statusEffectInstance.getEffectType().isBeneficial()) offset = Math.max(offset, 36);
                else offset = 64;
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
            ServerInfo serverInfo = getInstance().getCurrentServerEntry();

            if (serverInfo != null) {
                if (serverInfo.isLocal())
                    serverName = serverInfo.name;
                else
                    serverName = serverInfo.address;
            }
            else if (getInstance().isConnectedToRealms()) serverName = "Realms";
            else if (getInstance().isInSingleplayer()) serverName = "Singleplayer";
            else {
                ClientPlayNetworkHandler clientPlayNetworkHandler = getInstance().getNetworkHandler();

                if (clientPlayNetworkHandler != null) {
                    return ((InetSocketAddress) clientPlayNetworkHandler.getConnection().getAddress()).getHostName();
                }
            }
        } catch (Exception exception) {
            Messaging.sendDebugMessage("Error getting serverName.", exception);
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
