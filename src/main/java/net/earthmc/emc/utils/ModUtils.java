package net.earthmc.emc.utils;

import java.util.Collection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.entity.effect.StatusEffectInstance;

public class ModUtils
{
    Window window = MinecraftClient.getInstance().getWindow();
    int windowWidth = window.getScaledWidth();
    int windowHeight = window.getScaledHeight();

    public enum State
    {
        BOTTOM_LEFT("BOTTOM_LEFT", false),
        BOTTOM_RIGHT("BOTTOM_RIGHT", false),
        LEFT("LEFT", false),
        RIGHT("RIGHT", false),
        TOP_LEFT("TOP_LEFT", false),
        TOP_MIDDLE("TOP_MIDDLE", false),
        TOP_RIGHT("TOP_RIGHT", false);

        private boolean active;
        private final String name;

        State(String name, boolean active) {
            this.name = name;
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean a) {
            active = a;
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

    public static int getTownlessArrayHeigth(JsonArray array, int maxLength)
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
                else offset = Math.max(offset, 64);
            }
        }
        return offset;
    }
}
