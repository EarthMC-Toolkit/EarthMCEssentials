package net.emc.emce;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.EventRegistry;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.modules.TaskScheduler;
import net.emc.emce.objects.Resident;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
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

    private Resident clientResident = null;
    private ModConfig config = null;
    private boolean shouldRender = false;
    private boolean debugModeEnabled = false;

    private final List<String> townlessResidents = new CopyOnWriteArrayList<>();
    private JsonArray nearbyPlayers = new JsonArray();
    //private NewsData newsData = new NewsData(null);

    public static KeyBinding configKeybinding;

    private final TaskScheduler scheduler = new TaskScheduler();
    public String mapName = "aurora";

    public int sessionCounter = 0;

    @Override
    public void onInitialize() {
        instance = this;

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        initConfig();

        configKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Config Menu",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        EventRegistry.RegisterClientTick();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                EventRegistry.RegisterCommands(this, dispatcher));
    }

    public static EarthMCEssentials instance() {
        return instance;
    }

    public Logger logger() {
        return logger;
    }

    public TaskScheduler scheduler() {
        return scheduler;
    }

    public Resident getClientResident() {
        return clientResident;
    }

    public void setClientResident(Resident res) {
        clientResident = res;
    }

    private void initConfig() { config = AutoConfig.getConfigHolder(ModConfig.class).getConfig(); }
    public ModConfig getConfig() {
        if (config == null) initConfig();
        return config;
    }

    public boolean shouldRender() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!config.general.enableMod || client.player == null || client.options.debugEnabled)
            return false;

        return shouldRender;
    }

    public void setDebugEnabled(boolean debugModeEnabled) {
        this.debugModeEnabled = debugModeEnabled;
    }

    public boolean debugEnabled() {
        return debugModeEnabled;
    }

    public List<String> getTownless() {
        return townlessResidents;
    }

    public JsonArray getNearbyPlayers() {
        return nearbyPlayers;
    }

    public void setNearbyPlayers(JsonArray nearbyPlayers) {
        this.nearbyPlayers = nearbyPlayers;
        OverlayRenderer.UpdateStates(false, true);
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setTownlessResidents(@NotNull JsonArray array) {
        // Make sure there is data to add.
        if (array.size() < 1) return;

        townlessResidents.clear();

        for (JsonElement res : array) {
            townlessResidents.add(res.getAsJsonObject().get("name").getAsString());
        }

        OverlayRenderer.SetTownless(townlessResidents);
        OverlayRenderer.UpdateStates(true, false);
    }
}