package net.emc.emce;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.EventRegistry;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.modules.TaskScheduler;
import net.emc.emce.utils.Messaging;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class EarthMCEssentials implements ModInitializer {
    private static EarthMCEssentials instance;

    public String mapName = "aurora";
    public EMCWrapper emcw = new EMCWrapper();

    private final Logger logger = LogManager.getLogger(EarthMCEssentials.class);

    private Player clientPlayer = null;
    private boolean shouldRender = false;

    private List<String> townlessNames = new CopyOnWriteArrayList<>();
    private Map<String, Player> nearbyPlayers = new ConcurrentHashMap<>();

    private final TaskScheduler scheduler = new TaskScheduler();

    public static KeyBinding configKeybinding;
    private ModConfig config = null;
    private boolean debugModeEnabled = false;

    @Override
    public void onInitialize() {
        instance = this;

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        initConfig();

        configKeybinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Open Config Menu",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"
        ));

        EventRegistry.RegisterClientTick();
        EventRegistry.RegisterConnection(this);

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

    public Player getClientPlayer() {
        return clientPlayer;
    }

    public void setClientPlayer(Player res) {
        clientPlayer = res;
    }

    public ModConfig config() { return config; }
    public void initConfig() { config = AutoConfig.getConfigHolder(ModConfig.class).getConfig(); }

    public boolean shouldRender() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!config.general.enableMod || client.player == null || client.options.debugEnabled)
            return false;

        return shouldRender;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugModeEnabled = enabled;

        if (enabled) Messaging.sendPrefixed("msg_debug_enabled");
        else Messaging.sendPrefixed("msg_debug_disabled");
    }

    public boolean debugEnabled() {
        return this.debugModeEnabled;
    }

    public List<String> getTownless() {
        return townlessNames;
    }

    public Map<String, Player> getNearbyPlayers() {
        return nearbyPlayers;
    }

    public void setNearbyPlayers(Map<String, Player> nearbyPlayers) {
        this.nearbyPlayers = nearbyPlayers;
        OverlayRenderer.UpdateStates(false, true);
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setTownless(@NotNull Map<String, Player> map) {
        // Make sure there is data to add.
        if (map.size() < 1) return;

        townlessNames.clear();
        townlessNames = GsonUtil.streamValues(map)
                .map(BaseEntity::getName)
                .collect(Collectors.toList());

        OverlayRenderer.SetTownless(townlessNames);
        OverlayRenderer.UpdateStates(true, false);
    }
}