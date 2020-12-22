package net.earthmc.emc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;

public class ModUtils
{
    Window window = MinecraftClient.getInstance().getWindow();
    int windowWidth = window.getScaledWidth();
    int windowHeigth = window.getScaledHeight();

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

    public static int getStringHeigth(String string)
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

    public static int getArrayHeigth(JsonArray array)
    {
        if (array.size() == 0) return 0;

        int totalLength = 0;
        for (int i = 0; i < array.size(); i++)
        {
            JsonObject currentObj = (JsonObject) array.get(i);
            totalLength += getStringHeigth(currentObj.get("name").getAsString());
        }

        return totalLength;
    }
}
