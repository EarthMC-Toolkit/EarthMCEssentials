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
import net.emc.emce.utils.Commands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

public class EarthMCEssentials implements ModInitializer {

    private static EarthMCEssentials instance;

    private final Logger logger = LogManager.getLogger(EarthMCEssentials.class);

    private Resident clientResident;
    private boolean shouldRender = false;
    private boolean debugModeEnabled = false;

    private final Set<String> townlessResidents = new ConcurrentSkipListSet<>();
    private JsonArray nearbyPlayers = new JsonArray();
    // TODO: unused?
    private NewsData newsData = new NewsData();

    private final TaskScheduler scheduler = new TaskScheduler();
    private final OverlayRenderer overlayRenderer = new OverlayRenderer(this);

    @Override
    public void onInitialize() {
        instance = this;

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        overlayRenderer.updateStates();

        this.scheduler().start();
        Commands.register(this);
        registerEvents();
    }

    private void registerEvents() {
        KeyBinding configKeybinding = KeyBindingHelper.registerKeyBinding(new
                KeyBinding("Open Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeybinding.wasPressed()) {
                Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get();
                client.setScreen(configScreen);
                ScreenEvents.remove(configScreen).register(screen -> overlayRenderer.updateStates());
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> overlayRenderer.render(matrixStack));
    }

    public Resident getClientResident() {
        return clientResident;
    }

    // TODO: unused?
    public void setClientResident(Resident res) {
        clientResident = res;
    }

    public ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    @NotNull
    public Set<String> getTownlessPlayers() {
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
        overlayRenderer.sendNews(getConfig().news.position, nd);
    }

    public void setNearbyPlayers(JsonArray nearbyPlayers) {
        this.nearbyPlayers = nearbyPlayers;
        overlayRenderer.updateStates();
    }

    public void setTownlessResidents(@NotNull JsonArray array) {
        // Make sure there is data to add.
        if (array.size() < 1) return;

        townlessResidents.clear();

        for (JsonElement townlessResident : array)
            townlessResidents.add(townlessResident.getAsJsonObject().get("name").getAsString());

        overlayRenderer.updateStates();
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