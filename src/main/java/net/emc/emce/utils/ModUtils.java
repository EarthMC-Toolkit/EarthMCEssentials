package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.emc.emce.EarthMCEssentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

import java.net.InetSocketAddress;
import java.util.Collection;


public class ModUtils {
    private static String serverName;

    public enum ScaleMethod {
        Independent,
        Proportionate
    }

    public enum State
    {
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

        public int getX()
        {
            return posX;
        }
        public int getY()
        {
            return posY;
        }

        public void setX(int x)
        {
            posX = x;
        }
        public void setY(int y)
        {
            posY = y;
        }
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
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getWindow().getScaledWidth();
    }

    public static int getWindowHeight() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getWindow().getScaledHeight();
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

    public static int getArrayHeight(JsonArray array) {
        if (array.size() == 0) return 0;

        int totalLength = 0;
        for (int i = 0; i < array.size(); i++)
            totalLength += getStringHeight(array.get(i).getAsJsonObject().get("name").getAsString());

        return totalLength;
    }

    public static int getTownlessArrayHeight(JsonArray array, int maxLength) {
        if (array.size() == 0) return 0;

        int totalLength = 0;
        for (int i = 0; i < array.size(); i++) {
            if (i >= maxLength && maxLength != 0) {
                String maxLengthString = "And " + (array.size()-i) + " more...";
                totalLength += getStringHeight(maxLengthString);
                return totalLength-10;
            } else {
                totalLength += getStringHeight(array.get(i).getAsJsonObject().get("name").getAsString());
            }
        }
        return totalLength;
    }

    public static int getNearbyLongestElement(JsonArray nearbyResidents) {
        if (nearbyResidents.size() == 0) return 0;

        int longestElement = 0;
        for (int i = 0; i < nearbyResidents.size(); i++) {
            JsonObject currentObj = nearbyResidents.get(i).getAsJsonObject();
            if (EarthMCEssentials.getClientResident() != null && currentObj.get("name").getAsString().equals(EarthMCEssentials.getClientResident().getName()))
                continue;

            int distance = Math.abs(currentObj.get("x").getAsInt() - (int) EarthMCEssentials.getClient().player.getX()) +
                           Math.abs(currentObj.get("z").getAsInt() - (int) EarthMCEssentials.getClient().player.getZ());

            String prefix = "";

            if (EarthMCEssentials.getConfig().nearby.showRank) {
                if (!currentObj.has("town")) prefix = "(Townless) ";
                else prefix = "(" + currentObj.get("rank").getAsString() + ") ";
            }

            MutableText nearbyText = new TranslatableText(prefix + currentObj.get("name").getAsString() + ": " + distance + "m");
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

    public static boolean shouldRender() {
        // Uses endsWith because EMC has 2 valid IPs (earthmc.net & play.earthmc.net)
        if (!serverName.contains("earthmc.net") && EarthMCEssentials.getConfig().general.emcOnly)
            return false;
        else if ((serverName.equals("singleplayer") || serverName.equals("realms")) && EarthMCEssentials.getConfig().general.emcOnly)
            return false;

        return true;
    }

    public static boolean isConnectedToEMC() {
        return serverName.toLowerCase().contains("earthmc.net");
    }

    public static String getServerName() {
        String serverName = "";

        try {
            ServerInfo serverInfo = EarthMCEssentials.getClient().getCurrentServerEntry();

            if (serverInfo != null) {
                if (serverInfo.isLocal())
                    serverName = serverInfo.name;
                else
                    serverName = serverInfo.address;
            }
            else if (EarthMCEssentials.getClient().isConnectedToRealms())
                serverName = "Realms";
            else if (EarthMCEssentials.getClient().isInSingleplayer())
                serverName = "Singleplayer";
            else {
                ClientPlayNetworkHandler clientPlayNetworkHandler = EarthMCEssentials.getClient().getNetworkHandler();

                if (clientPlayNetworkHandler != null) {
                    return ((InetSocketAddress) clientPlayNetworkHandler.getConnection().getAddress()).getHostName();
                }
            }
        } catch (Exception exception) {
            MsgUtils.sendDebugMessage("Error getting serverName.", exception);
        }

        return serverName;
    }

    public static void updateServerName() {
        serverName = getServerName().toLowerCase();
    }

    public static void setServerName(String serverName) {
        ModUtils.serverName = serverName;
    }
}
