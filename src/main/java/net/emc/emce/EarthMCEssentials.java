package net.emc.emce;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.object.NewsData;
import net.emc.emce.object.Resident;
import net.emc.emce.tasks.TaskScheduler;
import net.emc.emce.utils.EventRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EarthMCEssentials implements ModInitializer {

    private static EarthMCEssentials instance;

    private final Logger logger = LogManager.getLogger(EarthMCEssentials.class);

    private Resident clientResident;
    private ModConfig config;
    private boolean shouldRender = false;
    private boolean debugModeEnabled = false;

    private final List<String> townlessResidents = new CopyOnWriteArrayList<>();
    private JsonArray nearbyPlayers = new JsonArray();
    private NewsData newsData = new NewsData();

    public static KeyBinding configKeybinding;

    private final TaskScheduler scheduler = new TaskScheduler();

    @Override
    public void onInitialize()
    {
        instance = this;

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        configKeybinding = KeyBindingHelper.registerKeyBinding(new
                KeyBinding("Open Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        instance().scheduler().start();
        EventRegistry.RegisterClientTick();
        EventRegistry.RegisterCommands();
    }

    public Resident getClientResident() {
        return clientResident;
    }

    public void setClientResident(Resident res) {
        clientResident = res;
    }

    public ModConfig getConfig() {
        if (config == null)
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

       return config;
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public List<String> getTownless() {
        return townlessResidents;
    }

    public JsonArray getNearbyPlayers() {
        return nearbyPlayers;
    }

    public void setDebugModeEnabled(boolean debugModeEnabled) {
        this.debugModeEnabled = debugModeEnabled;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setNews(NewsData nd) {
        this.newsData = nd;
        OverlayRenderer.SendNews(config.news.position, nd);
    }

    public void setNearbyPlayers(JsonArray nearbyPlayers) {
        this.nearbyPlayers = nearbyPlayers;
        OverlayRenderer.UpdateStates(false, true);
    }

    public void setTownlessResidents(@NotNull JsonArray array) {
        // Make sure there is data to add.
        if (array.size() < 1) return;

        townlessResidents.clear();

        for (JsonElement townlessResident : array) {
            townlessResidents.add(townlessResident.getAsJsonObject().get("name").getAsString());
        }

        OverlayRenderer.SetTownless(townlessResidents);
        OverlayRenderer.UpdateStates(true, false);
    }

    public Logger logger() {
        return logger;
    }

    public TaskScheduler scheduler() {
        return scheduler;
    }

    public static EarthMCEssentials instance() {
        return instance;
    }
}