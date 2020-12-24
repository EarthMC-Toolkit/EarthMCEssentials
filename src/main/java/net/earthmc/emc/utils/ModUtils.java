package net.earthmc.emc.utils;

import java.net.InetSocketAddress;
import java.util.Collection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.earthmc.emc.EMCMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.ClientConnection;

public class ModUtils
{
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

    public static int getStringWidth(String string)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = client.textRenderer;    

        return renderer.getWidth(string);
    }

    public static int getStringHeight(String string)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = client.textRenderer;  

        return renderer.getStringBoundedHeight(string, 100000);
    }

    public static int getWindowWidth()
    {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getWindow().getScaledWidth();
    }

    public static int getWindowHeight()
    {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getWindow().getScaledHeight();
    }

    public static int getLongestElement(JsonArray array)
    {
        if (array.size() == 0) return 0;

        int longestElement = 0;     
        for (int i = 0; i < array.size(); i++)
        {
            JsonObject currentObj = (JsonObject) array.get(i);
            int currentWidth = getStringWidth(currentObj.get("name").getAsString());
            if (currentWidth > longestElement) longestElement = currentWidth;
        }

        return longestElement;
    }

    public static int getArrayHeight(JsonArray array)
    {
        if (array.size() == 0) return 0;

        int totalLength = 0;
        for (int i = 0; i < array.size(); i++)
        {
            JsonObject currentObj = (JsonObject) array.get(i);
            totalLength += getStringHeight(currentObj.get("name").getAsString());
        }

        return totalLength;
    }

    public static int getTownlessArrayHeight(JsonArray array, int maxLength)
    {
        if (array.size() == 0) return 0;

        int totalLength = 0;
        for (int i = 0; i < array.size(); i++)
        {
            if (i >= maxLength && maxLength != 0)
            {
                String maxLengthString = "And " + (array.size()-i) + " more...";
                totalLength += getStringHeight(maxLengthString);
                return totalLength-10;
            }
            else
            {
                JsonObject currentObj = (JsonObject) array.get(i);
                totalLength += getStringHeight(currentObj.get("name").getAsString());
            }
        }

        return totalLength;
    }

    public static int getNearbyLongestElement(JsonArray array)
    {
        if (array.size() == 0) return 0;

        int longestElement = 0;
        for (int i = 0; i < array.size(); i++)
        {
            JsonObject currentObj = (JsonObject) array.get(i);
            String nearbyTextString = currentObj.get("name").getAsString() + ": " + currentObj.get("x").getAsString() + ", " + currentObj.get("y").getAsString() + ", " + currentObj.get("z").getAsString();
            int currentWidth = getStringWidth(nearbyTextString);
            if (currentWidth > longestElement) longestElement = currentWidth;
        }

        return longestElement;
    }

    public static int getStatusEffectOffset(Collection<StatusEffectInstance> statusEffectInstances)
    {
        if (statusEffectInstances.isEmpty()) return 16;

        int offset = 0;

        for (StatusEffectInstance statusEffectInstance : statusEffectInstances)
        {
            if (statusEffectInstance.shouldShowIcon())
            {
                if (statusEffectInstance.getEffectType().isBeneficial()) offset = Math.max(offset, 36);
                else offset = 64;
            }
        }

        return offset;
    }

    public static String getServerName() {

        String serverName = "";
        try {

            ServerInfo serverInfo = EMCMod.client.getCurrentServerEntry();
            if (serverInfo != null) {
                if (serverInfo.isLocal()) {
                    serverName = serverInfo.name;
                } else {
                    serverName = serverInfo.address;
                }
            } else if (EMCMod.client.isConnectedToRealms()) {
                serverName = "Realms";
            } else {
                ClientPlayNetworkHandler clientPlayNetworkHandler = EMCMod.client.getNetworkHandler();
                ClientConnection clientConnection = clientPlayNetworkHandler.getConnection();
                InetSocketAddress socketAddress = (InetSocketAddress) clientConnection.getAddress();
                serverName = socketAddress.getHostName();
            }
        } catch (Exception exception) {
            System.out.println("EMC Essentials: Error getting serverName");
            exception.printStackTrace();
        }
        return serverName;
    }
}
