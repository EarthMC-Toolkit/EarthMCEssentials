package net.emc.emce;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.emc.emce.commands.*;
import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.object.Resident;
import net.emc.emce.object.ServerData;
import net.emc.emce.config.ConfigUtils;
import net.emc.emce.tasks.TaskScheduler;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EarthMCEssentials implements ModInitializer {

    private static EarthMCEssentials instance;

    private final Logger logger = LogManager.getLogger(EarthMCEssentials.class);

    private Resident client;
    private ModConfig config;
    private boolean shouldRender = false;
    private boolean debugModeEnabled = false;

    private final List<String> townlessResidents = new ArrayList<>();
    private JsonArray nearbyPlayers;
    private final Map<String, JsonObject> nations = new HashMap<>();
    private final Map<String, JsonObject> towns = new HashMap<>();
    private ServerData serverData;

    KeyBinding configKeybinding;

    private final TaskScheduler scheduler = new TaskScheduler();

    private static final String[] colors = new String[] { "BLUE", "DARK_BLUE", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "RED", "DARK_RED",
                                                    "LIGHT_PURPLE", "DARK_PURPLE", "YELLOW", "GOLD", "GRAY", "DARK_GRAY", "BLACK", "WHITE" };

    @Override
    public void onInitialize() {

        instance = this;

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        configKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        nearbyPlayers = new JsonArray();

        // Register client-sided commands.
        InfoCommands.registerNationInfoCommand();
        InfoCommands.registerTownInfoCommand();
        NearbyCommand.register();
        NetherCommand.register();
        QueueCommand.register();
        ToggleDebugCommand.register();
        TownlessCommand.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Pressed F4 (Config Menu)
            if (configKeybinding.wasPressed()) {
                Screen screen = ConfigUtils.getConfigBuilder().build();

                client.setScreen(screen);
		    }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> OverlayRenderer.render(matrixStack));
    }

    public Resident getClientResident() {
        return client;
    }

    public void setClientResident(Resident clientResident) {
        client = clientResident;
    }

    public ModConfig getConfig() {
        return config;
    }

    public String[] getColors() {
        return colors;
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

    public Map<String, JsonObject> getNations() {
        return nations;
    }

    public Map<String, JsonObject> getTowns() {
        return towns;
    }

    public JsonArray getNearbyPlayers() {
        return nearbyPlayers;
    }

    public void setDebugModeEnabled(boolean debugModeEnabled) {
        EarthMCEssentials.instance().debugModeEnabled = debugModeEnabled;
    }

    public void setShouldRender(boolean shouldRender) {
        EarthMCEssentials.instance().shouldRender = shouldRender;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public void setServerData(ServerData serverData) {
        EarthMCEssentials.instance().serverData = serverData;
    }

    public void setNations(JsonArray nations) {
        this.nations.clear();

        for (JsonElement nation : nations) {
            JsonObject object = nation.getAsJsonObject();

            this.nations.put(object.get("name").getAsString().toLowerCase(Locale.ROOT), object);
        }

        MsgUtils.sendDebugMessage("Updated nations, array size: " + this.nations.size());
    }

    public void setNearbyPlayers(JsonArray nearbyPlayers) {
        EarthMCEssentials.instance().nearbyPlayers = nearbyPlayers;
    }

    public void setTowns(@NotNull JsonArray towns) {
        this.towns.clear();

        for (JsonElement town : towns) {
            JsonObject object = town.getAsJsonObject();

            this.towns.put(object.get("name").getAsString().toLowerCase(Locale.ROOT), object);
        }

        MsgUtils.sendDebugMessage("Updated towns, array size: " + this.towns.size());
    }

    public void setTownlessResidents(@NotNull JsonArray townlessResidents) {
        this.townlessResidents.clear();

        for (JsonElement townlessResident : townlessResidents)
            this.townlessResidents.add(townlessResident.getAsJsonObject().get("name").getAsString());
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