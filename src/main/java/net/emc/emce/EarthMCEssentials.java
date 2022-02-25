package net.emc.emce;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.emc.emce.commands.*;
import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.object.Resident;
import net.emc.emce.tasks.TaskScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class EarthMCEssentials implements ModInitializer {

    private static EarthMCEssentials instance;

    private final Logger logger = LogManager.getLogger(EarthMCEssentials.class);

    private Resident clientResident;
    private ModConfig config;
    private boolean shouldRender = false;
    private boolean debugModeEnabled = false;

    private final List<String> townlessResidents = new ArrayList<>();
    private JsonArray nearbyPlayers;

    KeyBinding configKeybinding;

    private final TaskScheduler scheduler = new TaskScheduler();

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

        // Every tick, see if we are pressing F4.
        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            if (configKeybinding.wasPressed()) {
                Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get();
                client.setScreen(configScreen);
		    }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) ->
                OverlayRenderer.SetMatrixStack(matrixStack));

        ScreenEvents.BEFORE_INIT.register((client, newScreen, scaledWidth, scaledHeight) ->
                OverlayRenderer.UpdateStates());

        ScreenEvents.AFTER_INIT.register((client, newScreen, scaledWidth, scaledHeight) ->
        {
            if (newScreen instanceof ClothConfigScreen)
            {
                ScreenExtensions configSE = ScreenExtensions.getExtensions(newScreen);

                configSE.fabric_getRemoveEvent().register(screen ->
                        OverlayRenderer.UpdateStates());
            }
        });
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
        instance.debugModeEnabled = debugModeEnabled;
    }

    public void setShouldRender(boolean shouldRender) {
        instance.shouldRender = shouldRender;
    }

    public void setNearbyPlayers(JsonArray nearbyPlayers) {
        instance.nearbyPlayers = nearbyPlayers;

        OverlayRenderer.Init();
    }

    public void setTownlessResidents(@NotNull JsonArray townlessResidents) {
        this.townlessResidents.clear();

        for (JsonElement townlessResident : townlessResidents)
            this.townlessResidents.add(townlessResident.getAsJsonObject().get("name").getAsString());

        OverlayRenderer.Init();
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